package com.ovasta.sellers.base.local

import android.app.Application


class AppResources(private val application: Application) {
    fun getString(resId: Int): String = application.getString(resId)
}