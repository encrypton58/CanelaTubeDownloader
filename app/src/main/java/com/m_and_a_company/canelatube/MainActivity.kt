package com.m_and_a_company.canelatube

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.app.RecoverableSecurityException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.provider.Settings
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.m_and_a_company.canelatube.controllers.ViewModelFactory
import com.m_and_a_company.canelatube.databinding.ActivityMainBinding
import com.m_and_a_company.canelatube.databinding.BottomSheetDownloadBinding
import com.m_and_a_company.canelatube.databinding.ShowSongsDownloadedBinding
import com.m_and_a_company.canelatube.domain.data.models.SongDownloaded
import com.m_and_a_company.canelatube.domain.network.enum.TypeError
import com.m_and_a_company.canelatube.domain.network.model.Song
import com.m_and_a_company.canelatube.enviroment.Notifications
import com.m_and_a_company.canelatube.enviroment.isUpApi29
import com.m_and_a_company.canelatube.ui.Utils
import com.m_and_a_company.canelatube.ui.home.HomeViewModel
import com.m_and_a_company.canelatube.ui.home.SongsServerAdapter
import com.m_and_a_company.canelatube.ui.modals.LoaderModal
import com.m_and_a_company.canelatube.ui.modals.ModalAnimation
import com.m_and_a_company.canelatube.ui.svdn.DownloadUIState
import com.m_and_a_company.canelatube.ui.svdn.DownloadUIState.ClearState.getMessageFromErrors

class MainActivity : AppCompatActivity(), SongsServerAdapter.ActionsSongServer, DownloadedSongsAdapter.SelectedSongDownloadedListener {

