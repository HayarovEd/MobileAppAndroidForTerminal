package by.liscode.resources.uikit

import android.content.Context
import android.util.Log
import android.view.KeyEvent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.rscja.deviceapi.RFIDWithUHFUART


@Composable
fun rememberRfid(): RFIDWithUHFUART {
    val context = LocalContext.current
    val mReader = remember {
        RFIDWithUHFUART.getInstance()
    }
    val lifecycleObserver = rememberMapLifecycleObserver(
        mReader = mReader,
        context = context
    )
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }
    return mReader
}

@Composable
fun rememberMapLifecycleObserver(
    mReader: RFIDWithUHFUART,
    context: Context
): LifecycleEventObserver =
    remember(mReader) {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    mReader.stopInventory()
                    Log.d("REST TERMINAL", "on stop")
                }

                Lifecycle.Event.ON_CREATE -> {
                    Log.d("REST TERMINAL", "on create")
                }

                Lifecycle.Event.ON_RESUME -> {
                    mReader.init(context)
                    Log.d("REST TERMINAL", "on resume")
                }


                Lifecycle.Event.ON_DESTROY -> {
                    mReader.free()
                    Log.d("REST TERMINAL", "on pause")
                }

                else -> {}
            }
        }
    }

@Composable
fun OnScan(
    block: () -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    DisposableEffect(context) {
        val keyEventDispatcher = ViewCompat.OnUnhandledKeyEventListenerCompat { _, event ->
            if (event.action == KeyEvent.ACTION_UP) {
                Log.d("REST TERMINAL", "action ${event.keyCode}")
                when (event.keyCode) {
                    KeyEvent.KEYCODE_F9,
                    KeyEvent.KEYCODE_SYSTEM_NAVIGATION_UP,
                    KeyEvent.KEYCODE_VIDEO_APP_3,
                    KeyEvent.KEYCODE_VIDEO_APP_5,
                    KeyEvent.KEYCODE_VIDEO_APP_6,
                    KeyEvent.KEYCODE_STYLUS_BUTTON_TAIL,
                    KeyEvent.KEYCODE_RECENT_APPS,
                    KeyEvent.KEYCODE_MACRO_1,
                    KeyEvent.KEYCODE_MACRO_3 -> {
                        Log.d("REST TERMINAL", "result action")
                        block()
                    }
                }
            }
            true
        }

        ViewCompat.addOnUnhandledKeyEventListener(view, keyEventDispatcher)
        onDispose {
            ViewCompat.removeOnUnhandledKeyEventListener(view, keyEventDispatcher)
        }
    }
}

