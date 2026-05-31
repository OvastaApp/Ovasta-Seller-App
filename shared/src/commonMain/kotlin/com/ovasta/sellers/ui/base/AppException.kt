package com.ovasta.sellers.ui.base

/**
 * Multiplatform error model replacing Android-specific ComposeUIException.
 * Uses plain strings instead of @StringRes references.
 */
data class AppException(
    val title: String? = null,
    val message: String? = null,
    val code: Int? = null,
    val actions: List<() -> Unit> = emptyList(),
)
