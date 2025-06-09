package hr.mcesnik.eventivo.view

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun NewEventScreen(navController: NavHostController) {
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
        onResult = { uri: Uri? -> imageUri.value = uri }
    )

    if (showDatePicker.value) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker.value = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val calendar = Calendar.getInstance().apply { timeInMillis = it }
                        selectedYear.value = calendar.get(Calendar.YEAR)
                        selectedMonth.value = calendar.get(Calendar.MONTH)
                        selectedDay.value = calendar.get(Calendar.DAY_OF_MONTH)
                    }
                    showDatePicker.value = false
                    showTimePicker.value = true
                }) {
                    Text("Next", color = Color(0xFF333333))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker.value = false }) {
                    Text("Cancel", color = Color(0xFF333333))
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
                Box(Modifier.fillMaxWidth().wrapContentSize()) {
                    TimePicker(state = timePickerState)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val calendar = Calendar.getInstance().apply {
                        set(
                            selectedYear.value,
                            selectedMonth.value,
                            selectedDay.value,
                            timePickerState.hour,
                            timePickerState.minute,
                            0
                        )
                    }
                    date.value = calendar.time
                    showTimePicker.value = false
                }) {
                    Text("OK", color = Color(0xFF333333))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker.value = false }) {
                    Text("Cancel", color = Color(0xFF333333))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Create Event", color = Color(0xFF333333)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF333333))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFF5F5F5)
                )
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            val fieldColors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color(0xFF333333),
                unfocusedTextColor = Color(0xFF333333),
                focusedBorderColor = Color(0xFF333333),
                unfocusedBorderColor = Color(0xFFCCCCCC),
                focusedLabelColor = Color(0xFF333333),
                unfocusedLabelColor = Color(0xFF666666),
                cursorColor = Color(0xFF333333)
            )

            val roundedShape = RoundedCornerShape(24.dp)

            OutlinedTextField(
                value = title.value,
                onValueChange = { title.value = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                shape = roundedShape,
                colors = fieldColors
            )

            OutlinedTextField(
                value = clothing.value,
                onValueChange = { clothing.value = it },
                label = { Text("Clothing Style") },
                modifier = Modifier.fillMaxWidth(),
                shape = roundedShape,
                colors = fieldColors
            )

            OutlinedTextField(
                value = drink.value,
                onValueChange = { drink.value = it },
                label = { Text("Drinks Offered") },
                modifier = Modifier.fillMaxWidth(),
                shape = roundedShape,
                colors = fieldColors
            )

            OutlinedTextField(
                value = music.value,
                onValueChange = { music.value = it },
                label = { Text("Music Type") },
                modifier = Modifier.fillMaxWidth(),
                shape = roundedShape,
                colors = fieldColors
            )

            Button(
                onClick = { showDatePicker.value = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF333333),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = date.value?.let {
                        SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(it)
                    } ?: "Select Date and Time"
                )
            }

            Button(
                onClick = { launcher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF333333),
                    contentColor = Color.White
                )
            ) {
                Text("Choose Image from Gallery")
            }

            imageUri.value?.let {
                Text(
                    "Selected image: ${it.lastPathSegment}",
                    color = Color(0xFF333333),
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = {
                    when {
                        title.value.isBlank() -> Toast.makeText(context, "Title is required", Toast.LENGTH_SHORT).show()
                        date.value == null -> Toast.makeText(context, "Date and time are required", Toast.LENGTH_SHORT).show()
                        clothing.value.isBlank() -> Toast.makeText(context, "Clothing style is required", Toast.LENGTH_SHORT).show()
                        drink.value.isBlank() -> Toast.makeText(context, "Drinks offered is required", Toast.LENGTH_SHORT).show()
                        music.value.isBlank() -> Toast.makeText(context, "Music type is required", Toast.LENGTH_SHORT).show()
                        imageUri.value == null -> Toast.makeText(context, "Image is required", Toast.LENGTH_SHORT).show()
                        else -> {
                            uploading.value = true
                            val firestore = FirebaseFirestore.getInstance()
                            val storageRef = FirebaseStorage.getInstance().reference
                            val imageRef = storageRef.child("events/${UUID.randomUUID()}.jpg")

                            imageRef.putFile(imageUri.value!!)
                                .continueWithTask { task ->
                                    if (!task.isSuccessful) throw task.exception ?: Exception("Unknown error")
                                    imageRef.downloadUrl
                                }.addOnSuccessListener { uri ->
                                    val event = Event(
                                        id = UUID.randomUUID().toString(),
                                        title = title.value,
                                        date = date.value,
                                        clothing = clothing.value,
                                        drink = drink.value,
                                        music = music.value,
                                        image = uri.toString()
                                    )
                                    firestore.collection("events").add(event)
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Event created!", Toast.LENGTH_SHORT).show()
                                            navController.popBackStack()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, "Failed to save event", Toast.LENGTH_SHORT).show()
                                        }.also {
                                            uploading.value = false
                                        }
                                }.addOnFailureListener {
                                    Toast.makeText(context, "Upload failed", Toast.LENGTH_SHORT).show()
                                    uploading.value = false
                                }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !uploading.value,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF333333),
                    contentColor = Color.White
                )
            ) {
                Text(if (uploading.value) "Uploading..." else "Create Event")
            }
        }
    }
}

