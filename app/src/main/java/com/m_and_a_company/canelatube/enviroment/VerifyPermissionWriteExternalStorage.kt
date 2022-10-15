package com.m_and_a_company.canelatube.enviroment

import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.core.content.ContextCompat

fun verifyPermissionsWriteExternalStorage(ctx: Context): HashMap<String, Boolean> {

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val map = HashMap<String, Boolean>()
        map[VERSION_R_MORE] = Environment.isExternalStorageManager()
        map
    } else {
        val map = HashMap<String, Boolean>()
        map[VERSION_LESS_R] = ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.READ_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_GRANTED
        map
    }

}