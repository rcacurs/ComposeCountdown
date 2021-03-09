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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.request.RequestOptions
import com.example.androiddevchallenge.ui.theme.MyTheme
import dev.chrisbanes.accompanist.glide.GlideImage

private const val TAG = "Main Activity"
class MainActivity : AppCompatActivity() {
    val viewModel: TimerViewModel by viewModels()

    @ExperimentalAnimationApi
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "On create!")

        // viewModel.setRemainingMillis(5000L)
//        viewModel.start()
        setContent {
            MyTheme {
                MyApp(viewModel)
            }
        }
    }
}

// Start building your app here!
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun MyApp(viewModel: TimerViewModel) {

    Surface(color = MaterialTheme.colors.background) {
        Row(
            modifier = Modifier.fillMaxHeight().fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Timer(viewModel)
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun Timer(viewModel: TimerViewModel) {
    val remainingMinutes: Long by viewModel.remainingMinutes.observeAsState(0L)
    val remainingSeconds: Long by viewModel.remainingSeconds.observeAsState(0L)
    val remainingMilli: Long by viewModel.remainingMilli.observeAsState(0L)
    val finished: Boolean by viewModel.finished.observeAsState(false)
    val (showInput, setShowInput) = remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TimerDisplay(
            remainingMinutes, remainingSeconds, remainingMilli, finished, setShowInput
        )
        TimerControls(
            showInput,
            { viewModel.setRemainingMillis(it) },
            { viewModel.start(); setShowInput(false) },
            { viewModel.stop() }, { viewModel.clear() }
        )
    }
}

@Composable
fun TimerDisplay(minutes: Long, seconds: Long, milli: Long, finished: Boolean, onClick: (Boolean) -> Unit) {

    Crossfade(finished, animationSpec = tween(durationMillis = 1000)) {
        if (!it) {
            Row(
                modifier = Modifier.clickable(onClick = { onClick(true) })
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

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Preview("Timer Preview")
@Composable
fun TimerPreview() {
    MyTheme {
        Column {
            TimerDisplay(5, 5, 23, false, { it -> })
            TimerControls(true, {}, {}, {}, {})
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun TimerControls(
    showInput: Boolean,
    onTimeEdit: (millis: Long) -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onClear: () -> Unit
) {
    var textMinutes by remember { mutableStateOf("") }
    var textSeconds by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    var started by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),

    ) {
        AnimatedVisibility(
            visible = showInput
        ) {
            Row(
//                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = textMinutes,
                    onValueChange = { newValue ->
                        if (newValue.length < 3) {
                            textMinutes = newValue

                            onTimeEdit(
                                ((textMinutes.toLongOrNull() ?: 0) * 1000 * 60) +
                                    ((textSeconds.toLongOrNull() ?: 0) * 1000)
                            )
                        }
                    },
                    label = { Text("minutes") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hideSoftwareKeyboard()
                        }
                    ),
                    modifier = Modifier
                        .padding(start = 32.dp, top = 0.dp, bottom = 8.dp, end = 8.dp)
                        .width(90.dp)

                )
                Divider(
                    color = Color.Transparent,
                    modifier = Modifier
                        .height(10.dp)
                        .width(30.dp)
                )
                OutlinedTextField(
                    value = textSeconds,
                    onValueChange = { newValue ->
                        if (newValue.length < 4) {
                            textSeconds = newValue
                            onTimeEdit(
                                ((textMinutes.toLongOrNull() ?: 0) * 1000 * 60) +
                                    ((textSeconds.toLongOrNull() ?: 0) * 1000)
                            )
                        }
                    },
                    label = { Text("seconds") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hideSoftwareKeyboard()
                        }
                    ),
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .width(90.dp)

                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                modifier = Modifier.padding(8.dp),
                onClick = {
                    if (started) {
                        onStop()
                    } else {
                        onStart()
                    }
                    started = !started
                }
            ) {
                if (started) {
                    Text("Stop", style = MaterialTheme.typography.h4)
                } else {
                    Text("Start", style = MaterialTheme.typography.h4)
                }
            }
            Button(
                modifier = Modifier.padding(8.dp),
                onClick = { textMinutes = ""; textSeconds = ""; onClear() }
            ) {
                Text("Clear", style = MaterialTheme.typography.h4)
            }
        }
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
