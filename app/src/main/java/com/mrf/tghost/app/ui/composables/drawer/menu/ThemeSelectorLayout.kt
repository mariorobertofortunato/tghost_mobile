package com.mrf.tghost.app.ui.composables.drawer.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.mrf.tghost.R
import com.mrf.tghost.app.ui.composables.cta.BaseCtaFullWidth
import com.mrf.tghost.app.ui.composables.decoration.HorizontalDivider
import com.mrf.tghost.app.ui.composables.text.TextComponent
import com.mrf.tghost.domain.model.AppTheme
import com.mrf.tghost.app.ui.theme.BorderDimensions.borderNormal
import com.mrf.tghost.app.ui.theme.BorderDimensions.borderSmall
import com.mrf.tghost.app.ui.theme.DividerDimensions.dividerExtraSmall
import com.mrf.tghost.app.ui.theme.IconDimensions.iconSmall
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingExtraSmall
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingMedium
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingNormal
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingSmall
import com.mrf.tghost.app.ui.theme.blueThemeOnPrimary
import com.mrf.tghost.app.ui.theme.blueThemePrimary
import com.mrf.tghost.app.ui.theme.orangeThemeOnPrimary
import com.mrf.tghost.app.ui.theme.orangeThemePrimary
import com.mrf.tghost.app.ui.theme.primaryBlack
import com.mrf.tghost.app.ui.theme.primaryWhite
import com.mrf.tghost.app.ui.theme.purpleThemeOnPrimary
import com.mrf.tghost.app.ui.theme.purpleThemePrimary
import com.mrf.tghost.app.ui.theme.redThemeOnPrimary
import com.mrf.tghost.app.ui.theme.redThemePrimary
import com.mrf.tghost.app.ui.theme.shapes
import com.mrf.tghost.domain.model.ThemeCategory
import com.mrf.tghost.domain.model.ThemeItem
import com.mrf.tghost.app.utils.enums.LetterSpacing
import com.mrf.tghost.app.utils.enums.TextSize
import com.mrf.tghost.app.utils.extensions.showIf

