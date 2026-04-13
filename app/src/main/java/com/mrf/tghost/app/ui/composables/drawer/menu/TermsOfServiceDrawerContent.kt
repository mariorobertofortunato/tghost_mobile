package com.mrf.tghost.app.ui.composables.drawer.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.mrf.tghost.R
import com.mrf.tghost.app.ui.composables.cta.BaseCtaFullWidth
import com.mrf.tghost.app.ui.composables.decoration.HorizontalDivider
import com.mrf.tghost.app.ui.composables.text.TextComponent
import com.mrf.tghost.app.ui.theme.DividerDimensions.dividerExtraSmall
import com.mrf.tghost.app.ui.theme.IconDimensions.iconSmall
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingBig
import com.mrf.tghost.app.ui.theme.PaddingDimensions.paddingNormal
import com.mrf.tghost.app.utils.enums.TextSize

@Composable
fun TermsOfServiceDrawerContent (
    onDismiss: () -> Unit,
){

    val scrollState = rememberScrollState()

    val termsList = listOf(
        null to stringResource(R.string.terms_intro),
        stringResource(R.string.terms_acceptance) to stringResource(R.string.terms_acceptance_body),
        stringResource(R.string.terms_eligibility) to stringResource(R.string.terms_eligibility_body),
        stringResource(R.string.terms_description) to stringResource(R.string.terms_description_body),
        stringResource(R.string.terms_user_responsibilities) to stringResource(R.string.terms_user_responsibilities_body),
        stringResource(R.string.terms_privacy_policy) to stringResource(R.string.terms_privacy_policy_body),
        stringResource(R.string.terms_disclaimers) to stringResource(R.string.terms_disclaimers_body),
        stringResource(R.string.terms_modifications) to stringResource(R.string.terms_modifications_body),
        stringResource(R.string.terms_termination) to stringResource(R.string.terms_termination_body),
        stringResource(R.string.terms_governing_law) to stringResource(R.string.terms_governing_law_body),
        stringResource(R.string.terms_contact) to stringResource(R.string.terms_contact_body),
        null to stringResource(R.string.terms_acknowledgment)
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(paddingBig),
    ){
        Column(
            verticalArrangement = Arrangement.spacedBy(paddingNormal),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .verticalScroll(scrollState)
                .weight(1f, false)
        ){
            termsList.forEachIndexed { index, (head, body) ->
                if (index ==  1) { // Add divider before the last item
                    HorizontalDivider(
                        thickness = dividerExtraSmall,
                    )
                }
                TermsOfServiceItem(
                    head = head,
                    body = body
                )
                if (index == termsList.lastIndex - 1) { // Add divider after the first item
                    HorizontalDivider(
                        thickness = dividerExtraSmall,
                    )
                }
            }

        }

        BaseCtaFullWidth(
            text = stringResource(R.string.got_it),
            textSize = TextSize.BODY,
            icon = painterResource(R.drawable.ic_confirm),
            iconSize = iconSmall,
            onClick = onDismiss,
        )

    }
}

@Composable
fun TermsOfServiceItem(head: String? = null, body: String){
    Column{
        if (head != null) {
            TextComponent(
                text = head,
                textSize = TextSize.TITLE
            )
        }
        TextComponent(
            text = body,
            textSize = TextSize.BODY
        )

    }
}