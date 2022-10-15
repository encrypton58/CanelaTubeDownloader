package com.m_and_a_company.canelatube.ui.svdn

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.m_and_a_company.canelatube.R
import com.m_and_a_company.canelatube.controllers.ViewModelFactory
import com.m_and_a_company.canelatube.databinding.ActivityDownloadBinding
import com.m_and_a_company.canelatube.databinding.BottomSheetDownloadBinding
import com.m_and_a_company.canelatube.enviroment.requestActiveStorageManager
import com.m_and_a_company.canelatube.enviroment.verifyPermissionReadExternalStorage
import com.m_and_a_company.canelatube.network.domain.model.Format
import com.m_and_a_company.canelatube.ui.SelectToDownloadTypeDialog
import com.m_and_a_company.canelatube.ui.Utils
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
    private val bottomSheetBinding by lazy { BottomSheetDownloadBinding.inflate(layoutInflater) }
    private val viewModelFactory by lazy {
        ViewModelFactory.providesViewModelFactory(
            applicationContext
        )
    }
    private val viewModel: DownloadViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        urlToDownload = intent.extras?.getString(Intent.EXTRA_TEXT) ?: ""
        binding = ActivityDownloadBinding.inflate(layoutInflater)
        viewModel.viewState.observe(this, onChangeView)
        setContentView(binding.root)
        requestPermissions()
    }

    override fun onAccept(type: TypeDownload) {
        getListDownload(type)
        loader.show()
    }

    override fun onCancel() {
        Utils.toastMessage(this, "Has cancelado la descarga")
        finish()
    }

    override fun onFormatClicked(format: Format) {
        viewModel.getIdSong(urlToDownload, format.itag)
    }


    private val onChangeView = Observer<DownloadUIState> {
        when (it) {
            DownloadUIState.Loading -> TODO()
            is DownloadUIState.Success -> {
                loader.dismiss()
                setupBottomSheet(it)
            }
            is DownloadUIState.SuccessGetSongId -> {
                bottomSheet.setOnDismissListener(null)
                bottomSheet.dismiss()
                viewModel.downloadSong(it.song.id)
            }
        }
    }

    private fun setupBottomSheet(downloadUIState: DownloadUIState.Success) {
        bottomSheet.apply {
            setContentView(bottomSheetBinding.root)
            setOnDismissListener {
                finishAffinity()
            }
            setupDataInBottom(downloadUIState)
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

    private fun setupView() {
        SelectToDownloadTypeDialog(this, this).show()
    }

    private fun getListDownload(type: TypeDownload) {
        when (type) {
            TypeDownload.VIDEO -> {

            }
            TypeDownload.AUDIO -> {
                if (urlToDownload.isEmpty()) {
                    Utils.toastMessage(this, "No se ha podido obtener la url del video")
                }
                viewModel.getInfoSongFromUrl(urlToDownload)
            }
            TypeDownload.UNDEFINED -> {
                Utils.toastMessage(this, "No se ha seleccionado un tipo de descarga")
            }
        }
    }

    private fun requestPermissions() {
        if (!verifyPermissionReadExternalStorage(this)) {
            ModalAnimation(this).apply {
                setAnimation(R.raw.rabbit_moon)
                setTitleAndDesc(
                    getString(R.string.permissions_required_title),
                    getString(R.string.permissions_required_message)
                )
                setCallback(::acceptButton, ::cancelButton)
                show()
            }
        } else {
            setupView()
        }
    }

    private fun acceptButton() {
        callbackReadPermissions.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun cancelButton() {
        Utils.toastMessage(this, getString(R.string.permission_denied))
        finish()
    }

    /**
     * Dependiendo de la versión de Android, se solicita permiso de escritura
     * si es mayor a 29, se solicita permiso de lectura
     * si es mayor a 30, se solicita permiso de activar el administrador de almacenamiento
     * @param accept[Boolean] si se aceptó o no el permiso
     */
    private fun onReadPermissionResult(accept: Boolean?) {
        accept?.let {
            if (it) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    callbackEnableStorageManager.launch(requestActiveStorageManager(this))
                } else {
                    callbackWritePermissions.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
                setupView()
            }
        }
    }

    /**
     * Escucha el resultado de la solicitud del StorageManager
     * @param result[ActivityResult] resultado de la actividad
     */
    private fun onEnableStorageManagerResult(activityResult: ActivityResult?) {}

    /**
     * Escucha el resultado de la solicitud del permiso de escritura
     * @param accept[Boolean] si se aceptó o no el permiso
     */
    private fun onWritePermissionResult(accept: Boolean?) {}

}