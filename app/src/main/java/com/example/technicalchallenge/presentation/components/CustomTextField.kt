package com.example.technicalchallenge.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.technicalchallenge.util.colorDisabled
import com.example.technicalchallenge.util.colorPrimary
import com.example.technicalchallenge.util.textFieldTextStyle

@Preview
@Composable
fun CustomTextFieldPreview() {
    CustomTextField(value = "holaa", onValueChange = {})
}

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    value: String =  "",
    onValueChange: (String) -> Unit,
    leadingIcon: Painter? = null,
    suffix: String? = null,
    label: String = "",
    textAlign: TextAlign = TextAlign.Start,
    keyBoarType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIconOnClick: () -> Unit = {},
    enabled: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        enabled = enabled,
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() }
        ),
        leadingIcon = {
            if (leadingIcon != null)
                Icon(
                    painter = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        leadingIconOnClick()
                    }
                )
        },

        trailingIcon = trailingIcon,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            disabledTextColor = Color.Black,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            errorContainerColor = Color.White,
            cursorColor = colorPrimary,
            focusedBorderColor = colorPrimary,
            unfocusedBorderColor = colorDisabled,
            focusedLabelColor = colorPrimary,
        ),
        label = { Text(text = label, style = textFieldTextStyle) },
        suffix = { Text(text = suffix ?: "") },
        textStyle = LocalTextStyle.current.copy(textAlign = textAlign),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyBoarType
        ),
        visualTransformation = visualTransformation,
    )
}