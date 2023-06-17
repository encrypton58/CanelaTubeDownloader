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
import kotlinx.coroutines.launch

class SelectToDownloadTypeDialog(
    private val activity: Activity,
    selectedTypeDownloadListener: OnSelectTypeDownload
) : Dialog(activity) {

    private val selectTypeDownloadDialogBinding: SelectTypeDownloadDialogBinding =
        SelectTypeDownloadDialogBinding.inflate(activity.layoutInflater)
    private val listener: OnSelectTypeDownload
    private var typeDownload = TypeDownload.UNDEFINED

    init {
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

    private fun acceptTypeDownload() {
        listener.onAccept(typeDownload)
        if(typeDownload != TypeDownload.UNDEFINED) {
            dismiss()
        }else {
            Utils.toastMessage(context, "Debe seleccionar un tipo de descarga")
        }
    }

    /**
     * Maneja dismiss dialog que cierra la actividad si no se selecciona un tipo
     * de descarga válida o no se selecciona nada y se ejecuta OnBackPressed
     * @param dialogInterface Interfaz de escucha
     */
    private fun onDismissDialog(dialogInterface: DialogInterface) {
        dialogInterface.dismiss()
        if (typeDownload == TypeDownload.UNDEFINED) {
            activity.finish()
            Utils.toastMessage(activity.applicationContext, activity.applicationContext.getString(R.string.download_cancel_message))
        }
    }

}