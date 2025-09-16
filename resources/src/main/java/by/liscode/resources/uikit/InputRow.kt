package by.liscode.resources.uikit

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import by.liscode.domain.utils.BASE_URL
import by.liscode.resources.R

@Composable
fun InputRow(
    modifier: Modifier = Modifier,
    title: String,
    content: String,
    trailingIcon: ImageVector? = null,
    onChangeContent: (String) -> Unit,
    onClickTrailingIcon: () -> Unit = {},
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = modifier,
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium,
        )
        UiTextField(
            content = content,
            trailingIcon = trailingIcon,
            onClickContent = onChangeContent,
            onClickTrailingIcon = onClickTrailingIcon,
            placeholder = ""
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun InputRowView() {
    InputRow(
        title = "API URL",
        content = BASE_URL,
        trailingIcon = ImageVector.vectorResource(R.drawable.outline_check_24),
        onChangeContent = {},
        onClickTrailingIcon = {}
    )
}