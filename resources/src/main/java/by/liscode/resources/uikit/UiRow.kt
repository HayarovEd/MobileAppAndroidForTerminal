package by.liscode.resources.uikit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import by.liscode.domain.model.TypeLinen
import by.liscode.domain.utils.outFormat

@Composable
fun UiRowTitleThreeElements(
    modifier: Modifier = Modifier,
    title: String,
    firstNumber: Int,
    secondNumber: Int,
) {
    Row(
        modifier = modifier
            .clip(shape = RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .background(colorScheme.secondary)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = modifier.weight(6f),
            text = title,
            color = colorScheme.onSecondary,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = modifier.width(5.dp))
        Text(
            modifier = modifier.weight(1f),
            text = firstNumber.toString(),
            color = colorScheme.onSecondary,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = modifier.width(5.dp))
        Text(
            modifier = modifier.weight(1f),
            text = secondNumber.toString(),
            color = colorScheme.onSecondary,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}


@Composable
fun UiRowTitleTwoElements(
    modifier: Modifier = Modifier,
    title: String,
    firstNumber: Int,
) {
    Row(
        modifier = modifier
            .clip(shape = RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .background(colorScheme.secondary)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = modifier.weight(6f),
            text = title,
            color = colorScheme.onSecondary,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = modifier.width(5.dp))
        Text(
            modifier = modifier.weight(1f),
            text = firstNumber.toString(),
            color = colorScheme.onSecondary,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
fun UiRowThreeElements(
    modifier: Modifier = Modifier,
    title: TypeLinen,
    fontColor: Color,
    firstNumber: Int,
    secondNumber: Int,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = modifier.weight(5f),
            text = title.outFormat(),
            color = fontColor,
            style = MaterialTheme.typography.titleSmall,
        )
        Spacer(modifier = modifier.width(5.dp))
        Text(
            modifier = modifier.weight(1f),
            text = firstNumber.toString(),
            color = fontColor,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = modifier.width(5.dp))
        Text(
            modifier = modifier.weight(1f),
            text = secondNumber.toString(),
            color = fontColor,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun UiRowTwoElements(
    modifier: Modifier = Modifier,
    title: TypeLinen,
    fontColor: Color,
    firstNumber: Int,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = modifier.weight(6f),
            text = title.outFormat(),
            color = fontColor,
            style = MaterialTheme.typography.titleSmall,
        )
        Spacer(modifier = modifier.width(5.dp))
        Text(
            modifier = modifier.weight(1f),
            text = firstNumber.toString(),
            color = fontColor,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun UiColumnTwoElements(
    modifier: Modifier = Modifier,
    title: TypeLinen,
    fontColor: Color,
    value: String,
) {
    Column (
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            modifier = modifier.fillMaxWidth(),
            text = title.outFormat(),
            color = fontColor,
            style = MaterialTheme.typography.titleSmall,
        )
        Spacer(modifier = modifier.width(5.dp))
        Text(
            modifier = modifier.fillMaxWidth(),
            text = value,
            color = fontColor,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}


@Composable
fun UiRowTwoText(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = modifier,
            text = title,
            color = colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            modifier = modifier,
            text = value,
            color = colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun UiRowThreeText(
    modifier: Modifier = Modifier,
    title1: String,
    title2: String,
    title3: String,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = modifier,
            text = title1,
            color = colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = modifier.weight(1f))
        Text(
            modifier = modifier,
            text = title2,
            color = colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = modifier.width(10.dp))
        Text(
            modifier = modifier,
            text = title3,
            color = colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}



@Composable
fun UiClickableRow(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = modifier,
            text = title,
            color = colorScheme.error,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            modifier = modifier,
            text = value,
            color = colorScheme.error,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

