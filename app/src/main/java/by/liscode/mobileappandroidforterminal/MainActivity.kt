package by.liscode.mobileappandroidforterminal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme
import com.edurda77.take_work.TakeWorkScreenRoot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MobileAppAndroidForTerminalTheme {
                //NavController()
                TakeWorkScreenRoot()
            }
        }
    }
}