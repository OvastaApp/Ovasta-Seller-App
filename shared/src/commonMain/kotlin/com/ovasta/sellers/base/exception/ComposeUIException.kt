package com.ovasta.sellers.base.exception

import java.io.IOException

data class ComposeUIException(
    val exceptionTitle: String? = null,
    val errorMessage: String? = null,
    val exceptionMessage: String? = null,
    var code: Int? = null,
    val data: Any? = null,
    val actions: List<(() -> Unit)> = listOf(),
) : IOException()

fun Throwable.toComposeUIException(vararg actions: () -> Unit): ComposeUIException {
    return when (this) {
        is APIException -> {
            ComposeUIException(
                code = this.code,
                errorMessage = this.errorMessage,
                data = this.data,
                actions = actions.toList(),
            )
        }

        is NetworkException -> {
            ComposeUIException(
                code = 0,
                errorMessage = this.message,
                actions = actions.toList()
            )
        }

        is ComposeUIException -> this

        else -> {
            ComposeUIException(
                code = 0,
                errorMessage = null,
                actions = actions.toList(),
                exceptionMessage = "Unknown error occurred"
            )
        }
    }
}
