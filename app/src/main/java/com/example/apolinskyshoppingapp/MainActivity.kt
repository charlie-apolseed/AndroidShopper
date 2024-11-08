package com.example.apolinskyshoppingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.apolinskyshoppingapp.ui.screen.ShoppingScreen
import com.example.apolinskyshoppingapp.ui.theme.ShoppingAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var showSplashScreen by remember { mutableStateOf(true) }

            // Launching the 3-second delay to hide the splash screen
            LaunchedEffect(Unit) {
                delay(3000)
                showSplashScreen = false
            }

            ShoppingAppTheme {
                if (showSplashScreen) {
                    SplashScreen()
                } else {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        ShoppingAppNavHost(Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD6BDE0)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.shoppingcart),
            contentDescription = null
        )
    }
}

@Composable
fun ShoppingAppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "shoppingApp"
) {
    NavHost(
        navController = navController, startDestination = startDestination, modifier = modifier
    ) {
        composable("shoppingApp") { ShoppingScreen() }
    }
}