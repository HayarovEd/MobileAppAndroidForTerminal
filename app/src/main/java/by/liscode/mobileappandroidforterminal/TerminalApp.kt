package by.liscode.mobileappandroidforterminal

import android.app.Application
import by.liscode.mobileappandroidforterminal.di.baseModule
import by.liscode.mobileappandroidforterminal.di.repoModule
import by.liscode.mobileappandroidforterminal.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TerminalApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@TerminalApp)
            modules(
                baseModule, viewModelModule, repoModule
            )
        }
    }
}