package com.example.rakshapath

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible

class SuccessLoadingHelper {
    private lateinit var dialog: AlertDialog
    private lateinit var view:View
    fun showSuccessDialog(context: Context) {
        val inflater = LayoutInflater.from(context)
        view = inflater.inflate(R.layout.success_dialog, null)

        val builder = AlertDialog.Builder(context)
        builder.setView(view)
        builder.setCancelable(false)

        dialog = builder.create()
        dialog.show()
    }

    fun hideButtons(){
        val btnLayout = view.findViewById<LinearLayout>(R.id.CustomDialogButtonLayout)
        btnLayout.isVisible = false
    }
    fun updateText(message:String){
        val txt = view.findViewById<TextView>(R.id.textView2)
        txt.text = message
    }
    fun dismissSuccessDialog() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }
}