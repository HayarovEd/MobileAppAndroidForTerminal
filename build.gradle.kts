// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.ksp) apply false
}

/*

https://api.develop.cloud-laundry.ru/docs/public#/

Логинимся сюда, а дальше идём в зависимости от роли
manager - / manager /
client - / client /
client_employee - / terminal /*/
