package com.m_and_a_company.canelatube.enviroment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi


/**
 * Crea un intent para solicitar permiso de escritura en el almacenamiento externo
 * Requiere API 23 o superior para usar el StorageManager
 *
 * @param activity Activity
 * @return [Intent] intent
 */
@RequiresApi(Build.VERSION_CODES.R)
fun requestActiveStorageManager(activity: Activity): Intent {
    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
        addCategory("android.intent.category.DEFAULT")
        data = Uri.parse("package:${activity.packageName}")
    }
    return intent
}