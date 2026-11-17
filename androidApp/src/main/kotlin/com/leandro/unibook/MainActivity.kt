package com.leandro.unibook

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.leandro.unibook.di.initKoin
import com.leandro.unibook.worker.ReminderScheduler
import org.koin.android.ext.koin.androidContext

class MainActivity : ComponentActivity() {
    private val notificationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                Log.w(TAG, "Notification permission was denied; reminders cannot be shown.")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initKoin {
            androidContext(this@MainActivity)
        }

        requestNotificationPermissionIfNeeded()

        // Agendamento periódico de lembretes em background
        ReminderScheduler.schedulePeriodicReminders(this@MainActivity)

        setContent {
            App()
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private companion object {
        const val TAG = "MainActivity"
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
