package by.liscode.resources.uikit

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.otpfield.bottomStroke
import kotlinx.coroutines.launch

private data class OtpField(
    val text: String,
    val index: Int,
    val focusRequester: FocusRequester? = null
)


@Composable
fun OtpInputField(
    otp: String,
    count: Int = 5,
    modifier: Modifier = Modifier
        .clip(shape = RoundedCornerShape(5.dp))
        .border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
            shape = RoundedCornerShape(5.dp)),
    otpTextType: KeyboardType = KeyboardType.Number,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    onUpdateOtp: (String) -> Unit,
) {

    val scope = rememberCoroutineScope()

    // Initialize state for each OTP box with its character and optional focus requester.
    val otpFieldsValues = remember {
        (0 until count).mapIndexed { index, i ->
            mutableStateOf(
                OtpField(
                    text = otp.getOrNull(i)?.toString() ?: "",
                    index = index,
                    focusRequester = FocusRequester()
                )
            )
        }
    }

    // Update each OTP box's value when the overall OTP value changes, and manage focus.
    LaunchedEffect(key1 = otp) {
        for (i in otpFieldsValues.indices) {
            otpFieldsValues[i].value =
                otpFieldsValues[i].value.copy(text = otp.getOrNull(i)?.toString() ?: "")
        }
        // Request focus on the first box if the OTP is blank (e.g., reset).
        if (otp.isBlank()) {
            try {
                otpFieldsValues[0].value.focusRequester?.requestFocus()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Create a row of OTP boxes.
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        repeat(count) { index ->
            // For each OTP box, manage its value, focus, and what happens on value change.
            OtpBox(
                modifier = modifier,
                otpValue = otpFieldsValues[index].value,
                textType = otpTextType,
                textColor = textColor,
                isLastItem = index == count - 1, // Check if this box is the last in the sequence.
                totalBoxCount = count,
                onValueChange = { newValue ->
                    // Handling logic for input changes, including moving focus and updating OTP state.
                    scope.launch {
                        handleOtpInputChange(index, count, newValue, otpFieldsValues, otp, onUpdateOtp)
                    }
                },
                onFocusSet = { focusRequester ->
                    // Save the focus requester for each box to manage focus programmatically.
                    otpFieldsValues[index].value =
                        otpFieldsValues[index].value.copy(focusRequester = focusRequester)
                },
                onNext = {
                    // Attempt to move focus to the next box when the "next" action is triggered.
                    focusNextBox(index, count, otpFieldsValues)
                },
            )
        }
    }
}

/**
 * Handles input changes for each OTP box and manages the logic for updating the OTP state
 * and managing focus transitions between OTP boxes.
 *
 * @param index The index of the OTP box where the input change occurred.
 * @param count The total number of OTP boxes.
 * @param newValue The new value inputted into the OTP box at the specified index.
 * @param otpFieldsValues A list of mutable states, each representing an individual OTP box's state.
 * @param otp A mutable state holding the current concatenated value of all OTP boxes.
 *
 * The function updates the text of the targeted OTP box based on the length and content of `newValue`.
 * If `newValue` contains only one character, it replaces the existing text in the current box.
 * If two characters are present, likely from rapid user input, it sets the box's text to the second character,
 * assuming the first character was already accepted. If multiple characters are pasted,
 * they are distributed across the subsequent boxes starting from the current index.
 *
 * Focus management is also handled, where focus is moved to the next box if a single character is inputted,
 * and moved back to the previous box if the current box is cleared. This is especially useful for
 * scenarios where users might quickly navigate between OTP fields either by typing or deleting characters.
 *
 * Exception handling is used to catch and log any errors that occur during focus management to avoid
 * crashing the application and to provide debug information.
 */
private fun handleOtpInputChange(
    index: Int,
    count: Int,
    newValue: String,
    otpFieldsValues: List<MutableState<OtpField>>,
    otp: String,
    onUpdateOtp: (String) -> Unit,
) {
    // Handle input for the current box.
    if (newValue.length <= 1) {
        // Directly set the new value if it's a single character.
        otpFieldsValues[index].value = otpFieldsValues[index].value.copy(text = newValue)
    } else if (newValue.length == 2) {
        // If length of new value is 2, we can guess the user is typing focusing on current box
        // In this case set the unmatched character only
        otpFieldsValues[index].value =
            otpFieldsValues[index].value.copy(text = newValue.lastOrNull()?.toString() ?: "")
    } else if (newValue.isNotEmpty()) {
        // If pasting multiple characters, distribute them across the boxes starting from the current index.
        newValue.forEachIndexed { i, char ->
            if (index + i < count) {
                otpFieldsValues[index + i].value =
                    otpFieldsValues[index + i].value.copy(text = char.toString())
            }
        }
    }

    // Update the overall OTP state.
    var currentOtp = ""
    otpFieldsValues.forEach {
        currentOtp += it.value.text
    }

    try {
        // Logic to manage focus.
        if (newValue.isEmpty() && index > 0) {
            // If clearing a box and it's not the first box, move focus to the previous box.
            otpFieldsValues.getOrNull(index - 1)?.value?.focusRequester?.requestFocus()
        } else if (index < count - 1 && newValue.isNotEmpty()) {
            // If adding a character and not on the last box, move focus to the next box.
            otpFieldsValues.getOrNull(index + 1)?.value?.focusRequester?.requestFocus()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    onUpdateOtp(currentOtp)
}

private fun focusNextBox(
    index: Int,
    count: Int,
    otpFieldsValues: List<MutableState<OtpField>>
) {
    if (index + 1 < count) {
        // Move focus to the next box if the current one is filled and it's not the last box.
        try {
            otpFieldsValues[index + 1].value.focusRequester?.requestFocus()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


@SuppressLint("RememberInComposition")
@Composable
private fun OtpBox(
    modifier: Modifier,
    otpValue: OtpField, // Current value of this OTP box.
    textType: KeyboardType = KeyboardType.Number,
    textColor: Color = Color.Black,
    isLastItem: Boolean, // Whether this box is the last in the sequence.
    totalBoxCount: Int, // Total number of OTP boxes for layout calculations.
    onValueChange: (String) -> Unit, // Callback for when the value changes.
    onFocusSet: (FocusRequester) -> Unit, // Callback to set focus requester.
    onNext: () -> Unit, // Callback for handling "next" action, typically moving focus forward.
) {
    val focusManager = LocalFocusManager.current
    val focusRequest = otpValue.focusRequester ?: FocusRequester()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Calculate the size of the box based on screen width and total count.
    // If you're using this in Kotlin multiplatform mobile
    // val screenWidth = LocalWindowInfo.current.containerSize.width
    // If you're using this in Android
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp.dpToPx().toInt()
    val paddingValue = 5
    val totalBoxSize = (screenWidth / totalBoxCount) - paddingValue * totalBoxCount

    Box(
        modifier = modifier
            .size(totalBoxSize.pxToDp()),
        contentAlignment = Alignment.Center,
    ) {
        BasicTextField(
            value = TextFieldValue(otpValue.text, TextRange(maxOf(0, otpValue.text.length))),
            onValueChange = {
                // Logic to prevent re-triggering onValueChange when focusing.
                if (!it.text.equals(otpValue)) {
                    onValueChange(it.text)
                }
            },
            modifier = Modifier
                .testTag("otpBox${otpValue.index}")
                .focusRequester(focusRequest)
                .onGloballyPositioned {
                    onFocusSet(focusRequest)
                },
            textStyle = MaterialTheme.typography.titleLarge.copy(
                textAlign = TextAlign.Center,
                color = textColor
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = textType,
                imeAction = if (isLastItem) ImeAction.Done else ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    onNext()
                },
                onDone = {
                    // Hide keyboard and clear focus when done.
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            ),
            singleLine = true,
            visualTransformation = getVisualTransformation(textType),
        )
    }
}

/**
 * Provides an appropriate VisualTransformation based on the specified keyboard type.
 * This method is used to determine how text should be displayed in the UI.
 *
 * @param textType The type of keyboard input expected, which determines if the text should be obscured.
 * @return A VisualTransformation that either obscures text for password fields or displays text normally.
 *         Password and NumberPassword fields will have their input obscured with bullet characters.
 *         All other fields will display text as entered.
 */
@Composable
private fun getVisualTransformation(textType: KeyboardType) =
    if (textType == KeyboardType.NumberPassword || textType == KeyboardType.Password) PasswordVisualTransformation() else VisualTransformation.None

@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }


@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

@Preview
@Composable
fun OtpView_Preivew() {
    MaterialTheme {
        val otpValue = remember {
            mutableStateOf("124")
        }
        Column(
            modifier = Modifier.padding(40.pxToDp()),
            verticalArrangement = Arrangement.spacedBy(20.pxToDp())
        ) {
            OtpInputField(
                otp = "124",
                count = 4,
                modifier = Modifier
                    .border(1.pxToDp(), Color.Black)
                    .background(Color.White),
                otpTextType = KeyboardType.Number,
                onUpdateOtp = {}
            )

            OtpInputField(
                otp = "124",
                count = 4,
                otpTextType = KeyboardType.NumberPassword,
                modifier = Modifier
                    .border(3.pxToDp(), Color.Gray)
                    .background(Color.White),
                onUpdateOtp = {}
            )

            OtpInputField(
                otp = "124",
                count = 5,
                textColor = MaterialTheme.colorScheme.onBackground,
                onUpdateOtp = {},
                modifier = Modifier
                    .border(7.pxToDp(), Color(0xFF277F51), shape = RoundedCornerShape(12.pxToDp()))
            )

            OtpInputField(
                otp = "789",
                count = 5,
                onUpdateOtp = {},
                modifier = Modifier
                    .bottomStroke(color = Color.DarkGray, strokeWidth = 6.pxToDp())
            )
        }
    }
}
