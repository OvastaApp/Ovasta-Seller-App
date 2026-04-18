package com.ovasta.sellers.presentation.home.presentation

import com.ovasta.sellers.base.exception.ComposeUIException
import com.ovasta.sellers.presentation.home.data.model.HomeTask
import com.ovasta.sellers.presentation.home.data.model.PartnerStatistics

data class HomeViewState(
    val tasks: List<HomeTask> = emptyList(),
    val filteredTasks: List<HomeTask> = emptyList(),
    val error: ComposeUIException? = null,
    val showToastMessage: Int? = null,
    val isTracking: Boolean = false,
    val partnerStatistics: PartnerStatistics?= null,
    val isLogoutDialogVisible: Boolean = false
)