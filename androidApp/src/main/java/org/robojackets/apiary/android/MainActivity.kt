package org.robojackets.apiary.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import org.robojackets.apiary.android.ui.ApiaryAndroid
import org.robojackets.apiary.android.ui.theme.Apiary_MobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Apiary_MobileTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    ApiaryAndroid()
                }
            }
        }
    }
}