@Composable
fun ThemeSelectorLayout(
    currentTheme: String,
    isProUser: Boolean,
    onApplyCtaClick: (ThemeItem) -> Unit,
) {
    val scrollState = rememberScrollState()
    val categories = ThemeCategory.entries
    val items = listOf(
        ThemeItem(
            theme = AppTheme.LIGHT,
            backGroundColor = primaryWhite.value.toLong(),
            contentColor = primaryBlack.value.toLong(),
            category = ThemeCategory.CLASSIC,
            isProTheme = false
        ),
        ThemeItem(
            theme = AppTheme.DARK,
            backGroundColor = primaryBlack.value.toLong(),
            contentColor = primaryWhite.value.toLong(),
            category = ThemeCategory.CLASSIC,
            isProTheme = false
        ),
        ThemeItem(
            theme = AppTheme.RED,
            backGroundColor = redThemePrimary.value.toLong(),
            contentColor = redThemeOnPrimary.value.toLong(),
            category = ThemeCategory.MONO,
            isProTheme = true
        ),
        ThemeItem(
            theme = AppTheme.BLUE,
            backGroundColor = blueThemePrimary.value.toLong(),
            contentColor = blueThemeOnPrimary.value.toLong(),
            category = ThemeCategory.MONO,
            isProTheme = true
        ),
        ThemeItem(
            theme = AppTheme.PURPLE,
            backGroundColor = purpleThemePrimary.value.toLong(),
            contentColor = purpleThemeOnPrimary.value.toLong(),
            category = ThemeCategory.MONO,
            isProTheme = true
        ),
        ThemeItem(
            theme = AppTheme.ORANGE,
            backGroundColor = orangeThemePrimary.value.toLong(),
            contentColor = orangeThemeOnPrimary.value.toLong(),
            category = ThemeCategory.MONO,
            isProTheme = true
        ),
        ThemeItem(
            theme = AppTheme.JUNGLE_DARK,
            backGroundColor = primaryBlack.value.toLong(),
            contentColor = primaryWhite.value.toLong(),
            category = ThemeCategory.JUNGLE,
            isProTheme = false,
            bgRes = R.drawable.jungle_theme_dark_bg
        ),
        ThemeItem(
            theme = AppTheme.JUNGLE_LIGHT,
            backGroundColor = primaryWhite.value.toLong(),
            contentColor = primaryBlack.value.toLong(),
            category = ThemeCategory.JUNGLE,
            isProTheme = true,
            bgRes = R.drawable.jungle_theme_light_bg
        ),
        ThemeItem(
            theme = AppTheme.JUNGLE_OCEAN,
            backGroundColor = primaryBlack.value.toLong(),
            contentColor = primaryWhite.value.toLong(),
            category = ThemeCategory.JUNGLE,
            isProTheme = true,
            bgRes = R.drawable.jungle_theme_ocean_bg
        ),
        ThemeItem(
            theme = AppTheme.JUNGLE_GOLDEN,
            backGroundColor = primaryWhite.value.toLong(),
            contentColor = primaryBlack.value.toLong(),
            category = ThemeCategory.JUNGLE,
            isProTheme = true,
            bgRes = R.drawable.jungle_theme_golden_bg
        ),
        ThemeItem(
            theme = AppTheme.JUNGLE_MONOTINT,
            backGroundColor = primaryWhite.value.toLong(),
            contentColor = primaryBlack.value.toLong(),
            category = ThemeCategory.JUNGLE,
            isProTheme = true,
            bgRes = R.drawable.jungle_theme_monotint_bg
        )
    )

    var selectedTheme by remember {
        mutableStateOf(items.find { it.theme.name == currentTheme } ?: items.first())
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(paddingNormal),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(paddingNormal),
            modifier = Modifier

                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(bottom = paddingNormal)
        ) {
            categories.forEach { category ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(paddingNormal),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    TextComponent(
                        text = category.name,
                        textSize = TextSize.BODY,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = LetterSpacing.FIFTEEN,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = paddingMedium)
                            .padding(top = paddingSmall)
                    )

                    val categoryItems = items.filter { it.category == category }
                    categoryItems.chunked(2).forEach { rowItems ->
                        Row(
                            horizontalArrangement = Arrangement.Start
                        ) {
                            rowItems.forEach { item ->
                                GridItem(
                                    value = item.theme.name,
                                    isSelected = selectedTheme == item,
                                    onClick = {
                                        selectedTheme = item
                                    },
                                    backgroundColor = item.backGroundColor,
                                    bgRes = item.bgRes,
                                    contentColor = item.contentColor,
                                    isSelectable = !item.isProTheme || isProUser,
                                    modifier = Modifier
                                        .weight(1f)
                                )
                            }
                            // Se c'è solo 1 elemento in questa riga, aggiungi uno Spacer per bilanciare la griglia
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                    if (category.ordinal < categories.size - 1) {
                        HorizontalDivider(
                            thickness = dividerExtraSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                        )
                    }
                }
            }

        }
        BaseCtaFullWidth(
            text = stringResource(R.string.apply),
            textSize = TextSize.BODY,
            icon = painterResource(R.drawable.ic_confirm),
            iconSize = iconSmall,
            onClick = { onApplyCtaClick(selectedTheme) }
        )
    }

}


