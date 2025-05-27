package hr.mcesnik.eventivo

import hr.mcesnik.eventivo.presentation.ui.screens.HomeScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hr.mcesnik.eventivo.presentation.ui.screens.EventScreen
import hr.mcesnik.eventivo.presentation.ui.screens.FavoriteScreen
import hr.mcesnik.eventivo.presentation.ui.screens.LoginScreen
import hr.mcesnik.eventivo.ui.theme.EventivoTheme

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("favorites") { FavoriteScreen(navController)  }
        composable("event") { EventScreen(navController)}
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            EventivoTheme {
                AppNavHost(navController = navController)
            }
        }
    }
}