    //Permissions variables
    private var readPermission = false
    private var writePermission = false
    private var needShowSettingsModal = false
    private var permissionTimesRequests = 0
    private val callbackReadPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        ::onReadPermissionResult
    )

    //Activities Results
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var settingsActivity: ActivityResultLauncher<Intent>
    private val songDeleteIntentLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                Utils.toastMessage(applicationContext, "Se elimino correctamente")
                viewModel.removeItem(mSelectedSongPosition)
                mSelectedSongPosition = -1
            }
        }

    //modals
    private val mLoader by lazy { LoaderModal(this) }
    private val permissionsRequire by lazy {
        ModalAnimation(this).apply {
            setAnimation(R.raw.rabbit_moon)
            setTitleAndDesc(
                getString(R.string.permissions_required_title),
                getString(R.string.permissions_required_message)
            )
            setCallback(::showDownloadsBottomSheet, ::cancelButton)
        }
    }
    private val sendSettingsModal by lazy {
        ModalAnimation(this).apply {
            setAnimation(R.raw.rabbit_moon)
            setTitleAndDesc(
                getString(R.string.not_has_permissions_title),
                getString(R.string.not_has_permissions_body)
            )
            setCallback(::modalSettingsAccept, ::cancelButton)
        }
    }
    private val mDeleteModal by lazy {
        ModalAnimation(this).apply {
            setAnimation(R.raw.delete_animation)
            setTitleAndDesc(
                "Se elimino",
                "Se elimino la cancion del sevidor correctamente"
            )
            setCallback({ viewModel.getSongs(true) }, {})
        }
    }

    //viewModels
    private val viewModelFactory by lazy {
        ViewModelFactory.providesViewModelFactory(
            applicationContext
        )
    }
    private val viewModel: HomeViewModel by viewModels { viewModelFactory }

    //view
    private lateinit var binding: ActivityMainBinding
    private lateinit var mDownloadedSongsAdapter: DownloadedSongsAdapter
    private lateinit var mSongsServerAdapter: SongsServerAdapter
    private val bottomSheetBinding by lazy { ShowSongsDownloadedBinding.inflate(layoutInflater) }
    private val bottomSheetDialog by lazy {
        BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme).apply {
            setContentView(bottomSheetBinding.root)
        }
    }

    //other variables
    private var mSong: SongDownloaded? = null
    private var mSelectedSongPosition = -1
    private var mHasSongDownloaded = false
    private var mActionDetailDownloads = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        init()
    }

    override fun onDownloadSong(id: Int) {
        Utils.toastMessage(applicationContext, "Comienza la descarga")
        viewModel.downloadSong(id, false)
    }

    override fun onDeleteSongSever(id: Int, position: Int) {
        viewModel.deleteSong(id, position)
    }

    private fun onReadPermissionResult(accept: Boolean?) {
        accept?.let { granted ->
            if (granted) {
                showSongsDownloaded()
            } else {
                showSongsDownloadedNoPermission()
            }
        }
    }

    private fun initView() {
        binding.apply {
            fgHomeRvSongs.layoutManager = LinearLayoutManager(applicationContext)
            fgHomeFabDownloads.setOnClickListener { showDownloadsBottomSheet() }
            intent.action?.let { mActionDetailDownloads = it == Notifications.OPEN_DETAIL_DOWNLOADS }
            if (mActionDetailDownloads) {
                fgHomeFabDownloads.performClick()
            }
        }
    }

    private fun init() {
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
            ::onPermissionResult
        )
        settingsActivity =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                updateOrRequestPermissions(isCallFromSettings = true)
            }
        viewModel.statePermission.observe(this, ::onStatePermission)
        viewModel.state.observe(this) {
            mLoader.dismiss()
            when (it) {
                is DownloadUIState.Error -> setErrorView(it)
                DownloadUIState.Loading -> mLoader.show()
                is DownloadUIState.SuccessSongs -> setSongs(it.songs, false)
                is DownloadUIState.SuccessDelete -> deleteSong(it.isDelete, it.positionRemove)
                else -> {
                    Utils.toastMessage(applicationContext, getString(R.string.lbl_state_unknow))
                }
            }
        }

        viewModel.songsDownloaded.observe(this) { songsDownloaded ->
            setAdapterAndShowBottomSheet(songsDownloaded)
            if (mSelectedSongPosition != -1) {
                mDownloadedSongsAdapter.removeItem(mSelectedSongPosition)
            }
        }

        if(readPermission) {
            mHasSongDownloaded = viewModel.hasSongsDownloaded(contentResolver)
        }
        viewModel.getSongs(emitLoader = !mActionDetailDownloads)
    }

    override fun onSelecteSongDownload(uri: Uri) {
        val playSong = Intent(Intent.ACTION_VIEW).apply { setDataAndType(uri, "audio/*") }
        startActivity(Intent.createChooser(playSong, "Reproducir con: "))
    }

    private fun setAdapterAndShowBottomSheet(songs: List<SongDownloaded>) {
        mDownloadedSongsAdapter = DownloadedSongsAdapter(songs.reversed()) { songSelected, position ->
            mSong = songSelected
            mSelectedSongPosition = position
        }.apply {
            setOnClickSongDownloadedListener(this@MainActivity)
            setPopUpItemListener(popUpMenuListener())
        }
        bottomSheetBinding.showSongDownloadedRv.adapter = mDownloadedSongsAdapter
    }

    private fun onStatePermission(b: Boolean?) {
        b?.let { granted ->
            if (granted) {
                showSongsDownloaded()
            } else {
                callbackReadPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun showDownloadsBottomSheet() {
        updateOrRequestPermissions(isCallFromSettings = false)
    }

    private fun updateOrRequestPermissions(isCallFromSettings: Boolean) {
        val hasReadPermission = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val hasWritePermission = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        readPermission = hasReadPermission
        writePermission = hasWritePermission || minSdk29

        if (isCallFromSettings && !hasReadPermission) {
            Utils.toastMessage(applicationContext, "No se hizo cambios en los permisos")
        }

        val permissionToRequest = mutableListOf<String>()
        if (!writePermission) {
            permissionToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!readPermission) {
            permissionToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (permissionToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionToRequest.toTypedArray())
        } else {
            showSongsDownloaded()
        }
    }

    private fun onPermissionResult(permissions: Map<String, Boolean>) {
        readPermission = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermission
        writePermission = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: writePermission
        setNeedShowSettingsModal()
        validatePermissionToGo()
    }

    private fun setNeedShowSettingsModal() {
        permissionTimesRequests += 1

        val canRequestReadPermission = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            needShowSettingsModal = (!readPermission && !canRequestReadPermission)
        }else{
            if(permissionTimesRequests >= 2) {
                needShowSettingsModal = true
            }
        }
    }

    private fun validatePermissionToGo() {
        if (needShowSettingsModal) {
            sendSettingsModal.show()
        } else if (!readPermission || !writePermission) {
            permissionsRequire.show()
        } else {
            showSongsDownloaded()
        }
    }

    private fun showSongsDownloadedNoPermission() {
        bottomSheetBinding.showSongDownloadedTitle.text = getString(R.string.title_permission_not_aviable)
        bottomSheetDialog.show()
    }

    private fun showSongsDownloaded() {
        mHasSongDownloaded = viewModel.hasSongsDownloaded(contentResolver)
        viewModel.getFilesDownloaded(contentResolver)
        if(mHasSongDownloaded){
            bottomSheetBinding.showSongDownloadedContainerNothing.visibility = View.GONE
            bottomSheetBinding.showSongDownloadedRv.visibility = View.VISIBLE
            bottomSheetBinding.showSongDownloadedTitle.text = getString(R.string.title_downloads_song)
            bottomSheetBinding.showSongDownloadedRv.layoutManager =
                LinearLayoutManager(applicationContext)
        }else{
            bottomSheetBinding.showSongDownloadedContainerNothing.visibility = View.VISIBLE
            bottomSheetBinding.showSongDownloadedTitle.text = getString(R.string.title_downloads_song_empty)
            bottomSheetBinding.showSongDownloadedAnimEmpty.setAnimation(R.raw.empty_box_downloadeds)
            bottomSheetBinding.showSongDownloadedRv.visibility = View.GONE
        }
        bottomSheetDialog.show()
    }

    private fun setErrorView(state: DownloadUIState.Error) {
        setSongs(arrayListOf(), true)
        if(state.type == TypeError.INTERNET_OR_SERVER) {
            val snack = Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG)
            snack.view.setBackgroundResource(R.color.red_pastel_video_background)
            snack.setTextColor(Color.BLACK)
            snack.show()
        }else {
            val bottomSheetDownloadBinding = BottomSheetDownloadBinding.inflate(layoutInflater)
            val bottomSheetDialog = BottomSheetDialog(this)
            bottomSheetDialog.setContentView(bottomSheetDownloadBinding.root)
            bottomSheetDownloadBinding.apply {
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
                bsAcceptBtn.setOnClickListener { bottomSheetDialog.dismiss(); clearState() }
                val errorText = state.getMessageFromErrors()
                if (errorText != null) {
                    bsDownloadLinkShowErrorDetails.visibility = View.VISIBLE
                    bsDownloadLinkShowErrorDetails.setOnClickListener {
                        Utils.alertDialog(
                            this@MainActivity,
                            title = "Mostrando Errores",
                            message = errorText,
                            {},
                            {},
                            enbledPositiveAction = false
                        )
                    }
                }
            }
            bottomSheetDialog.show()
        }
    }

    /**
     * Funcion que se ejecuta cuando se cancela el modal del dialogo de permisos
     */
    private fun cancelButton() {
        Utils.toastMessage(applicationContext, getString(R.string.permission_denied))
    }


    private fun modalSettingsAccept() {
        settingsActivity.launch(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        })
    }

    private fun setSongs(songs: List<Song>, isError: Boolean) {

        if (isError) {
            binding.fgHomeTvEmpty.text = getString(R.string.title_empty_message_error)
        } else {
            binding.fgHomeTvEmpty.text = getString(R.string.title_empty_message)
        }

        if (songs.isEmpty()) {
            binding.fgHomeLlEmptyMessage.visibility = View.VISIBLE
            binding.fgHomeAnimEmpty.apply {
                setAnimation(R.raw.empty_box_downloadeds)
                repeatMode = LottieDrawable.RESTART
                repeatCount = 3
                playAnimation()

            }
            binding.fgHomeRvSongs.visibility = View.GONE
        } else {
            mSongsServerAdapter = SongsServerAdapter(actionSongListener = this, songs, mActionDetailDownloads)
            binding.fgHomeRvSongs.adapter = mSongsServerAdapter
        }
    }

    private fun deleteSong(isDeleteSong: Boolean, positionRemove: Int) {
        if(isDeleteSong) {
            mSongsServerAdapter.removeSongItem(binding.fgHomeRvSongs.findViewHolderForLayoutPosition(positionRemove)?.itemView!!, positionRemove)
            mDeleteModal.show()
        }
    }

    private fun clearState() {
        viewModel.clearState()
    }

    private fun popUpMenuListener() = PopupMenu.OnMenuItemClickListener {
        when (it.itemId) {

            R.id.options_file_menu_delete -> {

                Utils.alertDialog(this,
                    getString(R.string.title_delete_song_alert),
                    getString(R.string.message_delete_song_alert),
                    { deleteDownloadedSong() },
                    {},
                    true
                )
                false

            }

            else -> true
        }
    }

    private fun deleteDownloadedSong() {
        mSong?.let { song ->
            deleteSong(songDeleteIntentLauncher, song.uri)
        } ?: Utils.toastMessage(applicationContext, getString(R.string.lbl_not_song_for_delete))
    }

    @SuppressLint("NewApi")
    private fun deleteSong(
        activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>,
        uri: Uri
    ) {
        val contentResolveInstance = applicationContext.contentResolver
        var pendingIntent: PendingIntent? = null
        val deleteList = listOf(uri)

        try {

            if (isUpApi29()) {
                pendingIntent = MediaStore.createDeleteRequest(contentResolver, deleteList)
            } else {
                contentResolveInstance.delete(uri, "${Media._ID} = ?", arrayOf(mSong?.id.toString()))
                Utils.toastMessage(applicationContext, "Se elimino correctamente")
                viewModel.removeItem(mSelectedSongPosition)
                mSelectedSongPosition = -1
            }


        } catch (e: SecurityException) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                if (e is RecoverableSecurityException) {
                    pendingIntent = e.userAction.actionIntent
                }

            } else {
                Utils.toastMessage(
                    applicationContext,
                    getString(R.string.title_message_error_on_delete_song)
                )
            }

        } finally {

            if (pendingIntent != null) {
                val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent).build()
                activityResultLauncher.launch(intentSenderRequest)
            }

        }
    }

}