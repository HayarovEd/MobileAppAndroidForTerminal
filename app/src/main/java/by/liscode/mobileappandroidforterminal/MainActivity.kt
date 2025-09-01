package by.liscode.mobileappandroidforterminal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import by.liscode.mobileappandroidforterminal.navigation.NavController
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MobileAppAndroidForTerminalTheme {
                NavController()
                //TakeWorkScreenRoot()
            }
        }
    }
}