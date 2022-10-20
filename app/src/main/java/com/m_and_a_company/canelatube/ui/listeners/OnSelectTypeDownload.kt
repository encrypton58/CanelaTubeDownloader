package com.m_and_a_company.canelatube.ui.listeners

import com.m_and_a_company.canelatube.ui.enums.TypeDownload

/**
 * Escuchante para el tipo de descarga
 */
interface OnSelectTypeDownload {

    /**
     * Selecciona el tipo de descarga
     * @param [type] tipo de descarga
     */
    fun onAccept(type: TypeDownload)

    /**
     * Cancela la selección del tipo de descarga
     */
    fun onCancel()

}