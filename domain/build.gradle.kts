plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}


dependencies {
    implementation(libs.kotlinx.coroutines.android)
    //api(libs.kotlinx.datetime)
}