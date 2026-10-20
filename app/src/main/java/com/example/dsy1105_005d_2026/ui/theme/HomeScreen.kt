package com.example.dsy1105_005d_2026.ui.theme


import com.example.dsy1105_005d_2026.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.dsy1105_005d_2026.ui.login.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun HomeScreen(
        navController: NavController,
        vm: LoginViewModel= viewModel()
){
    val state=vm.uiState
    var showPass by remember { mutableStateOf(false) }

    // darkScheme es una funcion del material 3
    
    val ColorScheme = darkColorScheme(
        primary= Color(0xFF8D1421),
        onPrimary =Color.White,
        onSurface =Color(0xFF333333)
    ) // fin dark
    
    MaterialTheme(
        colorScheme=ColorScheme
    ){ // inicio de Material


    //Scaffold va a generar una pantalla basica
    Scaffold(
        topBar={
            TopAppBar(title={Text("Mi primer App",
            color= MaterialTheme.colorScheme.onPrimary
            ) })
        }
//InnerPadding ses refiere al espacio que entrega Scaffold para el contenido

    ){innerPadding ->
        Column(
            modifier= Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .background(Color(0xFFF0F0F0)),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            //centrar
             horizontalAlignment = Alignment.CenterHorizontally

        ){
            Text("!!!!  Bienvenido !!!!",
              //  style=MaterialTheme.typography.headlineMedium,
                //Hasta aqui 21.09.2026
                style= MaterialTheme.typography.headlineMedium,
                color=MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(66.dp))

            Image(
            painter= painterResource(id= R.drawable.logoduoc),
            contentDescription = "Logo App",
                modifier= Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(66.dp))

            Row(
                modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
                ){// Aqui aplicamos las propiedades de la fila

                Text("texto uno",
                    style= MaterialTheme.typography.bodyLarge.copy(
color=MaterialTheme.colorScheme.onSurface.copy(alpha=0.8f),
                        fontWeight = FontWeight.Bold),
                    modifier= Modifier
                        .padding(end=8.dp)
                )// fin text 1



                Text("texto dos",
                    style= MaterialTheme.typography.bodyLarge.copy(
                        color=MaterialTheme.colorScheme.onSurface.copy(alpha=0.8f),
                        fontWeight = FontWeight.Bold),
                    modifier= Modifier
                        .padding(end=8.dp)
                )// fin text 2

            } // fin Aqui aplicamos las propiedades de la fila

            OutlinedTextField(
                value=state.username,
                onValueChange = vm::onUsernameChange,
                label={Text("Usuario")},
                singleLine = true,
                modifier= Modifier.fillMaxWidth(0.95f)
            )// fin usuario

            OutlinedTextField(
                value=state.password,
                onValueChange = vm::onPasswordChange,
                label={Text("Contraseña")},
                singleLine=true,


                visualTransformation= if(showPass)
                    VisualTransformation.None else PasswordVisualTransformation(),

                trailingIcon = {
                    TextButton(onClick = {showPass=!showPass}){
                        Text (if (showPass) "Ocultar" else "Ver")
                    }//Fin show

                }, //fin trail
                modifier=Modifier.fillMaxWidth(0.95f)
            )//fin password

            if(state.error !=null){
                Spacer(Modifier.height(8.dp))
                Text(
                    text=state.error ?:"",
                    color=MaterialTheme.colorScheme.primary,
                    fontWeight= FontWeight.Bold
                )
            }

Spacer(modifier=Modifier.height(66.dp))

            Button(onClick = {/* accion futura */
            vm.submit { user ->
                navController.navigate("muestraDatos/$user")
                {
                    popUpTo("login") { inclusive = true }  // no vuelva al login
                    launchSingleTop = true
                }//fin navegation
            } //fin submit
            }, //fin onclick

                enabled=!state.isLoading,
                modifier= Modifier.fillMaxWidth(0.6f)
            ){
              //  Text("Presioname")

                Text (if (state.isLoading) "Validando" else "Iniciar Session")
            }// fin button


        }//fin columna


    }//fin inner
    }// Fin Material
}// Fin Home


@Preview(showBackground = true)
@Composable

fun HomeScreenPreview(){
    //simulacion que permite activar Homescreen
    val navController= rememberNavController()
    val vm= LoginViewModel()
    HomeScreen(navController=navController,vm=vm )
}


