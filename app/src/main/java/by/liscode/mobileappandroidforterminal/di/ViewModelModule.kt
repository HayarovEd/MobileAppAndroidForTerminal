package by.liscode.mobileappandroidforterminal.di


import by.liscode.fund.FundScreenViewModel
import by.liscode.login.LoginScreenViewModel
import by.liscode.orders.OrdersScreenViewModel
import by.liscode.regisration.RegistrationScreenViewModel
import by.liscode.settings.SettingsScreenViewModel
import by.liscode.splash.SplashScreenViewModel
import by.liscode.search.SearchScreenViewModel
import by.liscode.take_balance.TakeBalanceScreenViewModel
import com.edurda77.take_work.TakeWorkScreenViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import by.liscode.inventory.CurrentOrderScreenViewModel
import by.liscode.inventory.InventoryScreenViewModel
import ru.modulmoney.order_create.OrderCreateScreenViewModel
import by.liscode.write_off.WriteOffScreenViewModel

val viewModelModule = module {
    viewModelOf(::SettingsScreenViewModel)
    viewModelOf(::LoginScreenViewModel)
    viewModelOf(::RegistrationScreenViewModel)
    viewModelOf(::SplashScreenViewModel)
    viewModelOf(::TakeWorkScreenViewModel)
    viewModelOf(::FundScreenViewModel)
    viewModelOf(::TakeBalanceScreenViewModel)
    viewModelOf(::WriteOffScreenViewModel)
    viewModelOf(::InventoryScreenViewModel)
    viewModelOf(::SearchScreenViewModel)
    viewModelOf(::OrdersScreenViewModel)
    viewModelOf(::OrderCreateScreenViewModel)
    viewModelOf(::CurrentOrderScreenViewModel)
}

