package com.mrf.tghost.app.ui.screens.mainscreen.sections.tokenaccount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.fallback
import com.mrf.tghost.R
import com.mrf.tghost.app.ui.mapper.getChainIcon
import com.mrf.tghost.app.ui.mapper.getChainIconBadge
import com.mrf.tghost.domain.model.TokenAccount
import com.mrf.tghost.domain.model.ipfsGateways
import com.mrf.tghost.domain.model.ipfsToHttp

@Composable
fun TokenAccountItem(
    modifier: Modifier = Modifier,
    tokenAccount: TokenAccount
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Token Image / Icon
            if (!tokenAccount.image.isNullOrEmpty()) {
                var currentGatewayIndex by remember(tokenAccount.image) { mutableIntStateOf(0) }

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(ipfsToHttp(tokenAccount.image ?: "", ipfsGateways.getOrElse(currentGatewayIndex) { ipfsGateways.first() }))
                        .httpHeaders(
                            NetworkHeaders.Builder()
                                .set("User-Agent", "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36")
                                .build()
                        )
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .crossfade(true)
                        .fallback(R.drawable.logo_transparent)
                        .listener(
                            onError = { _, _ ->
                                if (currentGatewayIndex < ipfsGateways.size - 1) {
                                    currentGatewayIndex++
                                }
                            }
                        )
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.ic_help),
                    error = painterResource(R.drawable.ic_warning)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(getChainIcon(tokenAccount.symbol) ?: R.drawable.ic_help),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Chain Badge
            getChainIconBadge(tokenAccount.chainId)?.let { badgeRes ->
                Icon(
                    painter = painterResource(badgeRes),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 4.dp, y = 4.dp)
                        .size(16.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                        .padding(1.dp)
                        .clip(CircleShape)
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.weight(1f)
        ) {
            NameAndPricesRow(tokenAccount)
            TokenAmountAndValuesRow(tokenAccount)
            PriceChangeRow(tokenAccount)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun TokenAccountItemPreview(){
    TokenAccountItem(
        tokenAccount = TokenAccount(
            uiAmountString = "999"
        )
    )
}
