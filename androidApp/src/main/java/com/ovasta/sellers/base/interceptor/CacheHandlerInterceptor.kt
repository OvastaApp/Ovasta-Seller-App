package com.ovasta.sellers.base.interceptor

import android.content.Context
import com.ovasta.sellers.data.RemoteConstants
import okhttp3.Cache
import java.io.File

object CacheProviderInterceptor {
    fun provideCache(context: Context): Cache {
        return Cache(
            File(context.cacheDir, RemoteConstants.CACHE_NAME),
            RemoteConstants.CACHE_SIZE
        )
    }
}
