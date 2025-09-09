pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MobileAppAndroidForTerminal"
include(":app")
include(":data")
include(":domain")
include(":resources")
include(":settings")
include(":login")
include(":fund")
include(":regisration")
include(":splash")
include(":take_work")
include(":orders")
include(":take_balance")
