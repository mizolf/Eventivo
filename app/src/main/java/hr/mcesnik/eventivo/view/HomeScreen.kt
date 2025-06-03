package hr.mcesnik.eventivo.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import hr.mcesnik.eventivo.components.EventCard
import hr.mcesnik.eventivo.components.StaticSearchBar
import hr.mcesnik.eventivo.viewmodel.AuthViewModel
import hr.mcesnik.eventivo.viewmodel.EventViewModel
import hr.mcesnik.eventivo.viewmodel.FavoritesViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    eventViewModel: EventViewModel,
    favoritesViewModel: FavoritesViewModel,
    authViewModel: AuthViewModel
) {
    val events by eventViewModel.events.collectAsState()
    val userId by authViewModel.userId.collectAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var searchResults by remember { mutableStateOf(listOf<String>()) }

    fun performSearch(query: String) {
        searchResults = listOf("Rezultat 1", "Rezultat 2", "Rezultat 3").filter {
            it.contains(query, ignoreCase = true)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Eventivo", modifier = Modifier.padding(16.dp),style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ))
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                NavigationDrawerItem(
                    label = { Text("Home") },
                    selected = false,
                    icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                    onClick = {
                        navController.navigate("home")
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Favorites") },
                    selected = false,
                    icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                    onClick = {
                        navController.navigate("favorites")
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                NavigationDrawerItem(
                    label = { Text("Help and feedback") },
                    selected = false,
                    icon = { Icon(Icons.Filled.Info, contentDescription = null) },
                    onClick = { /* Handle click */ },
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Eventivo") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(imageVector = Icons.Default.Menu,
                                contentDescription = "Drawer Menu"
                            )
                        }
                    },

                )
            }

        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                StaticSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { performSearch(it) },
                )

                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                        .padding(innerPadding.calculateBottomPadding())
                ) {
                    Text("List of events", style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,

                    ))

                    IconButton(
                        onClick = {
                            navController.navigate("new-event")
                        },
                        modifier = Modifier
                            .background(Color.DarkGray, CircleShape)
                            .size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AddCircle,
                            contentDescription = "Add an event",
                            tint = Color.White
                        )
                    }
                }

                LazyColumn {
                    items(events) { event ->
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
}



