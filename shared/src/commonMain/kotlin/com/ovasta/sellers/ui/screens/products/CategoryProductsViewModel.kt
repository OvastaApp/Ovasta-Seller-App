package com.ovasta.sellers.ui.screens.products

import androidx.lifecycle.viewModelScope
import com.ovasta.sellers.domain.model.ProductCategory
import com.ovasta.sellers.domain.repository.IProductRepository
import com.ovasta.sellers.shared.resources.Res
import com.ovasta.sellers.shared.resources.product_updated_successfully
import com.ovasta.sellers.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class CategoryProductsViewModel(
    private val productRepository: IProductRepository,
) : BaseViewModel() {

    private val _viewState = MutableStateFlow(CategoryProductsViewState())
    val viewState = _viewState.asStateFlow()

    private var categoryId: Int? = null

    fun onScreenAction(action: CategoryProductsAction) {
        when (action) {
            is CategoryProductsAction.LoadProducts -> {
                categoryId = action.categoryId
                loadProducts(action.categoryId)
            }
            is CategoryProductsAction.OnSubCategorySelected ->
                _viewState.update { it.copy(selectedSubCategoryId = action.subCategoryId) }
            is CategoryProductsAction.OnProductClicked ->
                _viewState.update { it.copy(editingProduct = action.product) }
            CategoryProductsAction.DismissEdit ->
                _viewState.update { it.copy(editingProduct = null) }
            is CategoryProductsAction.OnEditSubmitted -> updateProduct(action)
        }
    }

    private fun loadProducts(categoryId: Int) {
        viewModelScope.launch {
            setLoading(true)
            runCatching { productRepository.getCategories() }
                .onSuccess { categories ->
                    setLoading(false)
                    applyCategory(categories.firstOrNull { it.id == categoryId })
                }
                .onFailure {
                    setLoading(false)
                    handleError(it)
                }
        }
    }

    private fun applyCategory(category: ProductCategory?) {
        val subCategories = category?.subCategories.orEmpty()
        _viewState.update {
            it.copy(
                categoryName = category?.name ?: it.categoryName,
                subCategories = subCategories,
                selectedSubCategoryId = it.selectedSubCategoryId
                    ?: subCategories.firstOrNull()?.id,
            )
        }
    }

    private fun updateProduct(action: CategoryProductsAction.OnEditSubmitted) {
        val product = _viewState.value.editingProduct ?: return
        viewModelScope.launch {
            setLoading(true)
            runCatching {
                productRepository.updateProductPrice(
                    productId = product.id,
                    salesPrice = action.salesPrice,
                    purchasePrice = action.purchasePrice,
                    show = action.show,
                    active = action.active,
                )
            }.onSuccess {
                setLoading(false)
                // Optimistically reflect the change in the loaded tree.
                _viewState.update { state ->
                    val updatedSubs = state.subCategories.map { sub ->
                        sub.copy(
                            products = sub.products.map { p ->
                                if (p.id == product.id) p.copy(
                                    salesPrice = action.salesPrice,
                                    purchasePrice = action.purchasePrice,
                                    show = action.show,
                                    active = action.active,
                                ) else p
                            }
                        )
                    }
                    state.copy(subCategories = updatedSubs, editingProduct = null)
                }
                emitMessage(getString(Res.string.product_updated_successfully))
            }.onFailure {
                setLoading(false)
                handleError(it)
            }
        }
    }
}
