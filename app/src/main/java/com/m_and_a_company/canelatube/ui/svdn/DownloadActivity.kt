package com.m_and_a_company.canelatube.ui.svdn

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.m_and_a_company.canelatube.R
import com.m_and_a_company.canelatube.controllers.ViewModelFactory
import com.m_and_a_company.canelatube.databinding.ActivityDownloadBinding
import com.m_and_a_company.canelatube.databinding.BottomSheetDownloadBinding
import com.m_and_a_company.canelatube.enviroment.requestActiveStorageManager
import com.m_and_a_company.canelatube.enviroment.verifyPermissionReadExternalStorage
import com.m_and_a_company.canelatube.enviroment.verifyPermissionsWriteExternalStorage
import com.m_and_a_company.canelatube.network.domain.model.Format
import com.m_and_a_company.canelatube.ui.SelectToDownloadTypeDialog
import com.m_and_a_company.canelatube.ui.Utils
import com.m_and_a_company.canelatube.ui.enums.StepsView
import com.m_and_a_company.canelatube.ui.enums.TypeDownload
import com.m_and_a_company.canelatube.ui.listeners.OnSelectTypeDownload
import com.m_and_a_company.canelatube.ui.modals.LoaderModal
import com.m_and_a_company.canelatube.ui.modals.ModalAnimation
import com.m_and_a_company.canelatube.ui.svdn.DownloadFormatsAdapter.DownloadFormatsAdapterListener
import com.squareup.picasso.Picasso

class DownloadActivity : AppCompatActivity(), OnSelectTypeDownload, DownloadFormatsAdapterListener {

    companion object {
        const val GRIDS_COLUMNS = 3
        lateinit var urlToDownload: String
    }

    private lateinit var binding: ActivityDownloadBinding
    private val loader: LoaderModal by lazy {
        LoaderModal(this)
    }

