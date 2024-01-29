package com.workmanager.onetimerequest

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.make
import com.workmanager.onetimerequest.databinding.ActivityTimeScheduleBinding
import com.workmanager.onetimerequest.notify.NotifyOneTimeRequest
import com.workmanager.onetimerequest.notify.NotifyOneTimeRequest.Companion.NOTIFICATION_ID
import com.workmanager.onetimerequest.notify.NotifyOneTimeRequest.Companion.NOTIFICATION_WORK
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class TimeScheduleActivity : AppCompatActivity() {

    private lateinit var getNotificationPermission: ActivityResultLauncher<String>
    private lateinit var binding: ActivityTimeScheduleBinding

    var isPermissionEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimeScheduleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        getNotificationPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isPermissionGranted: Boolean ->
                isPermissionEnabled = isPermissionGranted
            }

        setCalenderInputValue()
        checkPermission()
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                isPermissionEnabled = true
            } else {
                isPermissionEnabled = false
                getNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            isPermissionEnabled = true
        }
    }

    private fun setCalenderInputValue() {
        setSupportActionBar(binding.toolbar)

        val titleNotification = getString(R.string.notification_title)
        binding.collapsingToolbarLayout.title = titleNotification

        binding.doneFab.setOnClickListener {

            if (isPermissionEnabled) {
                val customCalendar = Calendar.getInstance()
                customCalendar.set(
                    binding.datePicker.year,
                    binding.datePicker.month,
                    binding.datePicker.dayOfMonth,
                    binding.timePicker.hour,
                    binding.timePicker.minute,
                    0
                )
                val customTime = customCalendar.timeInMillis
                val currentTime = System.currentTimeMillis()

                if (customTime > currentTime) {
                    val data = Data.Builder().putInt(NOTIFICATION_ID, 0).build()
                    val delay = customTime - currentTime
                    scheduleNotification(delay, data)

                    val titleNotificationSchedule = getString(R.string.notification_schedule_title)
                    val patternNotificationSchedule =
                        getString(R.string.notification_schedule_pattern)

                    val scheduledTime = titleNotificationSchedule + SimpleDateFormat(
                        patternNotificationSchedule,
                        Locale.getDefault()
                    ).format(customCalendar.time).toString()
                    make(binding.coordinatorLayout, scheduledTime, Snackbar.LENGTH_LONG).show()
                } else {
                    val errorNotificationSchedule = getString(R.string.notification_schedule_error)
                    make(
                        binding.coordinatorLayout,
                        errorNotificationSchedule,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    getNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun showSnakeBar(view: View, time: String) {
        make(
            view,
            time,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun scheduleNotification(delay: Long, data: Data) {
        val notificationWork = OneTimeWorkRequest.Builder(NotifyOneTimeRequest::class.java)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS).setInputData(data).build()

        WorkManager.getInstance(this).apply {
            beginUniqueWork(
                NOTIFICATION_WORK,
                ExistingWorkPolicy.REPLACE,
                notificationWork
            ).enqueue()
        }
    }
}

