package com.m_and_a_company.canelatube.ui

import android.app.Activity
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.core.content.ContextCompat
import com.m_and_a_company.canelatube.R
import com.m_and_a_company.canelatube.databinding.SelectTypeDownloadDialogBinding
import com.m_and_a_company.canelatube.ui.enums.TypeDownload
import com.m_and_a_company.canelatube.ui.listeners.OnSelectTypeDownload

class SelectToDownloadTypeDialog(
    activity: Activity,
    selectedTypeDownloadListener: OnSelectTypeDownload
) : Dialog(activity) {

    private val selectTypeDownloadDialogBinding: SelectTypeDownloadDialogBinding
    private val listener: OnSelectTypeDownload
    private var typeDownload = TypeDownload.UNDEFINED

    init {
        selectTypeDownloadDialogBinding =
            SelectTypeDownloadDialogBinding.inflate(activity.layoutInflater)
        listener = selectedTypeDownloadListener
        setContentView(selectTypeDownloadDialogBinding.root)
        setCancelable(false)
        window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        initViews()
        show()
    }

    private fun initViews() {
        selectTypeDownloadDialogBinding.apply {
            typeDownloadCardAudio.setOnClickListener(audioSelected())
            typeDownloadCardVideo.setOnClickListener(videoSelected())
            btnSelectTypeDownloadAccept.setOnClickListener(acceptTypeDownload())
            btnSelectTypeDownloadCancel.setOnClickListener(cancelTypeDownload())
        }
    }

    private fun audioSelected() = View.OnClickListener {
        resetCards()
        updateUISelectedCardAudio()
        typeDownload = TypeDownload.AUDIO
    }

    private fun videoSelected() = View.OnClickListener {
        resetCards()
        updateUISelectedCardVideo()
        typeDownload = TypeDownload.VIDEO
    }

    private fun resetCards() {
        selectTypeDownloadDialogBinding.apply {
            typeDownloadCardAudio.apply {
                backgroundTintList =
                    ContextCompat.getColorStateList(context, R.color.canela_variant)
                cardElevation = 10f
            }
            typeDownloadCardVideo.apply {
                backgroundTintList =
                    ContextCompat.getColorStateList(context, R.color.canela_variant)
                cardElevation = 10f
            }
        }
    }

    private fun updateUISelectedCardAudio() {
        selectTypeDownloadDialogBinding.apply {
            typeDownloadCardAudio.apply {
                backgroundTintList =
                    ContextCompat.getColorStateList(context, R.color.canela_background)
                cardElevation = 0f
            }
        }
    }

    private fun updateUISelectedCardVideo() {
        selectTypeDownloadDialogBinding.apply {
            typeDownloadCardVideo.apply {
                backgroundTintList =
                    ContextCompat.getColorStateList(context, R.color.canela_background)
                cardElevation = 0f
            }
        }
    }

    private fun acceptTypeDownload() = View.OnClickListener {
        listener.onAccept(typeDownload)
        if(typeDownload != TypeDownload.UNDEFINED) {
            dismiss()
        }
    }

    private fun cancelTypeDownload() = View.OnClickListener {
        dismiss()
        listener.onCancel()
    }

}