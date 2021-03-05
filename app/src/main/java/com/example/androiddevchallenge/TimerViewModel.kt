package com.example.androiddevchallenge

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

private const val TAG = "Timer View Model"
class TimerViewModel: ViewModel() {

    private val _remainingTime = MutableLiveData<Long>(10000L)

    val remainingTime: LiveData<Long>
        get() = _remainingTime

    private lateinit var timer: CountDownTimer

    fun setRemainingMillis(remaining: Long) {
        _remainingTime.value = remaining
    }


    fun start() {
        timer = object : CountDownTimer(remainingTime.value!!, 1000L) {
            override fun onFinish() {
                Log.d(TAG, "Timer finished!")

            }

            override fun onTick(millisUntilFinished: Long) {
                Log.d(TAG, "Timer clicked, remaining ${millisUntilFinished}")
                _remainingTime.value = millisUntilFinished
            }
        }
        timer.start()
    }
}