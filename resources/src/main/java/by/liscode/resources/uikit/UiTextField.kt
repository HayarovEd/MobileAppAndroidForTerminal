package by.liscode.resources.uikit

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import by.liscode.resources.theme.MobileAppAndroidForTerminalTheme

@Composable
fun UiTextField(
    modifier: Modifier = Modifier,
    content: String,
    label: String,
    trailingIcon: ImageVector? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isOnlyDigit: Boolean = false,
    isOnlyLetter: Boolean = false,
    disabledBorderColor: Color = Color.Unspecified,
    isError: Boolean = false,
    maxLines: Int = 1,
    onClickContent: (String) -> Unit,
    onClickTrailingIcon: () -> Unit = {},
) {
    TextField(
        modifier = modifier.fillMaxWidth(),
        value = content,
        onValueChange = { text ->
            if (isOnlyDigit) {
                if (text.all { it.isDigit() }) {
                    onClickContent(text)
                }
            } else if (isOnlyLetter) {
                if (text.all { it.isLetter() }) {
                    onClickContent(text)
                }
            } else {
                onClickContent(text)
            }
        },
        enabled = enabled,
        readOnly = readOnly,
        isError = isError,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onPrimaryContainer),
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = keyboardActions,
        maxLines = maxLines,
        placeholder = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall
            )
        },
        trailingIcon = {
            if (trailingIcon != null) {
                UiIconButton(
                    icon = trailingIcon,
                    onClick = onClickTrailingIcon
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.onPrimaryContainer,
            focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
            unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
            focusedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
            unfocusedLabelColor = MaterialTheme.colorScheme.outline,
            errorBorderColor = MaterialTheme.colorScheme.error,
            disabledBorderColor = disabledBorderColor
        )
    )
}

@Preview(
    showBackground = true
)
@Composable
private fun UiTextFieldView() {
    MobileAppAndroidForTerminalTheme {
        UiTextField(
            content = "",
            label = "hallo",
            onClickContent = {}
        )
    }
}

@Preview(
    showBackground = true
)
@Composable
private fun UiTextFieldView2() {
    MobileAppAndroidForTerminalTheme {
        UiTextField(
            content = "world!",
            label = "hallo",
            onClickContent = {}
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun UiTextFieldView3() {
    MobileAppAndroidForTerminalTheme {
        UiTextField(
            content = "",
            label = "hallo",
            onClickContent = {}
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun UiTextFieldView4() {
    MobileAppAndroidForTerminalTheme {
        UiTextField(
            content = "world!",
            label = "hallo",
            onClickContent = {}
        )
    }
}