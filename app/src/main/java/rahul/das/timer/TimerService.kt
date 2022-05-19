package rahul.das.timer

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.*

class TimerService : Service() {
    private val TAG = "TimerService"

    private var startTime = 0L
    private var endTime = 0L
    private var context: Context? = null
    private var max = 0L

    private var isTimerRunning = false
    private val binder:IBinder = TimerBinder()


    val TIMER_NOTIFCATION_ID = 1234

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("Hello", "Service on start command")
        return START_STICKY
    }

    override fun onCreate() {
        startTime = 0;
        endTime = 0;
        context = this
        max = PreferenceHelper().getMaxTime(this)
        Log.i("Hello", "Service was created")
    }

    inner class TimerBinder : Binder() {
        fun getService(): TimerService {
            return this@TimerService
        }
    }


    override fun onBind(intent: Intent?): IBinder {
        Log.i("Hello", "Binding service")
        return binder
    }

    public fun isTimerRunning() = isTimerRunning

    public fun sendNotification() {
        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(this)
            .setContentTitle("Timer App")
            .setContentText("Current time "+getTime())
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pi)
        startForeground(TIMER_NOTIFCATION_ID, builder.build())
    }


    public fun pushServiceToBackground() {
        stopForeground(false)
    }

    public fun getTime(): Long {
        if(max<System.currentTimeMillis()){
            stopSelf()
            return 0
        }

        return if (endTime > startTime) {
            (endTime - startTime) / 1000
        } else {
            (System.currentTimeMillis() - startTime) / 1000
        }
    }

    public fun startTimer() {
        if (!isTimerRunning) {
            startTime = System.currentTimeMillis()
            isTimerRunning = true
            max = PreferenceHelper().getMaxTime(this)
        }
    }

    public fun stopTimer() {
        if (isTimerRunning) {
            isTimerRunning = false
        }
    }
}