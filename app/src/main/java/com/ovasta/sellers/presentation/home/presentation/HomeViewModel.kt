package com.ovasta.sellers.presentation.home.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ovasta.sellers.R
import com.ovasta.sellers.base.BaseViewModel
import com.ovasta.sellers.data.setting.data.ISettingsRepository
import com.ovasta.sellers.presentation.home.data.IHomeRepository
import com.ovasta.sellers.presentation.home.data.model.HomeTask
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.content.Context
import com.ovasta.sellers.base.ScreenDirection
import com.ovasta.sellers.base.exception.toComposeUIException
import com.ovasta.sellers.presentation.nav.Login
import com.ovasta.sellers.presentation.nav.TaskDetails
import kotlinx.coroutines.Job

class HomeViewModel(
    val homeRepository: IHomeRepository
) : BaseViewModel() {


}