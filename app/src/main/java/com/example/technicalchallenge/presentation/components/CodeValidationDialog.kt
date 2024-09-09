package com.example.technicalchallenge.presentation.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.technicalchallenge.util.boldTitleStyle
import com.example.technicalchallenge.util.colorMediumBlue

@Preview
@Composable
fun CodeValidationDialogPreview() {
    CodeValidationDialog()
}

@Composable
fun CodeValidationDialog(
    isShowingDialog: Boolean=true,
    codeInput: String="",
    onCodeChange: (String) -> Unit= {},
    onValidate: (String) -> Unit= {},
    onDismiss: () -> Unit= {}
) {
    val context = LocalContext.current
    val validationMessage by remember { mutableStateOf("") }
    if(isShowingDialog) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnClickOutside = false,
                dismissOnBackPress = false
            ),
        ) {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Text("Ingresa el código", style = boldTitleStyle.copy(color = colorMediumBlue))
                Spacer(modifier = Modifier.height(8.dp))

                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = codeInput,
                    onValueChange = {
                        if (it.length <= 6) {
                            onCodeChange(it)
                        }
                    },
                    label = "Código",
                    keyBoarType = KeyboardType.Number
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (codeInput.length == 6) {
                            onValidate(codeInput)
                        } else {
                            Toast.makeText(
                                context,
                                "Ingresa un codigo de 6 digitos.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1877F2)
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Validar")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(validationMessage)
            }
        }
    }
}