package hr.mcesnik.eventivo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import hr.mcesnik.eventivo.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date


class EventViewModel : ViewModel() {
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events

    private val firestore = FirebaseFirestore.getInstance()
    private var eventsListener: ListenerRegistration? = null

    init {
        listenToEvents()
    }

    private fun listenToEvents() {
        eventsListener = firestore.collection("events")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("EventViewModel", "Error listening to events", error)
                    _events.value = emptyList()
                    return@addSnapshotListener
                }

                snapshot?.let { querySnapshot ->
                    val now = Date()

                    val eventList = querySnapshot.documents.mapNotNull { document ->
                        try {
                            val event = document.toObject(Event::class.java)?.copy(id = document.id)
                            if (event != null && event.date != null && event.date.before(now)) {
                                firestore.collection("events").document(document.id).delete()
                                null
                            } else {
                                event
                            }
                        } catch (e: Exception) {
                            Log.e("EventViewModel", "Error converting document to Event", e)
                            null
                        }
                    }
                    _events.value = eventList
                    Log.d("EventViewModel", "Events updated: ${eventList.size} events loaded")
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        eventsListener?.remove()
    }
}