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
                Log.d(TAG, "Timer clicked, remaining $millisUntilFinished")
                _remainingTime.value = millisUntilFinished
            }
        }
        timer.start()
    }
}
