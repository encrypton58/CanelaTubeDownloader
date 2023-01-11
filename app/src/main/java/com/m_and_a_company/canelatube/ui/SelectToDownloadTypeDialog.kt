package com.m_and_a_company.canelatube.ui

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import com.m_and_a_company.canelatube.R
import com.m_and_a_company.canelatube.databinding.SelectTypeDownloadDialogBinding
import com.m_and_a_company.canelatube.ui.enums.TypeDownload
import com.m_and_a_company.canelatube.ui.listeners.OnSelectTypeDownload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        setCancelable(true)
        window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        setOnDismissListener(this::onDismissDialog)
        initViews()
        show()
    }

    override fun show() {
        resetCards()
        super.show()
    }

    private fun initViews() {
        selectTypeDownloadDialogBinding.apply {
            typeDownloadCardAudio.setOnClickListener{ audioSelected() }
        }
    }

    private fun audioSelected() {
        resetCards()
        updateUISelectedCardAudio()
        typeDownload = TypeDownload.AUDIO
        CoroutineScope(Dispatchers.Main).launch {
            acceptTypeDownload()
        }
    }

    private fun resetCards() {
        selectTypeDownloadDialogBinding.apply {
            typeDownloadCardAudio.apply {
                backgroundTintList =
                    ContextCompat.getColorStateList(context, R.color.blue_paster_audio_background)
                cardElevation = 10f
            }
        }
    }

    private fun updateUISelectedCardAudio() {
        selectTypeDownloadDialogBinding.apply {
            typeDownloadCardAudio.apply {
                backgroundTintList =
                    ContextCompat.getColorStateList(context, R.color.blue_paster_audio_background_selected)
                cardElevation = 0f
            }
        }
    }

    private suspend fun acceptTypeDownload() {
        delay(500)
        listener.onAccept(typeDownload)
        if(typeDownload != TypeDownload.UNDEFINED) {
            dismiss()
        }else {
            Utils.toastMessage(context, "Debe seleccionar un tipo de descarga")
        }
    }

    private fun onDismissDialog(dialogInterface: DialogInterface) {
        dialogInterface.dismiss()
    }

}