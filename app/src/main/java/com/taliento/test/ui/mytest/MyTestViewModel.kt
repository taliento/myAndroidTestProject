/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.taliento.test.ui.mytest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.taliento.test.data.MyTestRepository
import com.taliento.test.ui.mytest.MyTestUiState.Error
import com.taliento.test.ui.mytest.MyTestUiState.Loading
import com.taliento.test.ui.mytest.MyTestUiState.Success
import javax.inject.Inject

@HiltViewModel
class MyTestViewModel @Inject constructor(
    private val myTestRepository: MyTestRepository
) : ViewModel() {

    val uiState: StateFlow<MyTestUiState> = myTestRepository
        .myTests.map<List<String>, MyTestUiState>(::Success)
        .catch { emit(Error(it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    fun addMyTest(name: String) {
        viewModelScope.launch {
            myTestRepository.add(name)
        }
    }
}

sealed interface MyTestUiState {
    data object Loading : MyTestUiState
    data class Error(val throwable: Throwable) : MyTestUiState
    data class Success(val data: List<String>) : MyTestUiState
}
