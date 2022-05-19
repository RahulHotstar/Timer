package rahul.das.timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _timerStatus = MutableLiveData<TimerStatus>().apply {
        value = TimerStatus.NOT_STARTED
    }

    val timerStatus: LiveData<TimerStatus> = _timerStatus


    fun updateRunning(running: Boolean) {
        if (running) {
            _timerStatus.value = TimerStatus.RUNNING
        } else {
            _timerStatus.value = TimerStatus.NOT_STARTED
        }
    }

}