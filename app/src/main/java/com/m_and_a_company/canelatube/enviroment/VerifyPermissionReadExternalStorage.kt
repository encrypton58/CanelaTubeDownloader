package com.m_and_a_company.canelatube.enviroment

import android.content.Context
import androidx.core.content.ContextCompat

/**
 * Verifica si se tiene permiso para leer archivos del almacenamiento externo
 * @param [ctx] Contexto de la aplicacion
 * @return [Boolean] true si se tiene permiso, false si no se tiene permiso
 */
fun verifyPermissionReadExternalStorage(ctx: Context): Boolean {
    val result = ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.READ_EXTERNAL_STORAGE)
    return result == android.content.pm.PackageManager.PERMISSION_GRANTED
}