package com.m_and_a_company.canelatube.ui.svdn

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.m_and_a_company.canelatube.R
import com.m_and_a_company.canelatube.controllers.ViewModelFactory
import com.m_and_a_company.canelatube.databinding.ActivityDownloadBinding
import com.m_and_a_company.canelatube.databinding.BottomSheetDownloadBinding
import com.m_and_a_company.canelatube.domain.network.model.Format
import com.m_and_a_company.canelatube.ui.SelectToDownloadTypeDialog
import com.m_and_a_company.canelatube.ui.Utils
import com.m_and_a_company.canelatube.ui.enums.StepsView
import com.m_and_a_company.canelatube.ui.enums.TypeDownload
import com.m_and_a_company.canelatube.ui.listeners.OnSelectTypeDownload
import com.m_and_a_company.canelatube.ui.modals.LoaderModal
import com.m_and_a_company.canelatube.ui.modals.ModalAnimation
import com.m_and_a_company.canelatube.ui.svdn.DownloadFormatsAdapter.DownloadFormatsAdapterListener
import com.m_and_a_company.canelatube.ui.svdn.DownloadUIState.ClearState.getMessageFromErrors
import com.squareup.picasso.Picasso
import com.tapadoo.alerter.Alerter

class DownloadActivity : AppCompatActivity(), OnSelectTypeDownload, DownloadFormatsAdapterListener {

    private var readPermission = false
    private var writePermission = false
    private var needShowSettingsModal = false
    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

    private lateinit var settingsActivity: ActivityResultLauncher<Intent>
    private var fromSettingsActivity = false

    companion object {
        const val GRIDS_COLUMNS = 3
        lateinit var urlToDownload: String
        var itagDownload = 0
    }

    private lateinit var binding: ActivityDownloadBinding
    private val loader: LoaderModal by lazy {
        LoaderModal(this)
    }

