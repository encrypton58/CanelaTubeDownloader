package com.m_and_a_company.canelatube.ui.svdn

import android.Manifest
import android.animation.Animator
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.m_and_a_company.canelatube.R
import com.m_and_a_company.canelatube.controllers.ViewModelFactory
import com.m_and_a_company.canelatube.databinding.ActivitySvdnBinding
import com.m_and_a_company.canelatube.databinding.SvdnBottomSheetBinding
import com.m_and_a_company.canelatube.domain.data.models.SongInfo
import com.m_and_a_company.canelatube.domain.data.models.VideoInfo
import com.m_and_a_company.canelatube.domain.network.model.RequestErrors
import com.m_and_a_company.canelatube.enviroment.*
import com.m_and_a_company.canelatube.ui.SelectToDownloadTypeDialog
import com.m_and_a_company.canelatube.ui.Utils
import com.m_and_a_company.canelatube.ui.enums.TypeDownload
import com.squareup.picasso.Picasso

class SVDN : AppCompatActivity(), DialogInterface.OnDismissListener {

    companion object {
        const val GRID_COLUMNS = 3
        var idSongDownload = 0
    }

    private val requestPermissionWriteExternalStorage =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    @SuppressLint("NewApi")
    private val requestPermissionReadExternalStorage =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if(it) {
                if (verifyPermissionsWriteExternalStorage(applicationContext).contains(VERSION_LESS_R)) {
                    requestPermissionWriteExternalStorage.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }else{
                    resultFromEnableStorageManager.launch(requestActiveStorageManager(this))
                }
            }else{
                Utils.toastMessage(applicationContext, "Permisos requerido ")
            }
        }

    private val resultFromEnableStorageManager =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {

        }

    private lateinit var mBinding: ActivitySvdnBinding
    private val svdnBottomSheet by lazy { BottomSheetDialog(this) }
    private val svdnBottomSheetBinding by lazy { SvdnBottomSheetBinding.inflate(layoutInflater) }
    private val viewModelFactory by lazy { ViewModelFactory.providesViewModelFactory(applicationContext) }
    private val viewModel: SVDNViewModel by viewModels { viewModelFactory }
    private lateinit var urlSong: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySvdnBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        getUrlSong()
        initViews()
        requestPermissions()
        initViewModelObservers()
    }

    private val uiState = Observer<SongInfoUIState> { viewState ->
        when (viewState) {
            is SongInfoUIState.Loading -> {}
            is SongInfoUIState.SuccessToGetInfo -> setSuccessRequestView(viewState.songInfo)
            is SongInfoUIState.Error -> setErrorRequestView(viewState.error)
            is SongInfoUIState.SuccessPrepareToDownload -> setSuccessPrepareToDownload()
            is SongInfoUIState.SuccessToGetInfoVideo -> setSuccessRequestView(viewState.videoInfo)
        }
    }

    private val typeDownload = Observer<TypeDownload> { typeDownload ->
        if(urlSong.isNotEmpty()){
            when (typeDownload) {
                TypeDownload.AUDIO -> {
                    initSVDNBottomSheet()
                    viewModel.getInfoSongFromUrl(urlSong)
                }
                TypeDownload.VIDEO -> {
                    initSVDNBottomSheet()
                    viewModel.getInfoVideoFromUrl(urlSong)
                }
                TypeDownload.UNDEFINED -> {
                    Utils.toastMessage(applicationContext, "No Se ha Seleccionado el tipo de descarga")
                }
                else -> {
                    Utils.toastMessage(applicationContext, "No Se ha Seleccionado el tipo de descarga")
                }
            }
        }else{
            Utils.toastMessage(applicationContext, "No se ha ingresado una url")
        }
    }

    private fun initViews() {
        SelectToDownloadTypeDialog(this, viewModel.selectToDownloadListener)
    }

    private fun requestPermissions() {
        if (!verifyPermissionReadExternalStorage(applicationContext)) {
            Utils.alertDialog(
                this,
                getString(R.string.permissions_required_title),
                getString(R.string.permissions_required_message),
                positiveAction = { positiveActionPermissions() },
                negativeAction = { negativeActionPermissions() })
        }
    }

    private fun initViewModelObservers() {
        viewModel.songViewState.observe(this, uiState)
        viewModel.typeDownloadViewState.observe(this, typeDownload)
    }

    private fun getUrlSong() {
        val extras = intent.extras
        if (extras != null) {
            urlSong = extras.getString(Intent.EXTRA_TEXT)!!
        }
    }

    private fun initSVDNBottomSheet() {
        svdnBottomSheet.apply {
            setContentView(svdnBottomSheetBinding.root)
            setOnDismissListener(this@SVDN)
        }
        svdnBottomSheetBinding.apply {
            svdnRetryConnectButton.setOnClickListener(retryGetInfoSong())
        }

        deviceHaveInternetConnection(application).apply {
            if (!this) {
                setNotNetworkView()
            }
        }
        svdnBottomSheet.show()
    }

    private fun setNotNetworkView() {
        svdnBottomSheetBinding.apply {
            setShimmerHide(true)
            svdnShimmerViewContainerDownFormats.visibility = View.GONE
            changeColorCardBackground(svdnCardViewImage, R.color.white)
            svdnAnimationView.apply {
                visibility = View.VISIBLE
                setAnimation(R.raw.no_internet_rabbit)
                repeatMode = LottieDrawable.REVERSE
                repeatCount = LottieDrawable.INFINITE
                playAnimation()
            }
            changeColorCardBackground(cardContainerText, R.color.canela_variant)
            svdnTitleAuthorTv.text = getString(R.string.no_have_internet_connection)
            svdnRetryConnectButton.visibility = View.VISIBLE
        }
    }

    private fun setSuccessRequestView(songInfo: SongInfo) {
        viewModel.typeDownloadViewState.value
        val adapter = FormatsAdapter(onFormatItemClick(), viewModel.typeDownloadViewState.value!!).apply { setFormats(songInfo.formats) }
        setShimmerHide(true)
        svdnBottomSheetBinding.apply {
            svdnImageView.apply {
                visibility = View.VISIBLE
                Picasso.get().load(songInfo.thumbnail).into(svdnImageView)
            }
            svdnShimmerViewContainerDownFormats.visibility = View.GONE
            svdnTitleAuthorTv.text = getString(R.string.set_info_song_data, songInfo.title)
            changeColorCardBackground(cardContainerText, R.color.canela_variant)
            svdnDownFormatsRv.layoutManager = GridLayoutManager(applicationContext, GRID_COLUMNS)
            svdnDownFormatsRv.adapter = adapter
            svdnContainerDownFormats.visibility = View.VISIBLE
        }
    }

    private fun setSuccessRequestView(videoInfo: VideoInfo) {
        val adapter = FormatsAdapter(onFormatItemClick(), viewModel.typeDownloadViewState.value!!).apply { setVideoFormats(videoInfo.formats) }
        setShimmerHide(true)
        svdnBottomSheetBinding.apply {
            svdnImageView.apply {
                visibility = View.VISIBLE
                Picasso.get().load(videoInfo.thumbnail).into(svdnImageView)
            }
            svdnShimmerViewContainerDownFormats.visibility = View.GONE
            svdnTitleAuthorTv.text = getString(R.string.set_info_song_data, videoInfo.title)
            changeColorCardBackground(cardContainerText, R.color.canela_variant)
            svdnDownFormatsRv.layoutManager = GridLayoutManager(applicationContext, GRID_COLUMNS)
            svdnDownFormatsRv.adapter = adapter
            svdnContainerDownFormats.visibility = View.VISIBLE
        }
    }

    private fun setSuccessPrepareToDownload() {
        svdnBottomSheetBinding.apply {
            svdnTitleAuthorTv.text = getString(R.string.download_title)
            svdnAnimationView.apply {
                setAnimation(R.raw.donwload_rabbit)
                repeatCount = 0
                playAnimation()
            }.addAnimatorListener(animationDownloadListener())
        }
    }

    private fun setConvertView() {
        svdnBottomSheetBinding.apply {
            changeColorCardBackground(svdnCardViewImage, R.color.canela_variant)
            svdnAnimationView.apply {
                visibility = View.VISIBLE
                setAnimation(R.raw.rabbit_drink)
                repeatCount = LottieDrawable.INFINITE
                playAnimation()
            }
            svdnImageView.visibility = View.GONE
            svdnTitleAuthorTv.text = getString(R.string.convert_title)
            svdnContainerDownFormats.visibility = View.GONE
        }

    }

    private fun setErrorRequestView(error: RequestErrors) {
        svdnBottomSheetBinding.apply {
            setShimmerHide(true)
            svdnShimmerViewContainerDownFormats.visibility = View.GONE
            changeColorCardBackground(svdnCardViewImage, R.color.white)
            svdnAnimationView.apply {
                visibility = View.VISIBLE
                setAnimation(R.raw.error_rabbit)
                repeatCount = LottieDrawable.INFINITE
                playAnimation()
            }
            changeColorCardBackground(cardContainerText, R.color.canela_variant)
            svdnTitleAuthorTv.text = Utils.buildMessageError(error)
        }
    }

    private fun setShimmerHide(isHidden: Boolean) {
        svdnBottomSheetBinding.apply {
            if (isHidden) {
                svdnShimmerViewContainerImage.hideShimmer()
                svdnShimmerViewContainerText.hideShimmer()
                svdnShimmerViewContainerDownFormats.hideShimmer()
                return
            } else {
                svdnShimmerViewContainerImage.apply {
                    showShimmer(true)
                    startShimmer()
                }
                svdnShimmerViewContainerText.apply {
                    showShimmer(true)
                    startShimmer()
                }
                svdnShimmerViewContainerDownFormats.apply {
                    showShimmer(true)
                    startShimmer()
                }
            }
        }
    }

    private fun setRetryGetInfoView() {
        svdnBottomSheetBinding.apply {
            setShimmerHide(false)
            svdnShimmerViewContainerDownFormats.apply {
                visibility = View.VISIBLE
            }
            svdnAnimationView.visibility = View.INVISIBLE
            changeColorCardBackground(svdnCardViewImage, R.color.background_gray_dark)
            changeColorCardBackground(cardContainerText, R.color.background_gray_dark)
            svdnTitleAuthorTv.text = ""
            svdnRetryConnectButton.visibility = View.INVISIBLE
        }
    }

    private fun changeColorCardBackground(card: CardView, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            card.backgroundTintList = applicationContext.getColorStateList(color)
        } else {
            card.setCardBackgroundColor(
                ContextCompat.getColor(
                    applicationContext,
                    color
                )
            )
        }
    }

    private fun retryGetInfoSong(): View.OnClickListener =
        View.OnClickListener {
            setRetryGetInfoView()
            deviceHaveInternetConnection(application).apply {
                if (this) {
                    if (urlSong.isNotEmpty()) {
                        viewModel.getInfoSongFromUrl(urlSong)
                        return@OnClickListener
                    }
                } else {
                    setNotNetworkView()
                }
            }
        }

    private fun animationDownloadListener() = object : Animator.AnimatorListener{
        override fun onAnimationStart(p0: Animator?) {}
        override fun onAnimationEnd(p0: Animator?) {
            viewModel.downloadSong()
            svdnBottomSheet.dismiss()
        }
        override fun onAnimationCancel(p0: Animator?) {}
        override fun onAnimationRepeat(p0: Animator?) {}
    }

    private fun onFormatItemClick() = object : FormatsAdapter.OnClickFormatItemListener {
        override fun onClickFormatItem(iTag: Int) {
            setConvertView()
            viewModel.prepareSongToDownload(iTag)
        }
    }

    private fun positiveActionPermissions() {
        requestPermissionReadExternalStorage.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun negativeActionPermissions() {
        Utils.toastMessage(applicationContext, getString(R.string.permission_denied))
    }

    override fun onDismiss(p0: DialogInterface?) {
        finish()
    }

}