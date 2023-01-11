package com.m_and_a_company.canelatube.ui

import android.content.Context
import android.widget.Toast
import com.m_and_a_company.canelatube.R

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

}