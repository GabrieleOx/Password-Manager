package com.gabrieleox.passwordmanager

import android.content.Context
import android.os.Build
import android.view.WindowManager
import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gabrieleox.passwordmanager.MainActivity.Companion.aliasList
import java.security.SecureRandom

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
    var folder by remember { mutableStateOf(
        if (aliasList.keys.toList().isNotEmpty()){
            aliasList.keys.toList().first()
        }else ""
    )}
    var folderMenu by remember { mutableStateOf(false) }
    var maiuscole by remember { mutableStateOf(true) }
    var numeri by remember { mutableStateOf(true) }
    var simboli by remember { mutableStateOf(true) }
    var lunghezza by remember { mutableIntStateOf(10) }
    val context = LocalContext.current
    var passwordVisibile by remember { mutableStateOf(false) }

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
                            .menuAnchor(
                                ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                                enabled = true
                            )
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

                OutlinedTextField(
                    value = password,
                    onValueChange = { it ->
                        if (modType){
                            password = it
                        }
                    },
                    readOnly = !modType,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    label = { Text("Password:") },
                    singleLine = true,
                    maxLines = 1,
                    textStyle = TextStyle(
                        fontSize = 16.sp
                    ),
                    visualTransformation = if (!passwordVisibile){
                        PasswordVisualTransformation()
                    }else {
                        VisualTransformation.None
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    trailingIcon = {
                        FilledTonalIconButton(
                            onClick = {
                                passwordVisibile = !passwordVisibile
                            }
                        ) {
                            Icon(
                                imageVector = if (passwordVisibile){
                                    Icons.Filled.Visibility
                                }else {
                                    Icons.Filled.VisibilityOff
                                },
                                contentDescription = if (passwordVisibile){
                                    "Password visibile"
                                }else {
                                    "Password oscurata"
                                }
                            )
                        }
                    }
                )

                if (!modType){
                    OutlinedTextField(
                        value = "$lunghezza",
                        onValueChange = { it ->
                            if (it.isDigitsOnly() && it != ""){
                                if(it.toInt() < 8){
                                    Toast.makeText(context, "Valore di lunghezza non valido (almeno 8) ...", Toast.LENGTH_SHORT).show()
                                }else lunghezza = it.toInt()
                            }else {
                                Toast.makeText(context, "Valore di lunghezza non valido (almeno 8)...", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        label = { Text("Lunghezza password:") },
                        singleLine = true,
                        maxLines = 1,
                        textStyle = TextStyle(
                            fontSize = 16.sp
                        )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "Maiuscole:",
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = maiuscole,
                            onCheckedChange = {
                                maiuscole = !maiuscole
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "Numeri:",
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = numeri,
                            onCheckedChange = {
                                numeri = !numeri
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "Simboli:",
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(
                            checked = simboli,
                            onCheckedChange = {
                                simboli = !simboli
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
                    FilledTonalButton(
                        onClick = {
                            password = passwordGenerator(
                                lunghezza,
                                maiuscole,
                                numeri,
                                simboli
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 36.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.inverseSurface,
                            disabledContainerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                            disabledContentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        elevation = ButtonDefaults.filledTonalButtonElevation(
                            defaultElevation = 3.dp,
                            pressedElevation = 7.dp,
                            focusedElevation = 5.dp,
                            hoveredElevation = 4.dp
                        )
                    ) {
                        Text(
                            text = "Genera password",
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                        )
                    }
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

fun passwordGenerator(
    lunghezza: Int,
    maiuscole: Boolean,
    numeri: Boolean,
    simboli: Boolean
): String {

    require(lunghezza >= 8) { "La lunghezza minima consigliata è 8" }

    val secureRandom = SecureRandom()

    val minuscole = ('a'..'z').toList()
    val maiuscoleList = ('A'..'Z').toList()
    val cifre = ('0'..'9').toList()
    val simboliSafe = listOf(
        '!', '#', '$', '%', '*', '+', '-', '.', ':', '=',
        '?', '@', '^', '_', '~'
    )

    val pool = mutableListOf<Char>()
    val password = mutableListOf<Char>()

    // garantisce almeno un carattere per categoria
    fun pick(list: List<Char>): Char =
        list[secureRandom.nextInt(list.size)]

    password.add(pick(minuscole))
    pool.addAll(minuscole)

    if (maiuscole) {
        password.add(pick(maiuscoleList))
        pool.addAll(maiuscoleList)
    }

    if (numeri) {
        password.add(pick(cifre))
        pool.addAll(cifre)
    }

    if (simboli) {
        password.add(pick(simboliSafe))
        pool.addAll(simboliSafe)
    }

    require(pool.isNotEmpty()) { "Nessun carattere disponibile" }

    while (password.size < lunghezza) {
        password.add(pick(pool))
    }

    // shuffle sicuro
    for (i in password.lastIndex downTo 1) {
        val j = secureRandom.nextInt(i + 1)
        val tmp = password[i]
        password[i] = password[j]
        password[j] = tmp
    }

    return password.joinToString("")
}



@Composable
fun EditingScreen(){
    val deleteFolder: DeleteViewModel<String> = viewModel()
    val newFolder: NewFolderViewModel = viewModel()
    var editing by remember { mutableStateOf(true) }
    var recompose by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (recompose){
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator()
        }
    }else {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ){
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ){
                Text(
                    text = "Modifica:",
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
                        text = if (editing){
                            "Cartelle:"
                        }else{
                            "Password:"
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
                        checked = editing,
                        onCheckedChange = {
                            editing = !editing
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
                if (editing){
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 40.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(aliasList.keys.toList()){ folder ->
                            FolderMod(
                                folderName = folder,
                                onDelete = {
                                    Toast.makeText(context, "Attenzione: cancellando la cartella cancellerai anche le password", Toast.LENGTH_LONG).show()
                                    deleteFolder.requestDeleting(folder)
                                    recompose = !recompose
                                }
                            )
                        }
                    }
                }
            }
            NewFolderButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 25.dp, end = 25.dp),
                enabled = editing,
                onClick = {
                    newFolder.onCreating()
                    recompose = !recompose
                }
            )
        }
    }

    if(deleteFolder.isDialogShown){
        DeleteDialog(
            onDismiss = {
                deleteFolder.onDismiss()
                recompose = !recompose
            },
            onConfirm = {
                if (deleteFolder.toBeDeleted != null){
                    deleteFolder(
                        folder = deleteFolder.toBeDeleted!!,
                        context = context
                    )
                }
                deleteFolder.onDismiss()
                recompose = !recompose
            }
        )
    } else if(newFolder.isDialogShown){
        NewFolderDialog(
            onDismiss = {
                newFolder.onDismiss()
                recompose = !recompose
            },
            onConfirm = {
                if (!newFolder.folderName.isNullOrBlank()){
                    aliasList[newFolder.folderName!!] = mutableListOf()
                    saveNames(aliasList, context)
                }
                newFolder.onDismiss()
                recompose = !recompose
            },
            viewModel = newFolder
        )
    }
}

fun deleteFolder(
    folder: String,
    context: Context
): Boolean {
    try{

        aliasList.remove(folder)?.forEach { passwordName ->
            deleteKey("$folder:$passwordName")
        }

        saveNames(
            context = context,
            names = aliasList
        )

    }catch (e: Exception){
        println(e.message)
        e.printStackTrace()
        return false
    }

    return true
}

@Composable
fun SettingsScreen(){
}