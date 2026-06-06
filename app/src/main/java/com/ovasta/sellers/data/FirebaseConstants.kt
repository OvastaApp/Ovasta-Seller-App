package com.ovasta.sellers.data

import org.koin.core.component.KoinComponent

object FirebaseConstants : KoinComponent {
    const val FIRESTORE_ROOT_ORDERS_NAME: String = "orders"
    const val FIRESTORE_ROOT_ONLINE_DRIVERS_NAME: String = "online_drivers"
    const val FIRESTORE_ROOT_DISTRICT_NAME: String = "districts"
    const val FIRESTORE_PRODUCTS_NAME: String = "products"


    const val USER_ID_KEY = "user_id"
    const val USER_NAME_KEY = "user_name"
    const val USER_PHONE_KEY = "user_phone"
}