@Composable
fun GridItem(
    value: String? = null,
    isSelected: Boolean,
    backgroundColor: Long,
    bgRes: Int? = null,
    contentColor: Long,
    onClick: () -> Unit,
    isSelectable: Boolean = true,
    modifier: Modifier = Modifier
) {
    val bgColor = Color(backgroundColor)
    val fgColor = Color(contentColor)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .border(
                width = borderNormal,
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                shape = shapes.medium
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(paddingNormal),
        verticalArrangement = Arrangement.spacedBy(paddingNormal)
    )
    {

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(
                    color = bgColor,
                    shape = shapes.medium
                )
                .border(
                    width = borderSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    shape = shapes.medium
                )
        ) {

            Image(
                painter = painterResource(id = bgRes ?: R.drawable.jungle_theme_dark_bg),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(shapes.medium)
                    .matchParentSize()
                    .showIf(bgRes != null)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(paddingExtraSmall),
                modifier = Modifier
                    .padding(paddingMedium)
            ) {
                Row(
                    modifier = Modifier
                        .background(if (!isSelectable) fgColor.copy(alpha = 0.25f) else fgColor)
                        .fillMaxWidth(0.6f)
                        .padding(paddingExtraSmall)
                ) {}
                Row(
                    modifier = Modifier
                        .background(if (!isSelectable) fgColor.copy(alpha = 0.25f) else fgColor)
                        .fillMaxWidth(0.9f)
                        .padding(paddingExtraSmall)
                ) {}
            }

            Icon(
                painter = painterResource(R.drawable.ic_premium),
                contentDescription = "Pro item",
                tint = if (!isSelectable) MaterialTheme.colorScheme.primary else Color.Transparent,
                modifier = Modifier
                    .background(
                        color = if (!isSelectable) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                        shape = shapes.medium
                    )
                    .border(
                        borderNormal,
                        if (!isSelectable) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shapes.medium
                    )
                    .padding(paddingSmall)
                    .size(iconSmall)
            )


        }

        if (value != null) {
            TextComponent(
                text = value,
                textSize = TextSize.CAPTION,
                textAlign = TextAlign.Center,
                modifier = Modifier
            )
        }

    }
}


@Composable
@Preview
private fun GridPreview() {
    val items = listOf(
        ThemeItem(
            theme = AppTheme.LIGHT,
            backGroundColor = primaryWhite.value.toLong(),
            contentColor = primaryBlack.value.toLong(),
            category = ThemeCategory.CLASSIC,
            isProTheme = false
        ),
        ThemeItem(
            theme = AppTheme.DARK,
            backGroundColor = primaryBlack.value.toLong(),
            contentColor = primaryWhite.value.toLong(),
            category = ThemeCategory.CLASSIC,
            isProTheme = false
        ),
        ThemeItem(
            theme = AppTheme.RED,
            backGroundColor = redThemePrimary.value.toLong(),
            contentColor = redThemeOnPrimary.value.toLong(),
            category = ThemeCategory.MONO,
            isProTheme = true
        ),
        ThemeItem(
            theme = AppTheme.BLUE,
            backGroundColor = blueThemePrimary.value.toLong(),
            contentColor = blueThemeOnPrimary.value.toLong(),
            category = ThemeCategory.MONO,
            isProTheme = true
        ),
        ThemeItem(
            theme = AppTheme.PURPLE,
            backGroundColor = purpleThemePrimary.value.toLong(),
            contentColor = purpleThemeOnPrimary.value.toLong(),
            category = ThemeCategory.MONO,
            isProTheme = true
        ),
        ThemeItem(
            theme = AppTheme.ORANGE,
            backGroundColor = orangeThemePrimary.value.toLong(),
            contentColor = orangeThemeOnPrimary.value.toLong(),
            category = ThemeCategory.MONO,
            isProTheme = true
        ),
        ThemeItem(
            theme = AppTheme.JUNGLE_DARK,
            backGroundColor = primaryBlack.value.toLong(),
            contentColor = primaryWhite.value.toLong(),
            category = ThemeCategory.JUNGLE,
            isProTheme = false
        ),
        ThemeItem(
            theme = AppTheme.JUNGLE_LIGHT,
            backGroundColor = primaryWhite.value.toLong(), 
            contentColor = primaryBlack.value.toLong(),
            category = ThemeCategory.JUNGLE,
            isProTheme = true
        )
    )

    ThemeSelectorLayout(
        isProUser = false,
        currentTheme = items[0].theme.name,
        onApplyCtaClick = { }
    )
}

@Composable
@Preview
private fun GridItemPreview() {
    GridItem(
        value = "ghhjg", isSelected = true, onClick = { Unit },
        backgroundColor = primaryWhite.value.toLong(),
        contentColor = primaryBlack.value.toLong()
    )
}
