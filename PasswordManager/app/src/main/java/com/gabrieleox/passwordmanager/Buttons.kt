package com.gabrieleox.passwordmanager

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gabrieleox.passwordmanager.MainActivity.Companion.aliasList

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun CreationButton(
    folder: String,
    alias: String,
    password: String,
    modifier: Modifier = Modifier,
    enabled: Boolean
){
    val context = LocalContext.current
    val activity = remember(context) {
        context.findFragmentActivity()
    }

    FilledTonalIconButton(
        onClick = {
            if (activity == null) return@FilledTonalIconButton
            if (!checkAuth(context)) return@FilledTonalIconButton
            if (aliasList[folder]?.contains(alias) == true ) {
                Toast.makeText(context, "Una password con quel nome è già presente!!", Toast.LENGTH_SHORT).show()
                return@FilledTonalIconButton
            }
            if (alias == "com.gabrieleox.passwordmanager.name_list"){
                Toast.makeText(context, "Impossibile creare una password con quel nome!!", Toast.LENGTH_SHORT).show()
                return@FilledTonalIconButton
            }

            newKey("$folder:$alias")
            aliasList[folder]?.add(alias)
            saveNames(aliasList, context)
            val cipher = getCipherForEncrypt("$folder:$alias")

            showBiometricPrompt(activity, cipher, onAuthenticated = { authenticatedCipher ->
                savePassword("$folder:$alias", password.toByteArray(), cipher, context)
            }, onFail = { deleteKey("$folder:$alias") })
        },
        enabled = enabled,
        shape = RoundedCornerShape(50),
        modifier = modifier
            .scale(1.5f),
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Add"
        )
    }
}

@Composable
fun ShowButton(
    alias: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick:() -> Unit
) {
    FilledTonalButton(
        onClick = { onClick() },
        enabled = enabled,
        modifier = modifier,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Text(
            text = alias,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
fun Folder(
    folder: String,
    chName:(String) -> Unit,
    showOnClick:() -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val passwords = aliasList[folder]

    Surface(
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.5f.dp,
            color = MaterialTheme.colorScheme.outline
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { expanded = !expanded }
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )

    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = folder,
                style = MaterialTheme.typography.titleMedium
            )

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                passwords?.forEach { alias ->
                    ShowButton(
                        alias = alias,
                        onClick = {
                            chName(alias)
                            showOnClick()
                        }
                    )
                }
            }
        }
    }
}
