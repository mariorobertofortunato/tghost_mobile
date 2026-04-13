package com.mrf.tghost.app.ui.screens.mainscreen

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.mrf.tghost.R
import com.mrf.tghost.app.ui.theme.IconDimensions.iconNormal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenHeader(
    modifier: Modifier = Modifier,
    onNetworkIconClick: () -> Unit,
    onMenuIconClick: () -> Unit
) {

    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = TextUnit(0.1f, TextUnitType.Sp)
            )
        },
        actions = {
            IconButton(
                onClick = onNetworkIconClick,
                modifier = Modifier.size(iconNormal)
            ) {
                Icon(
                    painter = painterResource(R.drawable.network_node),
                    contentDescription = stringResource(id = R.string.network_screen_header_value),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(
                onClick = onMenuIconClick,
                modifier = Modifier.size(iconNormal)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_menu_asymm),
                    contentDescription = stringResource(id = R.string.menu_icon),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Unspecified,
            navigationIconContentColor = Color.Unspecified,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        )
    )

}
