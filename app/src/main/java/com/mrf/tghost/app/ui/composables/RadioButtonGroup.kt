package com.mrf.tghost.app.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.mrf.tghost.app.ui.theme.BorderDimensions.borderSmall
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall
import com.mrf.tghost.app.ui.theme.primaryBlack

@Composable
fun RadioButtonGroup(
    options: List<String>,
    selectedOption: String,
    optionTextStyle: TextStyle = MaterialTheme.typography.labelLarge,
    onOptionSelected: (String) -> Unit,
    optionEnabled: (String) -> Boolean = { true },
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(paddingSmall),
        modifier = Modifier
            .fillMaxWidth()
            //.padding(paddingSmall)
    ) {
        options.forEach { option ->
            val isSelected = selectedOption == option
            val isEnabled = optionEnabled(option)

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 44.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(
                        color = when {
                            !isEnabled -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            isSelected -> MaterialTheme.colorScheme.primary
                            else -> primaryBlack.copy(alpha = 0.5f)
                        }
                    )
                    .then(
                        if (isSelected && isEnabled) {
                            Modifier.innerShadow(
                                shape = MaterialTheme.shapes.small,
                                shadow = Shadow(
                                    radius = 4.dp,
                                    spread = 1.dp,
                                    color = Color.Black.copy(alpha = 0.6f),
                                    offset = DpOffset(2.dp, 3.dp)
                                )
                            )
                        } else {
                            Modifier.border(
                                width = borderSmall,
                                color = if (isEnabled) MaterialTheme.colorScheme.outlineVariant else Color.Transparent,
                                shape = MaterialTheme.shapes.small
                            )
                        }
                    )
                    .clickable(
                        enabled = isEnabled,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(),
                        onClick = { onOptionSelected(option) }
                    )
            ) {
                Box {
                    // Testo di sfondo (Inattivo)
                    Text(
                        text = option.uppercase(),
                        style = optionTextStyle,
                        color = when {
                            !isEnabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        fontWeight = FontWeight.Bold,
                        letterSpacing = TextUnit(0.15f, TextUnitType.Sp),
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                    )

                    // Testo animato (Attivo)
                    androidx.compose.animation.AnimatedVisibility(
                        visible = isSelected && isEnabled,
                        enter = slideInVertically(animationSpec = tween(300)),
                        exit = fadeOut(animationSpec = tween(200))
                    ) {
                        Text(
                            text = option.uppercase(),
                            style = optionTextStyle,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = TextUnit(0.15f, TextUnitType.Sp),
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
