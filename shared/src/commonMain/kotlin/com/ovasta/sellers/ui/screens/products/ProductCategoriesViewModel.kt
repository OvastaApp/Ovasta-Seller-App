package com.ovasta.sellers.ui.screens.products

import androidx.lifecycle.viewModelScope
import com.ovasta.sellers.domain.repository.IProductRepository
import com.ovasta.sellers.ui.base.BaseViewModel
import com.ovasta.sellers.ui.base.ScreenDirection
import com.ovasta.sellers.ui.screens.CategoryProducts
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductCategoriesViewModel(
    private val productRepository: IProductRepository,
) : BaseViewModel() {

    private val _viewState = MutableStateFlow(ProductCategoriesViewState())
    val viewState = _viewState.asStateFlow()

    fun onScreenAction(action: ProductCategoriesAction) {
        when (action) {
            ProductCategoriesAction.LoadCategories -> loadCategories()
            is ProductCategoriesAction.OnCategoryClicked ->
                emitScreenDirection(ScreenDirection.Push(CategoryProducts(action.categoryId)))
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            setLoading(true)
            runCatching { productRepository.getCategories() }
                .onSuccess { categories ->
                    setLoading(false)
                    _viewState.update { it.copy(categories = categories) }
                    // Single-category shortcut: skip the list and go straight to products.
                    if (categories.size == 1) {
                        emitScreenDirection(ScreenDirection.Replace(CategoryProducts(categories.first().id)))
                    }
                }
                .onFailure {
                    setLoading(false)
                    handleError(it)
                }
        }
    }
}
