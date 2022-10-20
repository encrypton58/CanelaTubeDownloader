package com.m_and_a_company.canelatube.enviroment

import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.core.content.ContextCompat

fun verifyPermissionsWriteExternalStorage(ctx: Context): Boolean {

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Environment.isExternalStorageManager()
    } else {
        val permission = ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        permission == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

}