    private val callbackWritePermissions =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
            ::onWritePermissionResult
        )

    private val callbackReadPermissions =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
            ::onReadPermissionResult
        )

    private val callbackEnableStorageManager =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ::onEnableStorageManagerResult
        )

    private val bottomSheet by lazy { BottomSheetDialog(this) }
    private var stepFlag: StepsView = StepsView.REQUIRED_PERMISSION
    private var isOpenSettingsConf = false
    private val bottomSheetBinding by lazy { BottomSheetDownloadBinding.inflate(layoutInflater) }
    private val viewModel: DownloadViewModel by viewModels { viewModelFactory }
    private val selectTypeDownloadDialog by lazy {
        SelectToDownloadTypeDialog(
            this, this
        )
    }

    private val viewModelFactory by lazy {
        ViewModelFactory.providesViewModelFactory(
            applicationContext
        )
    }

    private val permissionsModal by lazy {
        ModalAnimation(this).apply {
            setAnimation(R.raw.rabbit_moon)
            setTitleAndDesc(
                getString(R.string.permissions_required_title),
                getString(R.string.permissions_required_message)
            )
            setCallback(::acceptButton, ::cancelButton)
        }
    }

    private val sendSettingsModal by lazy {
        permissionsModal.apply {
            setTitleAndDesc(
                getString(R.string.not_has_permissions_title),
                getString(R.string.not_has_permissions_body)
            )
            setCallback({
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
                isOpenSettingsConf = true
            }, ::cancelButton)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        urlToDownload = intent.extras?.getString(Intent.EXTRA_TEXT) ?: ""
        binding = ActivityDownloadBinding.inflate(layoutInflater)
        viewModel.viewState.observe(this, onChangeView)
        setContentView(binding.root)
        requestPermissions()
    }

    override fun onResume() {
        if (!verifyPermissionReadExternalStorage(this) && isOpenSettingsConf) {
            Utils.toastMessage(applicationContext, getString(R.string.permissions_not_set))
        }
        super.onResume()
    }

    override fun onAccept(type: TypeDownload) {
        getListDownload(type)
    }

    override fun onCancel() {
        Utils.toastMessage(applicationContext, getString(R.string.download_cancel_message))
        finish()
    }

    override fun onFormatClicked(format: Format) {
        stepFlag = StepsView.SELECTED_SONG_DOWNLOAD
        if (verifyPermissionReadExternalStorage(applicationContext) && verifyPermissionsWriteExternalStorage(
                applicationContext
            )
        ) {

            viewModel.getIdSong(urlToDownload, format.itag)
        } else {
            onReadPermissionResult(verifyPermissionReadExternalStorage(applicationContext))
        }
    }

    private val onChangeView = Observer<DownloadUIState> { state ->
        loader.dismiss()
        when (state) {
            DownloadUIState.Loading -> {
                loader.show()
                bottomSheet.setOnDismissListener(null)
                bottomSheet.dismiss()
            }
            is DownloadUIState.Success -> {
                setupBottomSheet { setupDataInBottom(state) }
            }
            is DownloadUIState.SuccessGetSongId -> {
                viewModel.downloadSong(state.song.id)
                finishAffinity()
            }
            is DownloadUIState.Error -> {
                setupBottomSheet { setupErrorInBottom(state) }
            }
        }
    }

    private fun setupBottomSheet(lambda: () -> Unit) {
        bottomSheet.apply {
            setContentView(bottomSheetBinding.root)
            setOnDismissListener { dialogInterface ->
                dialogInterface.dismiss()
                Utils.toastMessage(applicationContext, getString(R.string.download_cancel_message))
                finishAffinity()
            }
            lambda()
            show()
        }
    }

    private fun setupDataInBottom(state: DownloadUIState.Success) {
        bottomSheetBinding.apply {
            bsDownloadImageView.visibility = View.VISIBLE
            bsDownloadTitleAuthorTv.text = state.musicDownloadsModel.title
            Picasso.get().load(state.musicDownloadsModel.thumbnail).into(bsDownloadImageView)
            val adapter = DownloadFormatsAdapter().apply {
                setOnClickListener(this@DownloadActivity)
                setFormats(state.musicDownloadsModel.formats)
            }
            bsDownloadRv.adapter = adapter
            bsDownloadRv.layoutManager = GridLayoutManager(applicationContext, GRIDS_COLUMNS)
        }
    }

    private fun setupErrorInBottom(state: DownloadUIState.Error) {
        bottomSheetBinding.apply {
            modalAnimationView.apply {
                visibility = View.VISIBLE
                setAnimation(R.raw.error_rabbit)
                repeatCount = LottieDrawable.INFINITE
                repeatMode = LottieDrawable.RESTART
                playAnimation()
            }
            bsDownloadTitleAuthorTv.text = state.message
            if (state.errors != null) {
                Utils.toastMessage(applicationContext, Utils.buildMessageError(state.errors))
            }
        }
    }

    private fun setupView() {
        selectTypeDownloadDialog.show()
    }

    private fun getListDownload(type: TypeDownload) {
        when (type) {
            TypeDownload.VIDEO -> {
                Utils.alertDialog(
                    this,
                    getString(R.string.type_download_select_type_video_title),
                    getString(R.string.type_download_select_type_video_message),
                    {
                        selectTypeDownloadDialog.show()
                    },
                    {
                        Utils.toastMessage(applicationContext, getString(R.string.download_cancel_message))
                        finish()
                    })
            }
            TypeDownload.AUDIO -> {
                loader.show()
                if (urlToDownload.isEmpty()) {
                    Utils.toastMessage(applicationContext, getString(R.string.download_no_url_entered))
                }
                viewModel.getInfoSongFromUrl(urlToDownload)
            }
            TypeDownload.UNDEFINED -> {
                Utils.toastMessage(applicationContext, getString(R.string.type_download_select_type_no_valid))
            }
        }
    }

    private fun requestPermissions() {
        if (!verifyPermissionReadExternalStorage(this)) {
            permissionsModal.show()
        } else {
            setupView()
        }
    }


    /**
     * Funcion que se ejecuta cuando se accepta el modal del dialogo de permisos
     */
    private fun acceptButton() {
        callbackReadPermissions.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    /**
     * Funcion que se ejecuta cuando se cancela el modal del dialogo de permisos
     */
    private fun cancelButton() {
        Utils.toastMessage(applicationContext, getString(R.string.permission_denied))
        finish()
    }

    /**
     * Dependiendo de la versión de Android, se solicita permiso de escritura
     * si es mayor a 29, se solicita permiso de lectura
     * si es mayor a 30, se solicita permiso de activar el administrador de almacenamiento
     * @param accept[Boolean] si se aceptó o no el permiso
     */
    private fun onReadPermissionResult(accept: Boolean?) {
        //si esto es true se puede mostrar el dilog de permisos
        val cantShowModalPermission = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        accept?.let { granted ->
            //si no se aceptaron los permisos y el paso es cuando se piden los permisos
            // se muestra muestra la vista de seleccion de cancion
            if (!granted && stepFlag == StepsView.REQUIRED_PERMISSION) {
                setupView()
                return
            }

            //si se puede mostrar el modal de los permisos y se encuentra en la seleccion de la cancion
            // muestra el modal que los requiere
            if (cantShowModalPermission && stepFlag == StepsView.SELECTED_SONG_DOWNLOAD && !granted) {
                permissionsModal.show()
                return
            }

            // si no se puede mostrar el modal de permisos y se encuentra en el paso de seleccionar la cancion
            // y los permisos no fueron aceptados se muestra el modal que envia a configuracion de la app
            if (!cantShowModalPermission && stepFlag == StepsView.SELECTED_SONG_DOWNLOAD && !granted) {
                sendSettingsModal.show()
                return
            }

            if(granted) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    callbackEnableStorageManager.launch(requestActiveStorageManager(this))
                } else {
                    callbackWritePermissions.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
        }
    }

    /**
     * Escucha el resultado de la solicitud del StorageManager
     * @param activityResult[ActivityResult] resultado de la actividad
     */
    private fun onEnableStorageManagerResult(activityResult: ActivityResult?) {
        if(!verifyPermissionsWriteExternalStorage(this)) {
            Utils.toastMessage(this, getString(R.string.permission_not_set_storage_manager))
        } else  {
            Utils.toastMessage(this, getString(R.string.download_press_message))
        }
    }

    /**
     * Escucha el resultado de la solicitud del permiso de escritura
     * @param accept[Boolean] si se aceptó o no el permiso
     */
    private fun onWritePermissionResult(accept: Boolean?) {
        accept?.let { granted ->
            if (!granted) {
                Utils.toastMessage(applicationContext, getString(R.string.permission_denied))
                finish()
            } else {
                setupView()
            }
        }
    }

}