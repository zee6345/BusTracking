package com.app.bustracking.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.bustracking.R
import com.app.bustracking.presentation.views.activities.MainActivity
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.PusherEvent
import com.pusher.client.channel.SubscriptionEventListener
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange

class AppService : Service(), ConnectionEventListener, SubscriptionEventListener {

    companion object {
        var alreadyRunning = false
    }

    private val timerHandler = Handler(Looper.getMainLooper())
    private var totalRunningTime: Long = 0
    private val timerRunnable: Runnable = object : Runnable {
        override fun run() {
            totalRunningTime += 1000 // Increment by 1 second
            // Broadcast the totalRunningTime value
            val broadcastIntent = Intent("app_timer_update")
            broadcastIntent.putExtra("total_running_time", totalRunningTime)
            LocalBroadcastManager.getInstance(this@AppService).sendBroadcast(broadcastIntent)

            timerHandler.postDelayed(this, 1000) // Schedule the timer to run every second
        }
    }


    private val PUSHER_APP_ID = 1695142
    private val PUSHER_APP_KEY = "1f3eac61c1534d7ca731"
    private val PUSHER_APP_SECRET = "820dcb7d16632e710184"

    private val pusherOptions by lazy {
        PusherOptions().setCluster("ap2")
    }
    private val pusher by lazy {
        Pusher(PUSHER_APP_KEY, pusherOptions)
    }
    private val channel by lazy {
        pusher.subscribe("seentul-tracking")
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.apply {
            val busId = getStringExtra("bus_id")
            busId?.let {
                channel.bind("${it}location", this@AppService)
            }
        }

        return START_STICKY
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager): String? {
        val channelId = "bustracking"
        val channelName = "Bus Tracking Service"
        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        // omitted the LED color
        channel.importance = NotificationManager.IMPORTANCE_NONE
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        notificationManager.createNotificationChannel(channel)
        return channelId
    }

    override fun onCreate() {
        super.onCreate()

        alreadyRunning = true

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannel(
                notificationManager
            ) else ""
        val notificationBuilder = NotificationCompat.Builder(this, channelId!!)
        val notification: Notification =
            notificationBuilder.setOngoing(true).setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_MAX).setContentIntent(pendingIntent)
                .setCategory(NotificationCompat.CATEGORY_SERVICE).build()

        startForeground(1002, notification)

        //content
        pusher.connect(this, ConnectionState.ALL)


    }

    private fun startTimer() {
        timerRunnable.run()
    }

    private fun stopTimer() {
        timerHandler.removeCallbacks(timerRunnable)
    }

    override fun onDestroy() {
        alreadyRunning = false

//        stopTimer()

        super.onDestroy()
    }

    override fun onConnectionStateChange(change: ConnectionStateChange?) {
        Log.e(
            "Pusher", "State changed from " + change!!.previousState + " to " + change.currentState
        )
    }

    override fun onError(message: String?, code: String?, e: Exception?) {
        Log.e(
            "Pusher",
            "There was a problem connecting! " + "\ncode: " + code + "\nmessage: " + message + "\nException: " + e
        )
    }

    override fun onEvent(event: PusherEvent?) {
        Log.e("Pusher", "Received event with data: " + event.toString());
        val jsonString = event.toString()
        if (jsonString.isNotEmpty()) {
            val intent = Intent("My_Action_Event")
            intent.putExtra("json_data", jsonString)
            sendBroadcast(intent)
        }
    }

    fun removeBackslashes(jsonString: String): String {
        // Use regular expression to remove backslashes
        return jsonString.replace("\\\\", "")
    }
}