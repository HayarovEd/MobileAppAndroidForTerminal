package by.liscode.mobileappandroidforterminal.di


import by.liscode.fund.FundScreenViewModel
import by.liscode.login.LoginScreenViewModel
import by.liscode.regisration.RegistrationScreenViewModel
import by.liscode.settings.SettingsScreenViewModel
import by.liscode.splash.SplashScreenViewModel
import com.abelfma.take_balance.TakeBalanceScreenViewModel
import com.edurda77.take_work.TakeWorkScreenViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ru.modulmoney.write_off.WriteOffScreenViewModel

val viewModelModule = module {
    viewModelOf(::SettingsScreenViewModel)
    viewModelOf(::LoginScreenViewModel)
    viewModelOf(::RegistrationScreenViewModel)
    viewModelOf(::SplashScreenViewModel)
    viewModelOf(::TakeWorkScreenViewModel)
    viewModelOf(::FundScreenViewModel)
    viewModelOf(::TakeBalanceScreenViewModel)
    viewModelOf(::WriteOffScreenViewModel)
}

