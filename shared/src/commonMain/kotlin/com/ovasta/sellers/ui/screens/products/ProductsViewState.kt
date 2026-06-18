package com.ovasta.sellers.ui.screens.products

import com.ovasta.sellers.domain.model.ProductCategory
import com.ovasta.sellers.domain.model.ProductSubCategory
import com.ovasta.sellers.domain.model.SellerProduct

data class ProductCategoriesViewState(
    val categories: List<ProductCategory> = emptyList(),
)

sealed interface ProductCategoriesAction {
    data object LoadCategories : ProductCategoriesAction
    data class OnCategoryClicked(val categoryId: Int) : ProductCategoriesAction
}

data class CategoryProductsViewState(
    val categoryName: String = "",
    val subCategories: List<ProductSubCategory> = emptyList(),
    val selectedSubCategoryId: Int? = null,
    val editingProduct: SellerProduct? = null,
) {
    val visibleProducts: List<SellerProduct>
        get() = subCategories
            .firstOrNull { it.id == selectedSubCategoryId }
            ?.products
            .orEmpty()
}

sealed interface CategoryProductsAction {
    data class LoadProducts(val categoryId: Int) : CategoryProductsAction
    data class OnSubCategorySelected(val subCategoryId: Int) : CategoryProductsAction
    data class OnProductClicked(val product: SellerProduct) : CategoryProductsAction
    data object DismissEdit : CategoryProductsAction
    data class OnEditSubmitted(
        val salesPrice: Double,
        val purchasePrice: Double,
        val show: Boolean,
        val active: Boolean,
    ) : CategoryProductsAction
}
