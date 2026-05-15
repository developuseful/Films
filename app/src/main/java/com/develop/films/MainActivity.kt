package com.develop.films

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.develop.films.presentation.navigation.SetupNavGraph
import com.develop.films.ui.theme.FilmsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FilmsTheme {
                SetupNavGraph()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    FilmsTheme {
        SetupNavGraph()
    }
}