/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

private const val TAG = "Timer View Model"
class TimerViewModel : ViewModel() {
    private val _finished = MutableLiveData<Boolean>(false)
    val finished: LiveData<Boolean>
        get() = _finished

    private val _remainingTime = MutableLiveData<Long>(0L)
    val remainingTime: LiveData<Long>
        get() = _remainingTime

    private val _remainingSeconds = MutableLiveData<Long>(0L)
    val remainingSeconds: LiveData<Long>
        get() = _remainingSeconds

    private val _remainingMilli = MutableLiveData<Long>(0L)
    val remainingMilli: LiveData<Long>
        get() = _remainingMilli

    private val _remainingMinutes = MutableLiveData<Long>(0L)
    val remainingMinutes: LiveData<Long>
        get() = _remainingMinutes

    private lateinit var timer: CountDownTimer

//    fun setRemainingMinutes(minutes: Long?) {
//        setRemainingMillis((minutes ?: 0) * 1000 * 60)
//    }
//
//    fun setRemainingSeconds(seconds: Long?) {
//        setRemainingMillis((seconds ?: 0 ) * 1000)
//    }

    fun setRemainingMillis(remaining: Long) {
        _finished.value = false
        _remainingTime.value = remaining
        _remainingMilli.value = remaining % 1000
        _remainingSeconds.value = (remaining / 1000) % 60
        _remainingMinutes.value = (remaining / 1000) / 60
    }

    fun start() {
        val step = 25L
        _finished.value = false
        timer = object : CountDownTimer(remainingTime.value!!, step) {
            override fun onFinish() {
                _finished.value = true
                _remainingTime.value = 0L
                _remainingSeconds.value = 0L
                _remainingMilli.value = 0L
                _remainingSeconds.value = 0L
                Log.d(TAG, "Timer finished!")
            }

            override fun onTick(millisUntilFinished: Long) {
                Log.d(TAG, "Timer clicked, remaining $millisUntilFinished")
                _remainingTime.value = _remainingTime.value!! - step
                _remainingMilli.value = _remainingTime.value!! % 1000
                _remainingSeconds.value = (_remainingTime.value!! / 1000) % 60
                _remainingMinutes.value = (_remainingTime.value!! / 1000) / 60
            }
        }
        timer.start()
    }

    fun stop() {
        timer.cancel()
    }

    fun clear() {
        setRemainingMillis(0L)
    }
}
