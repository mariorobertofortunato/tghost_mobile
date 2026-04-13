package com.mrf.tghost.app.ui.composables.text

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.mrf.tghost.app.utils.enums.LetterSpacing
import com.mrf.tghost.app.utils.enums.TextSize

@Deprecated("Use vanilla Text")
@Composable
fun TextComponent (
    modifier: Modifier = Modifier,
    text: String,
    textSize: TextSize = TextSize.BODY,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    fontWeight: FontWeight = FontWeight.Normal,
    letterSpacing: LetterSpacing? = LetterSpacing.ZERO,
    textAlign: TextAlign? = TextAlign.Start,
    textDecoration: TextDecoration? = null
    ) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        letterSpacing = if (letterSpacing != null) TextUnit((letterSpacing.value * textStyle.fontSize.value), TextUnitType.Sp) else TextUnit.Unspecified,
        textAlign = textAlign,
        style = textStyle,
        textDecoration = textDecoration
    )
}


@Deprecated("Use vanilla Text")
@Composable
fun TextComponent (
    modifier: Modifier = Modifier,
    text: AnnotatedString,
    textSize: TextSize = TextSize.BODY,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    fontWeight: FontWeight = FontWeight.Normal,
    letterSpacing: LetterSpacing? = LetterSpacing.ZERO,
    textAlign: TextAlign? = TextAlign.Start,
    textDecoration: TextDecoration? = null
    ) {
    Text(
        modifier = modifier,
        text = text,
        color = color,
        letterSpacing = if (letterSpacing != null) TextUnit((letterSpacing.value * textStyle.fontSize.value), TextUnitType.Sp) else TextUnit.Unspecified,
        textAlign = textAlign,
        style = textStyle,
        textDecoration = textDecoration
    )
}