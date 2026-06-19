package com.ovasta.sellers.ui.screens.products

import androidx.lifecycle.viewModelScope
import com.ovasta.sellers.domain.model.ProductCategory
import com.ovasta.sellers.domain.repository.IProductRepository
import com.ovasta.sellers.shared.resources.Res
import com.ovasta.sellers.shared.resources.product_added_successfully
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
                _viewState.update { it.copy(editingProduct = action.product, isAddingProduct = false) }
            CategoryProductsAction.OnAddProductClicked ->
                _viewState.update { it.copy(isAddingProduct = true, editingProduct = null) }
            CategoryProductsAction.DismissEditor ->
                _viewState.update { it.copy(editingProduct = null, isAddingProduct = false) }
            is CategoryProductsAction.OnEditSubmitted -> updateProduct(action)
            is CategoryProductsAction.OnNewProductSubmitted -> addProduct(action)
        }
    }

    private fun loadProducts(categoryId: Int) {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true) }
            runCatching { productRepository.getCategories() }
                .onSuccess { categories ->
                    applyCategory(categories.firstOrNull { it.id == categoryId })
                    _viewState.update { it.copy(isLoading = false) }
                }
                .onFailure {
                    _viewState.update { it.copy(isLoading = false) }
                    handleError(it)
                }
        }
    }

    private fun applyCategory(category: ProductCategory?) {
        val subCategories = category?.subCategories.orEmpty()
        _viewState.update {
            // Keep the current selection only if it belongs to this category;
            // otherwise default to the first chip (e.g. when switching categories
            // while the ViewModel instance is reused).
            val validSelection = subCategories
                .firstOrNull { sub -> sub.id == it.selectedSubCategoryId }?.id
                ?: subCategories.firstOrNull()?.id
            it.copy(
                categoryName = category?.name ?: it.categoryName,
                subCategories = subCategories,
                selectedSubCategoryId = validSelection,
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
                    name = action.name,
                    price = action.price,
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
                                    name = action.name,
                                    salesPrice = action.price,
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

    private fun addProduct(action: CategoryProductsAction.OnNewProductSubmitted) {
        val subCategoryId = _viewState.value.selectedSubCategoryId ?: return
        viewModelScope.launch {
            setLoading(true)
            runCatching {
                productRepository.addProduct(
                    subCategoryId = subCategoryId,
                    name = action.name,
                    price = action.price,
                    show = action.show,
                    active = action.active,
                )
            }.onSuccess { newProduct ->
                setLoading(false)
                // Optimistically append the new product to the selected sub-category.
                _viewState.update { state ->
                    val updatedSubs = state.subCategories.map { sub ->
                        if (sub.id == subCategoryId) sub.copy(products = sub.products + newProduct)
                        else sub
                    }
                    state.copy(subCategories = updatedSubs, isAddingProduct = false)
                }
                emitMessage(getString(Res.string.product_added_successfully))
            }.onFailure {
                setLoading(false)
                handleError(it)
            }
        }
    }
}
