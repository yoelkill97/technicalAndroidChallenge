package com.example.technicalchallenge.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.technicalchallenge.R

@Preview
@Composable
fun PhoneInputCodePreview() {
    PhoneInputCode()
}

@Composable
fun PhoneInputCode(onVerificationCode: (String) -> Unit = {}) {
    var showError by remember { mutableStateOf(false) } //
    var phoneNumber by remember { mutableStateOf("+51") }
    var showPhoneInput by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }


    Column {
        Button(
            onClick = { showPhoneInput = !showPhoneInput },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2A6BAF) // Color típico de apps de teléfono (opcional)
            ),
            contentPadding = PaddingValues(0.dp) // Eliminar padding predeterminado
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_phone), // Reemplazar con el drawable del ícono de teléfono
                    contentDescription = "Phone Sign-In",
                    tint = Color.Unspecified, // Sin tint
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Sign in with Phone",
                    color = Color.White // Texto en blanco
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (showPhoneInput) {
            CustomTextField(
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = painterResource(id = R.drawable.ic_phone_low),
                value = phoneNumber,
                onValueChange = {

                    if (it.length > 2 || it.startsWith("51")) {
                        phoneNumber = it
                        showError = it.length < 12 || it.length > 13
                    }
                },
                label = "Número de teléfono"
            )

            if (showError) {
                Text("El número debe tener 9 dígitos", color = Color.Red)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    onVerificationCode("+1 650-555-3434")
                    showDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1877F2)
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Enviar código")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }


}