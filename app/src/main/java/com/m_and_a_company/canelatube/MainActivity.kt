package com.m_and_a_company.canelatube

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.app.RecoverableSecurityException
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.m_and_a_company.canelatube.controllers.ViewModelFactory
import com.m_and_a_company.canelatube.databinding.ActivityMainBinding
import com.m_and_a_company.canelatube.databinding.BottomSheetDownloadBinding
import com.m_and_a_company.canelatube.domain.data.models.SongDownloaded
import com.m_and_a_company.canelatube.domain.network.enum.TypeError
import com.m_and_a_company.canelatube.environment.Notifications
import com.m_and_a_company.canelatube.environment.isUpApi29
import com.m_and_a_company.canelatube.ui.Utils
import com.m_and_a_company.canelatube.ui.about.OurActivity
import com.m_and_a_company.canelatube.ui.home.HomeViewModel
import com.m_and_a_company.canelatube.ui.home.SongsDownloadedAdapter
import com.m_and_a_company.canelatube.ui.modals.LoaderModal
import com.m_and_a_company.canelatube.ui.modals.ModalAnimation
import com.m_and_a_company.canelatube.ui.svdn.DownloadUIState
import com.m_and_a_company.canelatube.ui.svdn.DownloadUIState.ClearState.getMessageFromErrors
import java.io.File


class MainActivity : AppCompatActivity(), DownloadedSongsAdapter.SelectedSongDownloadedListener {

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
            setCallback({
                updateOrRequestPermissions(isCallFromSettings = false)
            }, ::cancelButton)
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

    //viewModels
    private val viewModelFactory by lazy {
        ViewModelFactory.providesViewModelFactory(
            applicationContext
        )
    }
    private val viewModel: HomeViewModel by viewModels { viewModelFactory }

    //view
    private lateinit var binding: ActivityMainBinding
    private lateinit var songsDownloadedAdapter: SongsDownloadedAdapter

    //other variables
    private var mSong: SongDownloaded? = null
    private var mSelectedSongPosition = -1
    private var mActionDetailDownloads = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_other_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_menu_our -> {
                openOurScreen()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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

    @SuppressLint("SourceLockedOrientationActivity")
    private fun initView() {
        //set orientation activity in portrait
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding.apply {
            fgHomeRvSongs.layoutManager = LinearLayoutManager(applicationContext)
            intent.action?.let {
                mActionDetailDownloads = it == Notifications.OPEN_DETAIL_DOWNLOADS
            }
        }
        updateOrRequestPermissions(isCallFromSettings = false)
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
                else -> {
                    Utils.toastMessage(applicationContext, getString(R.string.lbl_state_unknow))
                }
            }
        }

        viewModel.songsDownloaded.observe(this) { songsDownloaded ->
            setSongs(songsDownloaded)
            if (mSelectedSongPosition != -1) {
                songsDownloadedAdapter.notifyItemRemoved(mSelectedSongPosition)
            }
        }

        if (readPermission) {
            viewModel.hasSongsDownloaded(contentResolver)
        }
        Notifications(applicationContext).createChannelNotification()
    }

    override fun onSelectedSongDownload(uri: Uri) {
        val playSong = Intent(Intent.ACTION_VIEW).apply { setDataAndType(uri, "audio/*") }
        startActivity(Intent.createChooser(playSong, "Reproducir con: "))
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
            showSongsDownloadedNoPermission()

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            needShowSettingsModal = (!readPermission && !canRequestReadPermission)
        } else {
            if (permissionTimesRequests >= 2) {
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
        with(binding) {
            fgHomeTvEmpty.text = getString(R.string.title_permission_not_aviable)
            binding.fgHomeLlEmptyMessage.visibility = View.VISIBLE
            fgHomeAnimEmpty.apply {
                setAnimation(R.raw.empty_box_downloadeds)
                repeatMode = LottieDrawable.RESTART
                repeatCount = 3
                playAnimation()
            }
            fgHomeRvSongs.visibility = View.GONE
        }
    }

    private fun showSongsDownloaded() {
        viewModel.getFilesDownloaded(contentResolver)
    }

    private fun setErrorView(state: DownloadUIState.Error) {
        if (state.type == TypeError.INTERNET_OR_SERVER) {
            val snack = Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG)
            snack.view.setBackgroundResource(R.color.red_pastel_video_background)
            snack.setTextColor(Color.BLACK)
            snack.show()
        } else {
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
        showSongsDownloadedNoPermission()
    }


    private fun modalSettingsAccept() {
        settingsActivity.launch(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        })
    }

    private fun setSongs(songs: List<SongDownloaded>) {
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
            binding.fgHomeLlEmptyMessage.visibility = View.GONE
            binding.fgHomeRvSongs.visibility = View.VISIBLE
            songsDownloadedAdapter =
                SongsDownloadedAdapter(songs, !mActionDetailDownloads) { songSelected, position ->
                    mSong = songSelected
                    mSelectedSongPosition = position
                    Utils.alertDialog(
                        this,
                        getString(R.string.title_delete_song_alert),
                        getString(R.string.message_delete_song_alert),
                        { deleteDownloadedSong() },
                        {},
                        true
                    )
                }
            songsDownloadedAdapter.setOnClickSongDownloadedListener(this@MainActivity)
            binding.fgHomeRvSongs.adapter = songsDownloadedAdapter
        }
    }

    private fun clearState() {
        viewModel.clearState()
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
                contentResolveInstance.delete(uri, "${MediaStore.Audio.Media._ID} = ?", arrayOf(mSong?.id.toString()))
                Utils.toastMessage(applicationContext, "Se elimino correctamente")
                viewModel.removeItem(mSelectedSongPosition)
                mSelectedSongPosition = -1
                uri.path?.let {
                    val file = File(it)
                    file.delete()
                    if (file.exists()) {
                        file.canonicalFile.delete()
                        if(file.exists()) {
                            applicationContext.deleteFile(file.name)
                        }
                    }
                }
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

    private fun openOurScreen() {
        startActivity(Intent(applicationContext, OurActivity::class.java))
    }

}