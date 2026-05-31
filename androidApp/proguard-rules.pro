# ============================================================================
# ProGuard Rules for Ovasta Seller App
# ============================================================================

# ---------- General / Debugging ----------
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses,EnclosingMethod
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations

# ---------- Kotlin ----------
-dontwarn kotlin.**
-keep class kotlin.Metadata { *; }
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Lazy {
    public *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ---------- Gson ----------
# Gson ships its own consumer rules; only keep app-specific model classes
-dontwarn sun.misc.**

# Keep all model/data classes used with @SerializedName
-keep class com.ovasta.sellers.data.** { *; }
-keep class com.ovasta.sellers.data.setting.model.** { *; }
-keep class com.ovasta.sellers.data.setting.data.datastore.** { *; }
-keep class com.ovasta.sellers.base.exception.** { *; }

# Shared module domain models (kotlinx.serialization)
-keep class com.ovasta.sellers.domain.model.** { *; }

# Keep any class with @SerializedName fields
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep any class with @Expose fields
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.Expose <fields>;
}

# Keep any class with @Keep annotation
-keep @androidx.annotation.Keep class * { *; }
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}

# ---------- Kotlinx Serialization ----------
-keep,includedescriptorclasses class com.ovasta.sellers.**$$serializer { *; }
-keepclassmembers class com.ovasta.sellers.** {
    *** Companion;
}
-keepclasseswithmembers class com.ovasta.sellers.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-dontwarn kotlinx.serialization.**

# ---------- Navigation3 Route Objects ----------
-keep class com.ovasta.sellers.presentation.nav.** { *; }

# ---------- Retrofit ----------
# Retrofit ships its own consumer rules; only keep app-specific interfaces
-dontwarn retrofit2.**

# Keep Retrofit service interfaces
-keep,allowobfuscation interface com.ovasta.sellers.data.setting.data.SettingsApi
-keep,allowobfuscation interface com.ovasta.sellers.presentation.profile.profile.data.ProfileApi
-keep,allowobfuscation interface com.ovasta.sellers.data.notification.FcmTokenApi

# Keep all methods in Retrofit interfaces
-keepclassmembernames,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# ---------- OkHttp ----------
# OkHttp/Okio ship their own consumer rules; only suppress warnings
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**

# ---------- Koin ----------
# Koin ships its own consumer rules; only suppress warnings and keep annotations
-dontwarn org.koin.**
-keepclassmembers class * {
    @org.koin.core.annotation.* <methods>;
}

# Keep ViewModels (Koin uses reflection to instantiate)
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Keep Koin modules
-keep class com.ovasta.sellers.di.** { *; }

# Keep classes injected by Koin
-keep class com.ovasta.sellers.**Repository* { *; }
-keep class com.ovasta.sellers.**UseCase* { *; }
-keep class com.ovasta.sellers.**DataSource* { *; }

# ---------- Firebase ----------
# Firebase/GMS ship their own consumer rules; only suppress warnings
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Firebase Crashlytics - keep for readable stack traces
-keep public class * extends java.lang.Exception

# ---------- Parcelize ----------
-keep class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# ---------- Jetpack Compose ----------
# Compose ships its own consumer rules; only suppress warnings
-dontwarn androidx.compose.**

# ---------- AndroidX Security Crypto ----------
# Security Crypto ships its own consumer rules; only suppress warnings
-dontwarn androidx.security.crypto.**
-dontwarn com.google.crypto.tink.**

# ---------- AndroidX DataStore ----------
# DataStore ships its own consumer rules; only suppress warnings
-dontwarn androidx.datastore.**

# ---------- Paging ----------
# Paging ships its own consumer rules; only suppress warnings
-dontwarn androidx.paging.**

# ---------- Coil ----------
# Coil ships its own consumer rules; only suppress warnings
-dontwarn coil.**

# ---------- Play Services Location ----------
-dontwarn com.google.android.gms.location.**

# ---------- Ktor (shared module networking) ----------
-dontwarn io.ktor.**
-dontwarn kotlinx.coroutines.debug.**