package com.ovasta.sellers

expect fun getPlatform(): Platform

interface Platform {
    val name: String
}
