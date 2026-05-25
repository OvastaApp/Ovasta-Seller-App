package com.ovasta.sellers.data.network

import io.ktor.client.HttpClient

expect fun createHttpClient(tokenProvider: AuthTokenProvider?): HttpClient