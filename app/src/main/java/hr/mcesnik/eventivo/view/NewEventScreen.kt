package hr.mcesnik.eventivo.view

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import hr.mcesnik.eventivo.model.Event
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEventScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    val title = remember { mutableStateOf("") }
    val date = remember { mutableStateOf<Date?>(null) }
    val clothing = remember { mutableStateOf("") }
    val drink = remember { mutableStateOf("") }
    val music = remember { mutableStateOf("") }
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val uploading = remember { mutableStateOf(false) }

    val showDatePicker = remember { mutableStateOf(false) }
    val showTimePicker = remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()

    val selectedYear = remember { mutableStateOf(0) }
    val selectedMonth = remember { mutableStateOf(0) }
    val selectedDay = remember { mutableStateOf(0) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            imageUri.value = uri
        }
    )

    if (showDatePicker.value) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker.value = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = millis
                        selectedYear.value = calendar.get(Calendar.YEAR)
                        selectedMonth.value = calendar.get(Calendar.MONTH)
                        selectedDay.value = calendar.get(Calendar.DAY_OF_MONTH)
                    }
                    showDatePicker.value = false
                    showTimePicker.value = true
                }) {
                    Text("Next")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker.value = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker.value) {
        AlertDialog(
            onDismissRequest = { showTimePicker.value = false },
            title = { Text("Select Time") },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize()
                ) {
                    TimePicker(state = timePickerState)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val calendar = Calendar.getInstance()
                    calendar.set(
                        selectedYear.value,
                        selectedMonth.value,
                        selectedDay.value,
                        timePickerState.hour,
                        timePickerState.minute,
                        0
                    )
                    date.value = calendar.time
                    showTimePicker.value = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Create a new event") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
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
            Button(
                onClick = { showDatePicker.value = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    date.value?.let {
                        SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(it)
                    } ?: "Select Date and Time"
                )
            }
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

            Button(onClick = { launcher.launch("image/*") }) {
                Text("Choose Image from Gallery")
            }

            imageUri.value?.let {
                Text("Selected image: ${it.lastPathSegment}")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (imageUri.value != null) {
                        uploading.value = true
                        val storageRef = FirebaseStorage.getInstance().reference
                        val imageRef = storageRef.child("events/${UUID.randomUUID()}.jpg")
                        imageRef.putFile(imageUri.value!!)
                            .continueWithTask { task ->
                                if (!task.isSuccessful) throw task.exception ?: Exception("Unknown upload error")
                                imageRef.downloadUrl
                            }.addOnSuccessListener { uri ->
                                val imageUrl = uri.toString()

                                val newEvent = Event(
                                    title = title.value,
                                    clothing = clothing.value,
                                    date = date.value,
                                    drink = drink.value,
                                    music = music.value,
                                    image = imageUrl
                                )

                                FirebaseFirestore.getInstance().collection("events")
                                    .add(newEvent)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Event created!", Toast.LENGTH_SHORT).show()
                                        uploading.value = false
                                        navController.popBackStack()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, "Failed to save event", Toast.LENGTH_SHORT).show()
                                        uploading.value = false
                                    }
                            }.addOnFailureListener {
                                Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
                                uploading.value = false
                            }
                    } else {
                        Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uploading.value
            ) {
                Text(if (uploading.value) "Uploading..." else "Create Event")
            }
        }
    }
}
