package com.gabrieleox.passwordmanager

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class DeleteViewModel<T> : ViewModel() {

    var toBeDeleted by mutableStateOf<T?>(null)
        private set

    val isDialogShown: Boolean
        get() = toBeDeleted != null

    /**
     * Used to call the dialog changing the ['isDialogShown'] and lets you set an object to delete: ['toBeDeleted']
     */
    fun requestDeleting(
        value: T
    ){
        toBeDeleted = value
    }

    /**
     * Always use after deleting the ['toBeDeleted'] object defined in the ['onDeleting()']
     */
    fun onDismiss(){
        toBeDeleted = null
    }

}