package com.gabrieleox.passwordmanager

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

fun checkAuth(
    context: Context
): Boolean {
    val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    if (!keyguardManager.isDeviceSecure) {
        Toast.makeText(context, "Errore: il tuo dispositivo non ha autenticazione...", Toast.LENGTH_SHORT).show()
        return false
    }else return true
}

@RequiresApi(Build.VERSION_CODES.R)
fun newKey(
    nameAlias: String
) {
    if (!keyStore.containsAlias(nameAlias)){
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")

        val params = KeyGenParameterSpec.Builder(
            nameAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setUserAuthenticationRequired(true)
            .setUserAuthenticationParameters(0, KeyProperties.AUTH_BIOMETRIC_STRONG or KeyProperties.AUTH_DEVICE_CREDENTIAL)
            .build()

        generator.init(params)
        generator.generateKey()
    }
}

fun deleteKey(
    nameAlias: String
) {
    if (keyStore.containsAlias(nameAlias)){
        keyStore.deleteEntry(nameAlias)
    }
}

fun getCipherForEncrypt(alias: String): Cipher {
    val secretKey = keyStore.getKey(alias, null) as SecretKey

    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    return cipher
}


fun encrypt(
    cipher: Cipher,
    data: ByteArray
): Pair<ByteArray, ByteArray> {
    val encrypted = cipher.doFinal(data)
    val iv = cipher.iv

    return Pair(encrypted, iv)
}

fun getCipherForDecrypt(alias: String, iv: ByteArray): Cipher {
    val secretKey = keyStore.getKey(alias, null) as SecretKey

    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, iv))
    return cipher
}


fun decrypt(
    cipher: Cipher,
    encrypted: ByteArray,
): ByteArray {
    return cipher.doFinal(encrypted)
}

fun showBiometricPrompt(
    activity: FragmentActivity,
    cipher: Cipher,
    onAuthenticated: (Cipher) -> Unit,
    onFail:() -> Unit = {}
) {
    val executor = ContextCompat.getMainExecutor(activity)

    val biometricPrompt = BiometricPrompt(
        activity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                result.cryptoObject?.cipher?.let { onAuthenticated(it) }
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                onFail()
            }

            override fun onAuthenticationFailed() {
                onFail()
            }
        }
    )

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Autenticazione richiesta:")
        .setDeviceCredentialAllowed(true) // questo risolve il crash
        .build()

    biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
}
