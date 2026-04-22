package com.mrf.tghost.app.ui.screens.mainscreen.sections.transactions

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mrf.tghost.R
import com.mrf.tghost.app.ui.mapper.getChainIconBadge
import com.mrf.tghost.app.ui.mapper.getTransactionTypeIcon
import com.mrf.tghost.app.utils.extensions.smartFormatAmount
import com.mrf.tghost.domain.model.BalanceChange
import com.mrf.tghost.domain.model.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.pow

@Composable
fun TransactionItem(
    modifier: Modifier = Modifier,
    transaction: Transaction
) {
    val primaryChange = transaction.balanceChanges.firstOrNull { !it.isNative }
        ?: transaction.balanceChanges.firstOrNull()
    val primaryChangeIndex = primaryChange?.let { transaction.balanceChanges.indexOf(it) } ?: -1
    val secondaryChange = transaction.balanceChanges
        .filterIndexed { index, _ -> index != primaryChangeIndex }
        .firstOrNull()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    if (transaction.isSuccess) MaterialTheme.colorScheme.surfaceVariant
                    else MaterialTheme.colorScheme.errorContainer,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(getTransactionTypeIcon(transaction.type)),
                contentDescription = null,
                tint = if (transaction.isSuccess) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(24.dp)
            )
        }

        // Details Section
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = transaction.type.name.replace("_", " "),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                primaryChange?.let {
                    val amount = it.amount.toDouble() / 10.0.pow(it.decimals.toDouble())
                    val color = if (amount > 0) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface

                    Text(
                        text = "${if (amount > 0) "+" else ""}${amount.smartFormatAmount()} ${it.symbol}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = color,
                        textAlign = TextAlign.End
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val dateStr = transaction.timestamp?.let {
                    SimpleDateFormat("dd MMM y, HH:mm", Locale.getDefault()).format(Date(it * 1000))
                } ?: "Unknown time"

                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (!transaction.isSuccess) {
                    Text(
                        text = "Failed",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    secondaryChange?.let {
                        val amount = it.amount.toDouble() / 10.0.pow(it.decimals.toDouble())
                        Text(
                            text = "${if (amount > 0) "+" else ""}${amount.smartFormatAmount()} ${it.symbol}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.End
                        )
                    }
                }
            }

            // Signature / ID (truncated)
            Text(
                text = transaction.id.take(8) + "..." + transaction.id.takeLast(8),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
