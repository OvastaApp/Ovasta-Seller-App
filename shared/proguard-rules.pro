# Ktor
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }

# kotlinx.serialization
-keep,includedescriptorclasses class com.ovasta.sellers.**$$serializer { *; }
-keepclassmembers class com.ovasta.sellers.** {
    *** Companion;
}
-keepclasseswithmembers class com.ovasta.sellers.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep class com.ovasta.sellers.data.** { *; }

# Koin
-keepclassmembers class * {
    @org.koin.core.annotation.Single *;
    @org.koin.core.annotation.Factory *;
}

# Compose Multiplatform
-dontwarn org.jetbrains.skia.**
-dontwarn org.jetbrains.skiko.**

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
