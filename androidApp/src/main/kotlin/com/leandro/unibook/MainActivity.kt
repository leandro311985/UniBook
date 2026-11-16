package com.leandro.unibook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.leandro.unibook.di.initKoin
import com.leandro.unibook.worker.ReminderScheduler
import org.koin.android.ext.koin.androidContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initKoin {
            androidContext(this@MainActivity)
        }

        // Agendamento periódico de lembretes em background
        ReminderScheduler.schedulePeriodicReminders(this@MainActivity)

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
