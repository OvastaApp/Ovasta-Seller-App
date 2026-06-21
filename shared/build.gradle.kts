plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("com.android.library")
    alias(libs.plugins.serialization)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
}

kotlin {

    androidTarget()

    val xcfName = "sharedKit"

    iosX64 {
        binaries.framework {
            baseName = xcfName
            binaryOption("bundleId", "com.ovasta.sellers.shared")
        }
    }

    iosArm64 {
        binaries.framework {
            baseName = xcfName
            binaryOption("bundleId", "com.ovasta.sellers.shared")
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = xcfName
            binaryOption("bundleId", "com.ovasta.sellers.shared")
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                // Networking
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.logging)
                // Serialization
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.serialization.core)
                // DI
                implementation(libs.koin.core)
                // Coroutines
                implementation(libs.kotlinx.coroutines.core)
                // DataStore
                implementation(libs.datastore.preferences.core)
                // Compose Multiplatform
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                // Image loading (KMP, Ktor-backed)
                implementation(libs.coil.compose)
                implementation(libs.coil.network.ktor)
                // Lifecycle ViewModel (KMP-compatible)
                implementation(libs.lifecycle.viewmodel.compose)
                // Koin Compose Multiplatform
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.activity.compose)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.security.crypto)
                implementation(libs.androidx.datastore.preferences)
                // Firebase (using platform BOM from androidApp)
                implementation(project.dependencies.platform(libs.firebase.bom))
                implementation(libs.firebase.auth)
                implementation(libs.firebase.crashlytics)
                implementation(libs.firebase.firestore)
                implementation(libs.firebase.messaging)
                // Coroutines support for Firebase
                implementation(libs.kotlinx.coroutines.play.services)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }
    }
}

android {
    namespace = "com.ovasta.shared"
    compileSdk = 36
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.ovasta.sellers.shared.resources"
    generateResClass = always
}
