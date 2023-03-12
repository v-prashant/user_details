package com.example.trendingapp.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.example.trendingapp.R

class ProgressUtil(val context: Context) {

    private var progressDailog: Dialog? = null

    var isShowing: Boolean = false

    fun showDialog() {
        showDialog("")
    }

    fun showDialog(message: String) {
        if (progressDailog == null) {
            progressDailog = createProgressDialog()
        }
        val tvMessage = progressDailog?.findViewById<TextView>(R.id.loadingMessage)
        tvMessage?.text = message
        if (message.isNotEmpty()) {
            tvMessage?.visibility = View.VISIBLE
        }
        progressDailog?.let {
            if(!it.isShowing) {
                it.show()
            }
        }
        isShowing = true
    }

    fun dismissDialog(){
        progressDailog?.dismiss()
        isShowing = false
    }

    private fun createProgressDialog(): Dialog? {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_progress_dialog)
        dialog.window?.let {
            it.setDimAmount(.6f)
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        val progressBar = dialog.findViewById<AppCompatImageView>(R.id.progress_bar)
   //     Glide.with(context).load(R.drawable.logo).into(progressBar)
        return dialog
    }
}