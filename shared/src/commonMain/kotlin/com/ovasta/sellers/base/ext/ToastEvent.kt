package com.ovasta.sellers.base.ext

data class StringId(val key: String)

sealed class ToastEvent {
    data class StringToastEvent(
        val message: String
    ) : ToastEvent()

    data class ResourceToastEvent(
        val stringId: StringId,
        val args: List<Any> = emptyList()
    ) : ToastEvent()
}
