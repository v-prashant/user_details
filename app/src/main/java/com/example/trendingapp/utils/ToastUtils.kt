package com.example.trendingapp.utils

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.os.Looper
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.trendingapp.R
import com.example.trendingapp.ui.Application

object ToastUtils {
    enum class ToastType {
        ERROR, SUCCESS, ALERT
    }

    enum class HeaderToastType {
        ERROR, SUCCESS, ALERT
    }

    fun makeToast(type: ToastType?, headerType: HeaderToastType, message: String?) {
        makeToast(Application.getInstance(), type!!, headerType, message, Toast.LENGTH_SHORT)
    }

    fun makeLongToast(type: ToastType?, message: String?, headerType: HeaderToastType) {
        makeToast(Application.getInstance(), type!!, headerType, message, Toast.LENGTH_LONG)
    }

    fun makeToast(
        type: ToastType,
        headerType: HeaderToastType,
        message: String?,
        toastLength: Int,
    ) {
        makeToast(Application.getInstance(), type, headerType, message, toastLength)
    }

    fun makeToast(
        context: Context?,
        type: ToastType,
        headerType: HeaderToastType,
        message: String?,
        toastLength: Int,
    ) {
        if (context == null) return
        if (context is Activity) {
            if (context.isDestroyed || context.isFinishing) return
        }
        try {
            val typeToIconMap: MutableMap<ToastType, Int> = HashMap<ToastType, Int>()
            typeToIconMap[ToastType.ALERT] = R.drawable.ic_cross_circle
            typeToIconMap[ToastType.ERROR] = R.drawable.ic_toast_info
            typeToIconMap[ToastType.SUCCESS] = R.drawable.ic_toast_success
            val typeToColorMap: MutableMap<ToastType, String> = HashMap<ToastType, String>()
            typeToColorMap[ToastType.ALERT] = "#c7a014"
            typeToColorMap[ToastType.ERROR] = "#E22853"
            typeToColorMap[ToastType.SUCCESS] = "#1F7D47"
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val layout: View = inflater.inflate(R.layout.clp_toast_with_icon, null, false)
            val icon = layout.findViewById<View>(R.id.icon) as ImageView
            icon.setImageDrawable(context.resources.getDrawable(typeToIconMap[type]!!))
            val status = layout.findViewById<TextView>(R.id.tv_status)
            when (headerType) {
                HeaderToastType.SUCCESS -> {
                    status.text = "Success"
                }
                HeaderToastType.ERROR -> {
                    status.text = "Error"
                }
                HeaderToastType.ALERT -> {
                    status.text = "Alert"
                }
            }
            val text = layout.findViewById<TextView>(R.id.text)
            text.text = message
            text.setTextColor(Color.WHITE)
            layout.findViewById<View>(R.id.toast_layout_root).setBackgroundColor(
                Color.parseColor(
                    typeToColorMap[type]
                )
            )
            val toast = Toast(context.applicationContext)
            val tv = TypedValue()
            var actionBarHeight = 0
            if (context.theme.resolveAttribute(
                    android.R.attr.actionBarSize,
                    tv,
                    true
                )
            ) actionBarHeight = TypedValue.complexToDimensionPixelSize(
                tv.data,
                context.resources.displayMetrics
            )
            toast.setGravity(
                Gravity.TOP or Gravity.FILL_HORIZONTAL,
                0,
                actionBarHeight
            )
            toast.duration = toastLength
            toast.view = layout
            toast.show()

        } catch (ex: Exception) {
        }
    }

    private var dialog: ProgressDialog? = null

    fun displayProgress(context: Context?, msg: String?) {
        // This has been called from worker thread
        if (Looper.myLooper() != Looper.getMainLooper()) return
        hideProgress()
        if (context is Activity) {
            if (context.isDestroyed || context.isFinishing) return
            dialog =
                ProgressDialog(context, ProgressDialog.THEME_HOLO_LIGHT)
            dialog?.isIndeterminate = true
            dialog?.setCancelable(false)
            dialog?.setMessage(msg)
            dialog?.show()
        }
    }

    fun hideProgress() {
        if (dialog != null && dialog?.isShowing == true) {
            val window: Window = dialog?.window
                ?: return
            val decor = window.decorView
            if (decor.parent != null) {
                dialog?.dismiss()
                dialog = null
            }
        }
    }
}