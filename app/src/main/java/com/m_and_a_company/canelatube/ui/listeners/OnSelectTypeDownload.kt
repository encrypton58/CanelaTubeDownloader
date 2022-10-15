package com.m_and_a_company.canelatube.ui.listeners

import com.m_and_a_company.canelatube.ui.enums.TypeDownload

interface OnSelectTypeDownload {

    fun onAccept(type: TypeDownload)

    fun onCancel()

}