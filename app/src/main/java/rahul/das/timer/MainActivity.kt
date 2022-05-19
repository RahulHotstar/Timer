package rahul.das.timer

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import rahul.das.timer.databinding.ActivityMainBinding
import java.util.*
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private var timerService: TimerService? = null
    private var serviceBound = false
    private val handler = SecondHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        viewModel.timerStatus.observe(this, { status ->
            if (status == TimerStatus.NOT_STARTED) {
                showStartUI()
            } else {
                showTimerUI()
            }
        })

        binding.btnStart.setOnClickListener {
            val text = binding.etMinutes.text
            if (!text.isNullOrEmpty()) {
                PreferenceHelper().saveMaxTime(text.toString().toInt(), this)
                startTimer()
            }
        }

        binding.btnEnd.setOnClickListener {
            val text = binding.etMinutes.text
            if (!text.isNullOrEmpty()) {
                stopTimer()
            }
        }

    }

    private fun startTimer(){
        if(serviceBound){
            timerService?.startTimer()
            viewModel.updateRunning(true)
        }
    }

    private fun stopTimer(){
        if(serviceBound){
            timerService?.stopTimer()
            viewModel.updateRunning(false)
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, TimerService::class.java)
        startService(intent)

        bindService(intent, serviceConnection, 0)
        Log.i("Hello", "requested start Service onStart()")
    }

    override fun onStop() {
        super.onStop()
//        if (serviceBound) {
//            if (timerService?.isTimerRunning()==true) {
//                timerService?.sendNotification()
//            } else {
//                stopService(Intent(this, TimerService::class.java))
//            }
//            // Unbind the service
//            unbindService(serviceConnection)
//            serviceBound = false
//        }
    }


    private fun showTimerUI() {
        binding.btnEnd.visibility = View.VISIBLE
        binding.tvTimer.visibility = View.VISIBLE

        binding.tvHint.visibility = View.GONE
        binding.etMinutes.visibility = View.GONE
        binding.btnStart.visibility = View.GONE

        handler.sendEmptyMessage(0)
    }

    private fun showStartUI() {
        binding.btnEnd.visibility = View.GONE
        binding.tvTimer.visibility = View.GONE

        binding.tvHint.visibility = View.VISIBLE
        binding.etMinutes.visibility = View.VISIBLE
        binding.btnStart.visibility = View.VISIBLE

        handler.removeMessages(0)
    }

    private fun updateUITime() {
        val time = timerService?.getTime()
        if(time==0L){
            viewModel.updateRunning(false)
            return
        }
        val text = "$time Seconds"
        binding.tvTimer.text = text
    }


    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.i("Hello", "Service was connected")
            val binder: TimerService.TimerBinder = service as TimerService.TimerBinder
            timerService = binder.getService()
            serviceBound = true
            timerService?.pushServiceToBackground()
            viewModel.updateRunning(timerService?.isTimerRunning() == true)

        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBound = false
            Log.i("Hello", "Service was disconnected")
        }
    }


    private inner class SecondHandler : Handler() {

        override fun handleMessage(msg: Message) {
            if (msg.what == 0) {//for updating the time
                if (!isDestroyed)
                    updateUITime()
                sendEmptyMessageDelayed(0, 1000)
            }
        }

    }


}