package com.mrf.tghost.app.ui.composables.loadingindicators

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mrf.tghost.R
import com.mrf.tghost.app.ui.theme.primaryGreen

@Composable
fun CircularLoadingIndicator (){
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
    ){
        Box(
            contentAlignment = Alignment.Center
        ){
            Image(
                painter = painterResource(id = R.drawable.axii_app_icon_1024),
                contentDescription = stringResource(R.string.splash_screen_image),
                modifier = Modifier
                    .size(160.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                    .clip(CircleShape)
            )
            CircularProgressIndicator(
                color = primaryGreen,
                trackColor = primaryGreen.copy(alpha = 0.35f),
                strokeWidth = 24.dp,
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .size(240.dp)
            )
        }
    }
    
}

@Preview
@Composable
fun CircularLoadingIndicatorPreview(){
    CircularLoadingIndicator()
}