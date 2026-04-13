package com.mrf.tghost.app.ui.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.mrf.tghost.R
import com.mrf.tghost.app.ui.composables.text.TextComponent
import com.mrf.tghost.app.ui.theme.BorderDimensions.borderNormal
import com.mrf.tghost.app.ui.theme.BorderDimensions.borderSmall
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall
import com.mrf.tghost.app.ui.theme.secondaryGreen
import com.mrf.tghost.app.ui.theme.shapes
import com.mrf.tghost.app.utils.enums.LetterSpacing
import com.mrf.tghost.app.utils.enums.TextSize

@Composable
fun MultiSelectChipGroup(
    options: List<String>,
    selectedOptions: Set<String>,
    onSelectionChanged: (Set<String>) -> Unit,
) {
    var currentSelections by remember { mutableStateOf(selectedOptions) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        TextComponent(
            text = stringResource(R.string.filter_by_category).uppercase(),
            textSize = TextSize.CALLOUT,
            letterSpacing = LetterSpacing.FIFTEEN
        )

        Spacer(modifier = Modifier.height(paddingSmall))

        Row(
            horizontalArrangement = Arrangement.spacedBy(paddingSmall),
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                val isSelected = currentSelections.contains(option)

                Box(
                    modifier = Modifier
                        .clip(shapes.extraSmall)
                        .border(
                            width = if (isSelected) borderNormal else borderSmall,
                            color = if (isSelected) secondaryGreen else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.25f),
                            shape = shapes.extraSmall
                        )
                        .clickable(
                            indication = null,
                            interactionSource = null,
                            onClick = {
                                val newSelections = if (isSelected) {
                                    currentSelections - option
                                } else {
                                    currentSelections + option
                                }
                                currentSelections = newSelections
                                onSelectionChanged(newSelections)
                            })
                        .padding(paddingSmall)
                        .weight(1f)
                ) {
                    TextComponent(
                        text = option,
                        textSize = TextSize.FOOTNOTE,
                        color = if (isSelected) secondaryGreen else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.25f)
                    )
                }
            }
        }
    }
}