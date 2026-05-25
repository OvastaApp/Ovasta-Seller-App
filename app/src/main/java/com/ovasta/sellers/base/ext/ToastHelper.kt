package com.ovasta.sellers.base.ext

import com.ovasta.sellers.platform.showPlatformToast

object ToastHelper {
    fun showLongToaster(message: String) {
        showPlatformToast(message)
    }

    fun showShortToaster(message: String?) {
        if (message != null) {
            showPlatformToast(message)
        }
    }
}
