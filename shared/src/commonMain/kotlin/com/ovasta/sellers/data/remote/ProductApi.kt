package com.ovasta.sellers.data.remote

import com.ovasta.sellers.domain.model.AddProductRequest
import com.ovasta.sellers.domain.model.ApiResponse
import com.ovasta.sellers.domain.model.ProductCategory
import com.ovasta.sellers.domain.model.SellerProduct
import com.ovasta.sellers.domain.model.UpdateProductRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class ProductApiService(private val client: HttpClient) {
    suspend fun getCategories(): ApiResponse<List<ProductCategory>> {
        return client.get("categories").body()
    }

    suspend fun updateProduct(districtProductId: Int, request: UpdateProductRequest) {
        client.put("categories/products/$districtProductId") {
            setBody(request)
        }
    }

    suspend fun addProduct(request: AddProductRequest): ApiResponse<SellerProduct> {
        return client.post("products") {
            setBody(request)
        }.body()
    }
}
