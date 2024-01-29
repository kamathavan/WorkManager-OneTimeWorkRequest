package com.workmanager.onetimerequest.notify

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotifyOneTimeRequest(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        TODO("Not yet implemented")
    }

    companion object {
        const val NOTIFICATION_ID = "appName_notification_id"
        const val NOTIFICATION_NAME = "appName"
        const val NOTIFICATION_CHANNEL = "appName_channel_01"
        const val NOTIFICATION_WORK = "appName_notification_work"
    }

}