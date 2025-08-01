package by.liscode.mobileappandroidforterminal.di


import by.liscode.settings.SettingsScreenViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::SettingsScreenViewModel)

   /*
    viewModelOf(::PlayerScreenViewModel)
    viewModelOf(::ChannelsScreenViewModel)
    viewModelOf(::SplashScreenViewModel)*/
}
