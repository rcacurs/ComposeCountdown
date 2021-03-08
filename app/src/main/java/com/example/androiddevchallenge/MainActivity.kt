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

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.bumptech.glide.request.RequestOptions
import com.example.androiddevchallenge.ui.theme.MyTheme
import dev.chrisbanes.accompanist.glide.GlideImage

private const val TAG = "Main Activity"
class MainActivity : AppCompatActivity() {
    val viewModel: TimerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "On create!")

        viewModel.setRemainingMillis(5000L)
        viewModel.start()
        setContent {
            MyTheme {
                MyApp(viewModel)
            }
        }
    }
}

// Start building your app here!
@Composable
fun MyApp(viewModel: TimerViewModel) {
    val remainingMinutes: Long by viewModel.remainingMinutes.observeAsState(0L)
    val remainingSeconds: Long by viewModel.remainingSeconds.observeAsState(0L)
    val remainingMilli: Long by viewModel.remainingMilli.observeAsState(0L)
    val finished: Boolean by viewModel.finished.observeAsState(false)
    Surface(color = MaterialTheme.colors.background) {
        TimerDisplay(
            remainingMinutes, remainingSeconds, remainingMilli, finished
        )
    }
}

@Composable
fun TimerDisplay(minutes: Long, seconds: Long, milli: Long, finished: Boolean) {

    Crossfade(finished, animationSpec = tween(durationMillis = 1000)) {
        if (!it) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "${minutes.toString().padStart(2, '0')}",
                    fontSize = 100.sp,
                    modifier = Modifier.alignByBaseline()
                )
                Text(
                    ":", fontSize = 100.sp,
                    modifier = Modifier.alignByBaseline()
                )

                Text(
                    "${seconds.toString().padStart(2, '0')}", fontSize = 100.sp,
                    modifier = Modifier.alignByBaseline()
                )

                Text(
                    ":", fontSize = 50.sp,
                    modifier = Modifier.alignByBaseline()
                )
                Text(
                    "${milli.toString().padStart(3, '0')}",
                    fontSize = 50.sp,
                    modifier = Modifier.alignByBaseline()
                )
            }
        } else {
            WinScreen()
        }
    }
}

@Preview("Timer Preview")
@Composable
fun TimerPreview() {
    MyTheme {
        TimerDisplay(5, 5, 23, false)
    }
}

@Composable
fun WinScreen() {
    GlideImage(
        data = R.drawable.confeti,
        contentDescription = "",
        requestBuilder = {
            val options = RequestOptions()
            options.fitCenter()
            options.centerCrop()

            apply(options)
        }
    )
}

// @Preview("Light Theme", widthDp = 360, heightDp = 640)
// @Composable
// fun LightPreview() {
//    MyTheme {
// //        MyApp()
//    }
// }
//
// @Preview("Dark Theme", widthDp = 360, heightDp = 640)
// @Composable
// fun DarkPreview() {
//    MyTheme(darkTheme = true) {
// //        MyApp()
//    }
// }
