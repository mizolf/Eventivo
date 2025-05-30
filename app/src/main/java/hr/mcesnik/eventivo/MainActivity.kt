package hr.mcesnik.eventivo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import hr.mcesnik.eventivo.model.Event
import hr.mcesnik.eventivo.ui.theme.EventivoTheme
import hr.mcesnik.eventivo.view.EventScreen
import hr.mcesnik.eventivo.view.FavoriteScreen
import hr.mcesnik.eventivo.view.HomeScreen
import hr.mcesnik.eventivo.view.LoginScreen
import hr.mcesnik.eventivo.view.NewEventScreen
import hr.mcesnik.eventivo.view.ProfileScreen
import hr.mcesnik.eventivo.view.RegisterScreen

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("home") { HomeScreen(navController) }
        composable("favorites") { FavoriteScreen(navController)  }
        composable("event/{eventJson}",
            arguments = listOf(navArgument("eventJson"){ type=NavType.StringType})
            ) { backStackEntry ->
                val json = backStackEntry.arguments?.getString("eventJson")
                val event = Gson().fromJson(json, Event::class.java)
                EventScreen(event = event, navController = navController)
        }
        composable("profile") { ProfileScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("new-event") { NewEventScreen(navController) }
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

