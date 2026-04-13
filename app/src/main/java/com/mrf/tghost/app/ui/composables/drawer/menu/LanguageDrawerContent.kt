package com.mrf.tghost.app.ui.composables.drawer.menu

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mrf.tghost.app.utils.enums.Language

@Composable
fun LanguageDrawerContent (
    onLanguageClick: (Language) -> Unit
) {

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(Language.entries.toTypedArray()) {
                ListItem(
                    value = it.displayName,
                    onClick = {
                        onLanguageClick(it)
                    }
                )
            }
        }



}