package hr.mcesnik.eventivo.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import hr.mcesnik.eventivo.components.EventCard
import hr.mcesnik.eventivo.viewmodel.AuthViewModel
import hr.mcesnik.eventivo.viewmodel.FavoritesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    navController: NavHostController,
    favoritesViewModel: FavoritesViewModel,
    authViewModel: AuthViewModel
){
    val favoriteEvents by favoritesViewModel.favoriteEvents.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title =  { Text("Favorite events") },
                modifier = Modifier,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },

            )
        }
    ){ innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            LazyColumn {
                items(favoriteEvents) { event ->
                    EventCard(
                        event,
                        navController,
                        favoritesViewModel,
                        authViewModel
                    )
                }
            }
        }

    }
}