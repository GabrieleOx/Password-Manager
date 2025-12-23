package com.gabrieleox.passwordmanager

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PasswordViewModel : ViewModel() {

    private val _password = MutableStateFlow<String?>(null)
    val password = _password.asStateFlow()

    fun requestPassword(
        entryId: String,
        context: Context,
        activity: FragmentActivity?
    ) {
        if (activity == null) return
        if (!checkAuth(context)) return

        val pair = seePassword(entryId, context) ?: return
        val (encryptedData, iv) = pair

        val cipher = getCipherForDecrypt(entryId, iv)

        showBiometricPrompt(activity, cipher, { authenticatedCipher ->
            val decryptedData = decrypt(
                authenticatedCipher,
                encryptedData
            )
            _password.value = decryptedData.toString(Charsets.UTF_8)
        })

        viewModelScope.launch {
            delay(10_000)
            _password.value = null
        }
    }

    fun clearPassword() {
        _password.value = null
    }
}
