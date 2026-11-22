package com.example.dsy1105_005d_2026.ui.theme
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import androidx.core.content.ContextCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dsy1105_005d_2026.R
import com.example.dsy1105_005d_2026.viewmodel.AppViewModelFactory
import com.example.dsy1105_005d_2026.viewmodel.LoginViewModel

/**
 * Pantalla de login. Valida correo y contraseña contra Room y, según el rol del usuario,
 * navega a la página de su perfil: cliente_home, admin_home o tecnico_home.
 */
@Composable
fun HomeScreen(
    navController: NavController,
    vm: LoginViewModel = viewModel(factory = AppViewModelFactory(LocalContext.current))
) {
    val state = vm.uiState
    var showPass by remember { mutableStateOf(false) }
<<<<<<< HEAD
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val biometricDisponible = BiometricManager.from(context).canAuthenticate(
        BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.DEVICE_CREDENTIAL
    ) == BiometricManager.BIOMETRIC_SUCCESS

    fun iniciarConBiometria() {
        if (activity == null) return
        val prompt = BiometricPrompt(
            activity,
            ContextCompat.getMainExecutor(context),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    vm.biometricLogin(state.correo) { rol ->
                        navController.navigate(rol.rutaInicio) {
                            popUpTo("login") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            }
        )
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Acceso biométrico")
            .setSubtitle("Confirma tu identidad para entrar a tu cuenta")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()
        prompt.authenticate(info)
    }
=======
>>>>>>> c1ae68d71e4def1f1aa11c0b38ba3da892369171

    MaterialTheme(colorScheme = ColoresApp) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logoduoc),
                contentDescription = "Logo App",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Bienvenido",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Inicia sesión para continuar",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = state.correo,
                        onValueChange = vm::onCorreoChange,
                        label = { Text("Correo") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = state.password,
                        onValueChange = vm::onPasswordChange,
                        label = { Text("Contraseña") },
                        singleLine = true,
                        visualTransformation = if (showPass)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPass = !showPass }) {
                                Icon(
                                    imageVector = if (showPass) Icons.Default.VisibilityOff
                                    else Icons.Default.Visibility,
                                    contentDescription = if (showPass) "Ocultar" else "Ver"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (state.error != null) {
                        Text(
                            text = state.error,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            vm.submit { rol ->
                                // Cada perfil tiene su propia página de inicio
                                navController.navigate(rol.rutaInicio) {
                                    popUpTo("login") { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        },
                        enabled = !state.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(if (state.isLoading) "Validando..." else "Iniciar sesión")
                    }

<<<<<<< HEAD
                    if (biometricDisponible) {
                        OutlinedButton(
                            onClick = { iniciarConBiometria() },
                            enabled = state.correo.isNotBlank() && !state.isLoading,
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Entrar con huella o reconocimiento del dispositivo")
                        }
                    } else {
                        Text(
                            "Acceso biométrico no disponible en este dispositivo. Usa correo y contraseña.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

=======
>>>>>>> c1ae68d71e4def1f1aa11c0b38ba3da892369171
                    TextButton(
                        onClick = { navController.navigate("registro") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("¿No tienes cuenta? Regístrate")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Cuentas de prueba (clave 123456):\n" +
                    "cliente@duoc.cl · admin@duoc.cl · tecnico@duoc.cl",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
