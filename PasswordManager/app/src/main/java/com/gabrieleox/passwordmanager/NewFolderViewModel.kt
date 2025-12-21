package com.gabrieleox.passwordmanager

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class NewFolderViewModel : ViewModel() {

    var folderName by mutableStateOf<String?>(null)

    var isDialogShown by mutableStateOf(false)
        private set

    fun onCreating(){
        isDialogShown = true
    }

    fun onDismiss(){
        folderName = null
        isDialogShown = false
    }

}