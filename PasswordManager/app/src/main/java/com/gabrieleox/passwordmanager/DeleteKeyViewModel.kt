package com.gabrieleox.passwordmanager

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class DeleteKeyViewModel : ViewModel() {

    var isDialogShown by mutableStateOf(false)
        private set

    fun onDeleting(){
        isDialogShown = true
    }

    fun onDismiss(){
        isDialogShown = false
    }

}