    private var alerterBigSong: Alerter? = null
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
                settingsActivity.launch(createSettingsIntent())
                isOpenSettingsConf = true
            }, ::cancelButton)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        urlToDownload = intent.extras?.getString(Intent.EXTRA_TEXT) ?: ""
        binding = ActivityDownloadBinding.inflate(layoutInflater)
        viewModel.viewState.observe(this, ::render)
        setContentView(binding.root)
        requestPermissions()
        createSettingsIntent()
    }

    override fun onAccept(type: TypeDownload) {
        getListDownload(type)
    }

    override fun onFormatClicked(format: Format) {
        itagDownload = format.itag
        stepFlag = StepsView.SELECTED_SONG_DOWNLOAD
        validateCanDownloadSong()
    }

    private fun updateOrRequestPermissions() {
        val hasReadPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        ) == PackageManager.PERMISSION_GRANTED

        val hasWritePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ) == PackageManager.PERMISSION_GRANTED
        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        readPermission = hasReadPermission
        writePermission = hasWritePermission || minSdk29
        val permissionToRequest = mutableListOf<String>()
        if (!writePermission) {
            permissionToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!readPermission) {
            permissionToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (permissionToRequest.isNotEmpty()) {
            permissionsLauncher.launch(permissionToRequest.toTypedArray())
        } else {
            setupView()
        }
    }

    private fun render(state: DownloadUIState) {
        when(state) {
            DownloadUIState.Loading -> {
                loader.show()
                bottomSheet.setOnDismissListener(null)
                bottomSheet.dismiss()
                alerterBigSong = null
            }
            is DownloadUIState.Success -> {
                setupBottomSheet { setupDataInBottom(state) }
            }
            is DownloadUIState.SuccessGetSongId -> {
                viewModel.downloadSong(
                    state.song.id
                )
                finishAffinity()
            }
            is DownloadUIState.Error -> {
                setupBottomSheet { setupErrorInBottom(state) }
            }
            else -> {
                Utils.toastMessage(applicationContext, "Action unknown")
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
        loader.dismiss()
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

            state.warningMessage?.let { warning ->
                alerterBigSong = Utils.alertTop(bottomSheet, warning, getString(R.string.lbl_message_has_warning_long_song))
                alerterBigSong?.show()
            }
        }
    }

    private fun setupErrorInBottom(state: DownloadUIState.Error) {
        bottomSheetBinding.apply {
            bsAcceptBtn.visibility = View.VISIBLE
            bsDownloadImageView.visibility = View.GONE
            bsDownloadRv.visibility = View.GONE
            modalAnimationView.apply {
                visibility = View.VISIBLE
                setAnimation(R.raw.error_rabbit)
                repeatCount = LottieDrawable.INFINITE
                repeatMode = LottieDrawable.RESTART
                playAnimation()
            }
            bsDownloadTitleAuthorTv.text = state.message
            bsAcceptBtn.setOnClickListener { accept() }
            val errorText = state.getMessageFromErrors()
            if (errorText != null) {
                bsDownloadLinkShowErrorDetails.visibility = View.VISIBLE
                bsDownloadLinkShowErrorDetails.setOnClickListener {
                    Utils.alertDialog(
                        ctx = this@DownloadActivity,
                        title = getString(R.string.lbl_error_details_error_title),
                        message = errorText,
                        positiveAction = { },
                        negativeAction = { },
                        enbledPositiveAction = false
                    )
                }
            }
        }
    }

    private fun setupView() {
        selectTypeDownloadDialog.show()
    }

    private fun getListDownload(type: TypeDownload) {
        when (type) {
            TypeDownload.AUDIO -> {
                loader.show()
                if (urlToDownload.isEmpty()) {
                    Utils.toastMessage(
                        applicationContext,
                        getString(R.string.download_no_url_entered)
                    )
                }
                viewModel.getInfoSongFromUrl(urlToDownload)
            }
            TypeDownload.UNDEFINED -> {
                Utils.toastMessage(
                    applicationContext,
                    getString(R.string.type_download_select_type_no_valid)
                )
            }
        }
    }

    private fun requestPermissions() {
        permissionsLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
            ::onResultPermission
        )
        settingsActivity = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ::onResultSettingsActivity
        )
        updateOrRequestPermissions()

    }

    /**
     * Manejador de resultado de la requisicion de permisos
     * @param [mapPermission] contiene el nombre del permiso y si fue aceptado
     */
    private fun onResultPermission(mapPermission: Map<String, Boolean>) {
        readPermission = mapPermission[Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermission
        writePermission =
            mapPermission[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: writePermission
        val canRequestWritePermission = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val canRequestReadPermission = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        needShowSettingsModal = !canRequestReadPermission || !canRequestWritePermission

        when (stepFlag) {
            StepsView.REQUIRED_PERMISSION -> setupView()
            StepsView.SELECTED_SONG_DOWNLOAD -> validateCanDownloadSong()
        }

    }

    private fun createSettingsIntent() =
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }

    /**
     * Manejador de resultado de abrir la configuracion de la app
     * @param [result] Resultado de la actividad
     */
    private fun onResultSettingsActivity(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_CANCELED) {
            fromSettingsActivity = true
            updateOrRequestPermissions()
        }
    }

    private fun validateCanDownloadSong() {
        if (writePermission && itagDownload != 0) {
            if (stepFlag == StepsView.SELECTED_SONG_DOWNLOAD) {
                viewModel.getIdSong(urlToDownload, itagDownload)
            }
        } else {
            if (!needShowSettingsModal) {
                permissionsModal.show()
            } else {
                sendSettingsModal.show()
            }
        }
    }

    /**
     * Funcion que se ejecuta cuando se accepta el modal del dialogo de permisos
     */
    private fun acceptButton() {
        updateOrRequestPermissions()
    }

    /**
     * Funcion que se ejecuta cuando se cancela el modal del dialogo de permisos
     */
    private fun cancelButton() {
        Utils.toastMessage(applicationContext, getString(R.string.permission_denied))
        finish()
    }

    private fun accept() {
        bottomSheet.dismiss()
        finishAffinity()
    }

}