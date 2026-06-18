package com.ovasta.sellers.data.repository

import com.ovasta.sellers.domain.model.ProductCategory
import com.ovasta.sellers.domain.model.ProductSubCategory
import com.ovasta.sellers.domain.model.SellerProduct

/**
 * Temporary in-memory dummy data for testing the My Products feature without a live API.
 * Toggle [USE_DUMMY] off (or delete this file and its usages) once the backend is wired.
 */
object ProductDummyData {
    /** When true, the product feature uses local dummy data and the Profile tab is forced visible. */
    const val USE_DUMMY = true

    private var lastId = 1000
    fun nextProductId(): Int = ++lastId

    val categories: List<ProductCategory> = listOf(
        ProductCategory(
            id = 1,
            name = "المعلم",
            count = 5,
            subCategories = listOf(
                ProductSubCategory(
                    id = 1,
                    name = "ساندويتشات",
                    products = listOf(
                        SellerProduct(id = 101, name = "ساندويتش فراخ", active = true, show = true, salesPrice = 45.0, purchasePrice = 30.0),
                        SellerProduct(id = 102, name = "ساندويتش لحمة", active = true, show = false, salesPrice = 60.0, purchasePrice = 42.0),
                        SellerProduct(id = 103, name = "ساندويتش طعمية", active = false, show = false, salesPrice = 15.0, purchasePrice = 8.0),
                    )
                ),
                ProductSubCategory(
                    id = 2,
                    name = "مشروبات",
                    products = listOf(
                        SellerProduct(id = 104, name = "عصير برتقال", active = true, show = true, salesPrice = 20.0, purchasePrice = 12.0),
                        SellerProduct(id = 105, name = "مياه معدنية", active = true, show = true, salesPrice = 5.0, purchasePrice = 3.0),
                    )
                ),
            )
        ),
        ProductCategory(
            id = 2,
            name = "البقالة",
            count = 3,
            subCategories = listOf(
                ProductSubCategory(
                    id = 3,
                    name = "معلبات",
                    products = listOf(
                        SellerProduct(id = 201, name = "تونة", active = true, show = true, salesPrice = 35.0, purchasePrice = 25.0),
                        SellerProduct(id = 202, name = "فول مدمس", active = true, show = true, salesPrice = 12.0, purchasePrice = 7.0),
                    )
                ),
                ProductSubCategory(
                    id = 4,
                    name = "ألبان",
                    products = listOf(
                        SellerProduct(id = 203, name = "جبنة بيضاء", active = false, show = true, salesPrice = 50.0, purchasePrice = 38.0),
                    )
                ),
            )
        ),
    )
}
