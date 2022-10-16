package com.m_and_a_company.canelatube.ui

import android.content.Context
import android.widget.Toast
import com.m_and_a_company.canelatube.R
import com.m_and_a_company.canelatube.domain.network.model.RequestErrors
import com.m_and_a_company.canelatube.network.domain.model.ErrorModel

object Utils {

    fun buildMessageError(errors: List<ErrorModel>): String {
        val message = StringBuilder()
        if(!errors.isEmpty()) {
            errors.forEach { error ->
                message.append("${error.field} ")
                message.append("${error.message} ")
                message .append("${error.rule} ")

            }
            return message.toString()
        }
        return ""
    }

    fun toastMessage(ctx: Context, message: String) {
        Toast.makeText(ctx, message, Toast.LENGTH_LONG).show()
    }

    fun alertDialog(ctx: Context, title: String ,message: String, positiveAction: () -> Unit, negativeAction: () -> Unit) {
        val alertDialog = android.app.AlertDialog.Builder(ctx)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(ctx.getString(R.string.accept_text_dialog)) { dialog, _ ->
                positiveAction()
                dialog.dismiss()
            }
            .setNegativeButton(ctx.getString(R.string.cancel_text_dialog)) { dialog, _ ->
                negativeAction()
                dialog.dismiss()
            }
        alertDialog.create().show()
    }

}