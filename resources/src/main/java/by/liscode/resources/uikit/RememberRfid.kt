package by.liscode.resources.uikit

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.rscja.deviceapi.RFIDWithUHFUART


@Composable
fun rememberRfid(context: Context): RFIDWithUHFUART {
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

