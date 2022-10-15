package com.m_and_a_company.canelatube.enviroment

import android.content.Context
import androidx.core.content.ContextCompat

fun verifyPermissionReadExternalStorage(ctx: Context): Boolean {
    val result = ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.READ_EXTERNAL_STORAGE)
    return result == android.content.pm.PackageManager.PERMISSION_GRANTED
}