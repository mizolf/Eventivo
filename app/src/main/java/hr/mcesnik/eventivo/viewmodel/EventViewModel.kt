package hr.mcesnik.eventivo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import hr.mcesnik.eventivo.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class EventViewModel : ViewModel() {
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events

    private val firestore = FirebaseFirestore.getInstance()

    init {
        fetchEvents()
    }

    private fun fetchEvents(){
        viewModelScope.launch {
            firestore.collection("events")
                .get()
                .addOnSuccessListener { result ->
                    val eventList = result.documents.mapNotNull{ it.toObject<Event>()}
                    _events.value = eventList
                }
                .addOnFailureListener{
                    _events.value = emptyList()
                }
        }
    }
}