package com.mrf.tghost.app.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mrf.tghost.R
import com.mrf.tghost.app.ui.composables.text.BaseTextField
import com.mrf.tghost.app.ui.composables.text.TextComponent
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingNormal
import com.mrf.tghost.app.ui.theme.shapes
import com.mrf.tghost.app.utils.enums.TextSize

@Composable
fun <T> LargeDropdownMenu(
    modifier: Modifier = Modifier,
    label: String,
    notSetLabel: String? = null,
    items: List<T>,
    selectedIndex: Int = -1,
    onItemSelected: (index: Int, item: T) -> Unit,
    selectedItemToString: (T) -> String = { it.toString() },
    drawItem: @Composable (T, () -> Unit) -> Unit = { item, onClick ->
        LargeDropdownMenuItem(
            text = item.toString(),
            onClick = onClick,
        )
    }
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.height(IntrinsicSize.Min)) {
        BaseTextField(
            label = label,
            value = items.getOrNull(selectedIndex)?.let { selectedItemToString(it) } ?: "",
            placeholder = stringResource(R.string.select_account),
            errorLabelVisibility = false,
            errorLabelValue = "",
            trailingIcon = R.drawable.ic_dropdown,
            onValueChange = { }
        )

        // Transparent clickable surface on top of BaseTextField
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clip(shapes.extraSmall)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {
                        expanded = true
                    }
                ),
            color = Color.Transparent,
        ) {}
    }

    if (expanded) {
        Dialog(
            onDismissRequest = { expanded = false },
        ) {

            Surface(
                shape = shapes.medium,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.onSurface,
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                val listState = rememberLazyListState()
                if (selectedIndex > -1) {
                    LaunchedEffect("ScrollToSelected") {
                        listState.scrollToItem(index = selectedIndex)
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    state = listState
                ) {
                    if (notSetLabel != null) {
                        item {
                            LargeDropdownMenuItem(
                                text = notSetLabel,
                                onClick = { },
                            )
                        }
                    }
                    itemsIndexed(items) { index, item ->
                        drawItem(
                            item
                        ) {
                            onItemSelected(index, item)
                            expanded = false
                        }

                        if (index < items.lastIndex) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .padding(horizontal = paddingNormal)
                            )
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun LargeDropdownMenuItem(
    text: String,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingNormal)
            .clickable(
                onClick = {
                    onClick()
                }
            )
    ) {
        TextComponent(
            text = text,
            textSize = TextSize.BODY,
            color = MaterialTheme.colorScheme.onSurface
        )
    }

}