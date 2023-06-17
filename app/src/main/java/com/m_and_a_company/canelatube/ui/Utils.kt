package com.m_and_a_company.canelatube.ui

import android.app.Dialog
import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.m_and_a_company.canelatube.R
import com.tapadoo.alerter.Alerter

object Utils {

    fun toastMessage(ctx: Context, message: String) {
        Toast.makeText(ctx, message, Toast.LENGTH_LONG).show()
    }

    fun alertDialog(
        ctx: Context, title: String, message: String,
        positiveAction: () -> Unit,
        negativeAction: () -> Unit,
        enbledPositiveAction: Boolean
    ) {
        val alertDialog = android.app.AlertDialog.Builder(ctx).apply {
            setTitle(title)
            setMessage(message)
            if (enbledPositiveAction){
                setPositiveButton(ctx.getString(R.string.accept_text_dialog)) { dialog, _ ->
                    positiveAction()
                    dialog.dismiss()
                }
            }

            setNegativeButton(ctx.getString(R.string.cancel_text_dialog)) { dialog, _ ->
                negativeAction()
                dialog.dismiss()
            }
        }
        alertDialog.create().show()
    }

    fun alertTop(dialog: Dialog, title: String, text: String) = Alerter.create(dialog)
            .setTitle(title)
            .setText(text)
            .setIcon(R.drawable.icon_warning)
            .setDuration(20 * 1000)
            .enableSwipeToDismiss()
            .setBackgroundColorInt(ContextCompat.getColor(dialog.context, R.color.yellow_pastel_background_warning))
}