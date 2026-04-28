package com.ovasta.sellers.base.ext

import android.widget.Toast
import androidx.annotation.StringRes

sealed interface ToastEvent {

    class StringToastEvent(val message: String, val duration: Int = Toast.LENGTH_SHORT) : ToastEvent

    class ResourceToastEvent(@StringRes val resId: Int, val duration: Int = Toast.LENGTH_SHORT) : ToastEvent

}