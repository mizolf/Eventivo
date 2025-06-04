package hr.mcesnik.eventivo.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.gson.Gson
import hr.mcesnik.eventivo.model.Event
import hr.mcesnik.eventivo.viewmodel.AuthViewModel
import hr.mcesnik.eventivo.viewmodel.FavoritesViewModel
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun EventCard(
    event: Event,
    navController: NavHostController,
    favoritesViewModel: FavoritesViewModel,
    authViewModel: AuthViewModel,
){
    val isFavorite by favoritesViewModel.isFavorite(event.id).collectAsState()
    val eventJson = Uri.encode(Gson().toJson(event))
    val navigateToEventScreen = {
        navController.navigate("event/$eventJson")
    }

    Column {
        Box(
            modifier = Modifier.height(180.dp)
                .padding(8.dp)
                .clickable{ navigateToEventScreen()}
        ) {
            Image(
                painter = rememberAsyncImagePainter(event.image),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .border(
                        width = 1.dp,
                        color = Color.Transparent,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            IconButton(
                onClick = {
                    if (isFavorite) {
                        favoritesViewModel.removeFromFavorites(event.id)
                    } else {
                        favoritesViewModel.addToFavorites(event)
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .background(Color.White.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(
                    imageVector = if(isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite"
                )
            }
        }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
                        .padding(8.dp)
            ) {
                Column() {
                    Text(
                        text = event.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${event.date?.let { SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(it) } ?: "Date TBD"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                IconButton(
                    onClick = {
                        navigateToEventScreen()
                    },
                    modifier = Modifier
                        .background(Color.DarkGray, CircleShape)
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Go",
                        tint = Color.White
                    )
                }
            }
    }
}