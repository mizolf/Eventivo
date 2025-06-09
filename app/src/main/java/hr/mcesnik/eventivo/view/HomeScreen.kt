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
import androidx.compose.material.icons.automirrored.outlined.Logout
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
import androidx.compose.runtime.LaunchedEffect
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
    val userEmail by authViewModel.userEmail.collectAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var filteredEvents by remember { mutableStateOf(events) }

    fun performSearch(query: String) {
        filteredEvents = if (query.isEmpty()) {
            events
        } else {
            events.filter { event ->
                event.title.contains(query, ignoreCase = true) ||
                        event.clothing.contains(query, ignoreCase = true) ||
                        event.drink.contains(query, ignoreCase = true) ||
                        event.music.contains(query, ignoreCase = true)
            }
        }
    }

    LaunchedEffect(events, searchQuery) {
        performSearch(searchQuery)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Eventivo", modifier = Modifier.padding(16.dp),style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ))
                userEmail?.let {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = it,
                        style = TextStyle(
                            color = Color.Gray,
                            fontSize = 14.sp,
                        )
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                NavigationDrawerItem(
                    label = { Text("Home") },
                    selected = false,
                    icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                    onClick = {
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Favorites") },
                    selected = false,
                    icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                    onClick = {
                        navController.navigate("favorites")
                        scope.launch { drawerState.close() }
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                NavigationDrawerItem(
                    label = { Text("Help and feedback") },
                    selected = false,
                    icon = { Icon(Icons.Filled.Info, contentDescription = null) },
                    onClick = {
                        navController.navigate("help")
                        scope.launch { drawerState.close() }
                    },
                )
                NavigationDrawerItem(
                    label = { Text("Logout") },
                    selected = false,
                    icon = { Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Logout,
                        contentDescription = "Logout"
                    ) },
                    onClick = {
                        authViewModel.logout()
                        navController.navigate("login") {
                            popUpTo(0)
                            launchSingleTop = true
                        }
                        scope.launch { drawerState.close() }
                    },
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
                    onQueryChange = {
                        searchQuery = it
                        performSearch(it)
                    },
                    onSearch = { performSearch(it) },
                )

                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column {
                        Text("List of events", style = TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 24.sp,
                        ))
                        if (searchQuery.isNotEmpty()) {
                            Text(
                                text = "${filteredEvents.size} results for \"$searchQuery\"",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            )
                        }
                    }

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

                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    items(filteredEvents) { event ->
                        EventCard(
                            event,
                            navController,
                            favoritesViewModel,
                            authViewModel
                        )
                    }

                    if (filteredEvents.isEmpty() && searchQuery.isNotEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Nema događaja koji odgovaraju vašoj pretrazi",
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        color = Color.Gray
                                    )
                                )

                            }
                        }
                    }
                }
            }
        }
    }
}



