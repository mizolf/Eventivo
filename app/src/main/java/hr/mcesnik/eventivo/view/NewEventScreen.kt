package hr.mcesnik.eventivo.view

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEventScreen(
    navController: NavHostController
) {
    val title = remember { mutableStateOf("") }
    val date = remember { mutableStateOf("") }
    val clothing = remember { mutableStateOf("") }
    val drink = remember { mutableStateOf("") }
    val music = remember { mutableStateOf("") }
    val image = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title =  { Text("Create a new event") },
                modifier = Modifier,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Menu"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title.value,
                onValueChange = { title.value = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = date.value,
                onValueChange = { date.value = it },
                label = { Text("Date (e.g., 2025-06-01)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = clothing.value,
                onValueChange = { clothing.value = it },
                label = { Text("Clothing style") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = drink.value,
                onValueChange = { drink.value = it },
                label = { Text("Drinks offered") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = music.value,
                onValueChange = { music.value = it },
                label = { Text("Music type") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = image.value,
                onValueChange = { image.value = it },
                label = { Text("Image URL") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // Ovdje možeš spremiti event (npr. u ViewModel ili Firebase)
                    navController.popBackStack() // Vrati se na prethodni ekran
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Event")
            }
        }
    }
}