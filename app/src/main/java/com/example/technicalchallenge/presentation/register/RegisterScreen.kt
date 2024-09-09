package com.example.technicalchallenge.presentation.register

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.technicalchallenge.R
import com.example.technicalchallenge.domain.model.Client
import com.example.technicalchallenge.presentation.components.CustomTextField
import com.example.technicalchallenge.util.DateTransformation
import com.example.technicalchallenge.util.boldTitleStyle
import com.example.technicalchallenge.util.colorBackground
import com.example.technicalchallenge.util.colorMediumBlue
import java.text.ParseException
import java.util.Calendar
import java.util.Date
@Preview
@Composable
fun RegisterPreview() {
    RegisterScreen()
}

@Composable
fun RegisterPage( viewModel: RegisterViewModel = hiltViewModel()){
    val registerState = viewModel.registerState.collectAsState()
    LaunchedEffect(registerState) {
        when (val state = registerState.value) {
            is RegisterState.SUCCESS -> {
                viewModel.registerResetStatus()
            }

            else -> {}
        }
    }
    RegisterScreen(onRegister = { viewModel.registerClient(it) } , registerState = registerState.value)
}

@SuppressLint("SimpleDateFormat")
@Composable
fun RegisterScreen(
    onRegister: (Client) -> Unit = {},
    registerState: RegisterState = RegisterState.IDLE,
    context: Context = LocalContext.current
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

    fun cleanData() {
        nombre = ""
        apellido = ""
        edad = ""
        fechaNacimiento = ""
    }

    fun setCalendar() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            context,
            androidx.appcompat.R.style.Base_ThemeOverlay_AppCompat_Dialog,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                fechaNacimiento = "${dayOfMonth.toString().padStart(2, '0')}/${(month + 1).toString().padStart(2, '0')}/${year}"

            }, year, month, day
        )
        datePickerDialog.show()
    }
    Column(
        modifier = Modifier
            .background(colorBackground)
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_android),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Creación de Cliente",
            style = boldTitleStyle.copy(color = colorMediumBlue, fontSize = 24.sp),
        )

        Spacer(
            modifier = Modifier
                .fillMaxHeight(0.08f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = painterResource(id = R.drawable.ic_user),
            value = nombre,
            onValueChange = {
                nombre = it
            },
            label = "Nombre "
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = painterResource(id = R.drawable.ic_user),
            value = apellido,
            onValueChange = {
                apellido = it
            },
            label = "Apellido "
        )
        Spacer(modifier = Modifier.height(8.dp))

        CustomTextField(
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = painterResource(id = R.drawable.ic_user),
            value = edad,
            onValueChange = {
                edad = it
            },
            keyBoarType = KeyboardType.Number,
            label = "Edad "
        )
        Spacer(modifier = Modifier.height(8.dp))

        CustomTextField(
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = painterResource(id = R.drawable.ic_bord),
            value = fechaNacimiento,
            keyBoarType = KeyboardType.Number,
            visualTransformation = DateTransformation(),
            onValueChange = {
                fechaNacimiento = it
                if (!it.contains(".") && !it.contains(",") && !it.contains(" ")) {
                    fechaNacimiento = it
                }
            },
            label = "Fecha de nacimiento (dd/MM/yyyy)",
            enabled = false,
            leadingIconOnClick = {
                setCalendar()
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        fun validarCampos(
            nombre: String,
            apellido: String,
            edad: String,
            fechaNacimiento: String,
            context: Context
        ): Boolean {
            return if (nombre.isBlank() || apellido.isBlank() || edad.isBlank() || fechaNacimiento.isBlank()) {
                Toast.makeText(context, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT)
                    .show()
                false
            } else {
                true
            }
        }

        // Extensión para parsear fechas de forma segura
        fun SimpleDateFormat.parseOrNull(dateString: String): Date? {
            return try {
                parse(dateString)
            } catch (e: ParseException) {
                null
            }
        }
        when (registerState) {
            is RegisterState.LOADING -> {
                CircularProgressIndicator()
            }

            is RegisterState.SUCCESS -> {
                Text(text = (registerState as RegisterState.SUCCESS).successMessage)
            }

            is RegisterState.ERROR -> {
                Text(text = (registerState as RegisterState.ERROR).errorMessage, color = Color.Red)
            }

            else -> {

                Button(
                    onClick = {
                        if (!validarCampos(
                                nombre,
                                apellido,
                                edad,
                                fechaNacimiento,
                                context
                            )
                        ) return@Button

                        // Convertir fecha de nacimiento a Date
                        Log.d("TAG", "RegisterScreen: $fechaNacimiento")
                        val dateOfBirth = simpleDateFormat.parseOrNull(fechaNacimiento) ?: run {
                            Toast.makeText(
                                context,
                                "Fecha de nacimiento inválida.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }
                        val client =                           Client(
                            nombre = nombre,
                            apellido = apellido,
                            edad = edad.toIntOrNull() ?: 0,
                            fechaNacimiento = dateOfBirth.toString()
                        )

                        // Crear el objeto Client y registrar
                        onRegister(client)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1877F2)
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Crear Usuario")
                }
            }
        }
    }
}



