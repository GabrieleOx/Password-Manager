package com.gabrieleox.passwordmanager

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.gabrieleox.passwordmanager.MainActivity.Companion.aliasList

@Composable
fun DeleteDialog(
    onDismiss:() -> Unit,
    onConfirm:() -> Unit
) {
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(100.dp),
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp,
                pressedElevation = 2.dp,
                focusedElevation = 8.dp,
                hoveredElevation = 8.dp,
                disabledElevation = 0.dp
            ),
            border = BorderStroke(
                width = (1.5f).dp,
                color = MaterialTheme.colorScheme.outline
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp)
            ) {
                Text(
                    text = "Sei sicuro/a?",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.titleMedium
                )
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    FilledTonalButton(
                        onClick = { onConfirm() },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(
                            text = "Conferma",
                            textAlign = TextAlign.Left,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    FilledTonalButton(
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(
                            text = "Annulla",
                            textAlign = TextAlign.Right,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NewFolderDialog(
    viewModel: NewFolderViewModel,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
){
    var alreadyPresent by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(170.dp),
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp,
                pressedElevation = 2.dp,
                focusedElevation = 8.dp,
                hoveredElevation = 8.dp,
                disabledElevation = 0.dp
            ),
            border = BorderStroke(
                width = (1.5f).dp,
                color = MaterialTheme.colorScheme.outline
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp)
            ) {
                Text(
                    text = "Nuova Cartella:",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
                OutlinedTextField(
                    value = viewModel.folderName?: "",
                    onValueChange = {
                        viewModel.folderName = it
                        if (it in aliasList){
                            alreadyPresent = true
                            Toast.makeText(context, "Cartella già presente!!!", Toast.LENGTH_SHORT).show()
                        }else alreadyPresent = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    label = { Text("Nome della cartella:") },
                    singleLine = true,
                    maxLines = 1,
                    textStyle = TextStyle(
                        fontSize = 16.sp
                    )
                )
                FilledTonalButton(
                    onClick = onConfirm,
                    enabled = !alreadyPresent,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 10.dp)
                ) {
                    Text(
                        text = "Crea",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
    }
}