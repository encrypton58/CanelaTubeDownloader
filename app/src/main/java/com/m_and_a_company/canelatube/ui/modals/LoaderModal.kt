package com.m_and_a_company.canelatube.ui.modals

import android.app.Dialog
import android.content.Context
import com.m_and_a_company.canelatube.R

class LoaderModal(context: Context): Dialog(context) {

    init {
        setContentView(R.layout.overlay_loader_layout)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }

}