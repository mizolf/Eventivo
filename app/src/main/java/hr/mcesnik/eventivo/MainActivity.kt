package hr.mcesnik.eventivo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
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
import hr.mcesnik.eventivo.viewmodel.AuthViewModel
import hr.mcesnik.eventivo.viewmodel.EventViewModel
import hr.mcesnik.eventivo.viewmodel.FavoritesViewModel

@Composable
fun AppNavHost(navController: NavHostController) {
    val eventViewModel: EventViewModel = viewModel()
    val favoritesViewModel: FavoritesViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                navController,
                authViewModel
            )
        }
        composable("home") {
            HomeScreen(
                navController,
                eventViewModel = eventViewModel,
                favoritesViewModel = favoritesViewModel,
                authViewModel = authViewModel
            )
        }
        composable("favorites") {
            FavoriteScreen(
                navController,
                favoritesViewModel,
                authViewModel
            )
        }
        composable("event/{eventJson}",
            arguments = listOf(navArgument("eventJson"){ type=NavType.StringType})
            ) { backStackEntry ->
                val json = backStackEntry.arguments?.getString("eventJson")
                val event = Gson().fromJson(json, Event::class.java)
                EventScreen(
                    event = event,
                    navController = navController,
                    favoritesViewModel = favoritesViewModel,
                )
        }
        composable("profile") { ProfileScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("new-event") { NewEventScreen(navController) }
    }
}

class MainActivity : ComponentActivity() {


    private fun requestPermission() {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACTIVITY_RECOGNITION)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), 1)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Favorites Channel"
            val descriptionText = "Channel for favorite event notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("favorites_channel", name, importance).apply {
                description = descriptionText
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermission()
        createNotificationChannel()
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            EventivoTheme {
                AppNavHost(navController = navController)
            }
        }
    }
}

