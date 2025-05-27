package hr.mcesnik.eventivo.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import hr.mcesnik.eventivo.components.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import hr.mcesnik.eventivo.components.StaticSearchBar
import hr.mcesnik.eventivo.viewmodel.EventViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, viewModel: EventViewModel = viewModel()) {
    val events by viewModel.events.collectAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()


    var searchQuery by rememberSaveable { mutableStateOf("") }
    var searchResults by remember { mutableStateOf(listOf<String>()) }
    //App Bar
        //Drawer
        //Profile?

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
                    onClick = { }
                )
                NavigationDrawerItem(
                    label = { Text("Favorites") },
                    selected = false,
                    onClick = { }
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
                    title = "Eventivo",
                    onMenuClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    onProfileClick = {
                        navController.navigate("profile") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                StaticSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { performSearch(it) },
                )
                LazyColumn {
                    items(events) { event ->
                        Text(event.title)
                    }
                }
            }

        }
    }
    //List of events
}



