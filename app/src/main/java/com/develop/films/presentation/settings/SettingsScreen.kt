package com.develop.films.presentation.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onSignOut: () -> Unit,
    onSignInSuccess: () -> Unit
) {
    val context = LocalContext.current
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var account by remember { mutableStateOf<GoogleSignInAccount?>(null) }

    val googleSignInClient = remember(context) {
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val signedAccount = task.getResult(ApiException::class.java)
                if (signedAccount != null) {
                    account = signedAccount
                    errorMessage = null
                    onSignInSuccess()
                } else {
                    errorMessage = "Не удалось получить аккаунт Google"
                }
            } catch (e: ApiException) {
                errorMessage = "Ошибка входа: ${e.statusCode}"
            }
        } else {
            errorMessage = "Вход отменён"
        }
    }

    LaunchedEffect(Unit) {
        account = GoogleSignIn.getLastSignedInAccount(context)
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.padding(12.dp))

            if (account != null) {
                Text(
                    text = "Вход выполнен как",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = account?.displayName.orEmpty(),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = account?.email.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.padding(20.dp))
                Button(onClick = {
                    googleSignInClient.signOut().addOnCompleteListener {
                        account = null
                        errorMessage = null
                        onSignOut()
                    }
                }) {
                    Text(text = "Выйти из аккаунта")
                }
            } else {
                Text(
                    text = "Пользователь не авторизован",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.padding(12.dp))
                Button(onClick = {
                    launcher.launch(googleSignInClient.signInIntent)
                }) {
                    Text(text = "Войти в аккаунт")
                }
            }

            if (!errorMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.padding(16.dp))
                Text(
                    text = errorMessage.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
