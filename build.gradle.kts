// Archivo build.gradle.kts de la RAÍZ del proyecto (no el de /app)
// Las versiones salen de gradle/libs.versions.toml, así no vuelve a haber conflicto de versiones.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
}
