package hr.mcesnik.eventivo.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import hr.mcesnik.eventivo.components.EventInfoCard
import hr.mcesnik.eventivo.model.Event
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreen(
    navController: NavHostController,
    event: Event
){
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title =  { Text(event.title) },
                modifier = Modifier,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Menu"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {  }) {
                        Icon(
                            imageVector = Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorite"
                        )
                    }
                }
            )
        }
    ){ innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.height(250.dp)

            ) {
                Image(
                    painter = rememberAsyncImagePainter(event.image),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            width = 1.dp,
                            color = Color.Transparent,
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentScale = ContentScale.Crop
                )
            }
            EventInfoCard(icon = Icons.Default.LocalDrink, label = "Drink", value = event.drink)
            EventInfoCard(icon = Icons.Default.MusicNote, label = "Music", value = event.music)
            EventInfoCard(icon = Icons.Default.Checkroom, label = "Dress Code", value = event.clothing)

            val formattedDate = event.date?.let {
                SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.getDefault()).format(it)
            } ?: "Date TBD"

            EventInfoCard(icon = Icons.Default.Event, label = "Date", value = formattedDate)
        }
    }
}