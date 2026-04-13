package com.mrf.tghost.app.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Surface
import com.mrf.tghost.app.navigation.NavGraph
import com.mrf.tghost.app.ui.theme.TGhostTheme
import com.mrf.tghost.app.ui.theme.primaryBlack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            TGhostTheme {
                Surface {
                    NavGraph()
                }
            }
        }
    }
}




