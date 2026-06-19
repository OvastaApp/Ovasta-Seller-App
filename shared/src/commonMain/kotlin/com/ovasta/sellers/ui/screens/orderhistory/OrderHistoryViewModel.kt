package com.ovasta.sellers.ui.screens.orderhistory

import androidx.lifecycle.viewModelScope
import com.ovasta.sellers.domain.repository.IOrderHistoryRepository
import com.ovasta.sellers.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrderHistoryViewModel(
    private val orderHistoryRepository: IOrderHistoryRepository,
) : BaseViewModel() {

    private val _viewState = MutableStateFlow(OrderHistoryViewState())
    val viewState = _viewState.asStateFlow()

    fun onScreenAction(action: OrderHistoryAction) {
        when (action) {
            OrderHistoryAction.LoadOrderHistory -> loadFirstPage()
            OrderHistoryAction.LoadMore -> loadNextPage()
        }
    }

    private fun loadFirstPage() {
        viewModelScope.launch {
            setLoading(true)
            runCatching { orderHistoryRepository.getLastOrders(page = 1) }
                .onSuccess { response ->
                    setLoading(false)
                    _viewState.update {
                        it.copy(
                            orders = response.orders,
                            currentPage = response.currentPage,
                            lastPage = response.lastPage,
                            isLoadingMore = false,
                        )
                    }
                }
                .onFailure {
                    setLoading(false)
                    handleError(it) { loadFirstPage() }
                }
        }
    }

    private fun loadNextPage() {
        val state = _viewState.value
        // Guard against duplicate loads and loading past the last page.
        if (state.isLoadingMore || !state.canLoadMore) return

        val nextPage = state.currentPage + 1
        viewModelScope.launch {
            _viewState.update { it.copy(isLoadingMore = true) }
            runCatching { orderHistoryRepository.getLastOrders(page = nextPage) }
                .onSuccess { response ->
                    _viewState.update {
                        it.copy(
                            orders = it.orders + response.orders,
                            currentPage = response.currentPage,
                            lastPage = response.lastPage,
                            isLoadingMore = false,
                        )
                    }
                }
                .onFailure {
                    _viewState.update { it.copy(isLoadingMore = false) }
                    handleError(it) { loadNextPage() }
                }
        }
    }
}
