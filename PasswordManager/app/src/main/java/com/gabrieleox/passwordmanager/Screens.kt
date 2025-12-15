package com.gabrieleox.passwordmanager

import android.os.Build
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gabrieleox.passwordmanager.MainActivity.Companion.aliasList

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun HomeScreen() {
    val (passwordSeeing, chSeeing) = remember { mutableStateOf(false) }
    var folderSelected by remember { mutableStateOf("") }
    val (nameSelected, chName) = remember { mutableStateOf("") }
    if (passwordSeeing){
        PasswordScreen(
            folder = folderSelected,
            name = nameSelected,
            chSeeing = chSeeing
        )
    }else {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Le tue password:",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
            LazyColumn (
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(aliasList.keys.toList()){ folder ->
                    Folder(
                        folder = folder,
                        chName = chName,
                        showOnClick = {
                            folderSelected = folder
                            chSeeing(true)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PasswordScreen(
    folder: String,
    name: String,
    viewModel: PasswordViewModel = viewModel(),
    chSeeing:(Boolean) -> Unit
){
    BackHandler {
        chSeeing(false)
    }

    val context = LocalContext.current
    val activity = context.findFragmentActivity()
    val password by viewModel.password.collectAsState()

    DisposableEffect(Unit) {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)

        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.clearPassword()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ){
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ){
            if (password == null) {
                Button(
                    onClick = {
                        viewModel.requestPassword("$folder:$name", context, activity)
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    )
                ) {
                    Text("Mostra password", color = Color.White)
                }
            } else {
                Text("Cartella: $folder", color = Color.White, modifier = Modifier.align(Alignment.CenterHorizontally))
                Text("Nome: $name", color = Color.White, modifier = Modifier.align(Alignment.CenterHorizontally))
                Text("Password: ${password!!}", color = Color.White, modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
        Button(
            onClick = {
                chSeeing(false)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
        ) {
            Text(text = "Indietro", color = Color.White)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun CreationScreen() {
    var modType by remember { mutableStateOf(true) }
    var titolo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var folder by remember { mutableStateOf(aliasList.keys.toList().first()) }
    var folderMenu by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ){
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(35.dp)
        ) {
            Text(
                text = "Crea una password:",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = if (modType){
                        "Manuale:"
                    }else{
                        "Automatico:"
                    },
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = modType,
                    onCheckedChange = {
                        modType = !modType
                    },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = MaterialTheme.colorScheme.inverseSurface,
                        checkedThumbColor = MaterialTheme.colorScheme.inverseOnSurface,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surface,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(horizontal = 10.dp)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = folderMenu,
                    onExpandedChange = { folderMenu = !folderMenu },
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                ) {
                    OutlinedTextField(
                        value = folder,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Cartella") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(folderMenu)
                        },
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = folderMenu,
                        onDismissRequest = { folderMenu = false }
                    ) {
                        aliasList.keys.toList().forEach { it ->
                            DropdownMenuItem(
                                text = { Text(text = it) },
                                onClick = {
                                    folder = it
                                    folderMenu = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = titolo,
                    onValueChange = { titolo = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    label = { Text("Nome:") },
                    singleLine = true,
                    maxLines = 1,
                    textStyle = TextStyle(
                        fontSize = 16.sp
                    )
                )

                if (!modType){
                }else {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal =  24.dp),
                        label = { Text("Password:") },
                        singleLine = true,
                        maxLines = 1,
                        textStyle = TextStyle(
                            fontSize = 16.sp
                        )
                    )
                }
            }
        }
        CreationButton(
            folder = folder,
            alias = titolo,
            password = password,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 25.dp, bottom = 25.dp),
            enabled = password.isNotBlank() and titolo.isNotBlank() and folder.isNotBlank()
        )
    }
}

@Composable
fun EditingScreen(){
}

@Composable
fun SettingsScreen(){
}