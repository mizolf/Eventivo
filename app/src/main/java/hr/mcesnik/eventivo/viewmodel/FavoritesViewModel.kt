package hr.mcesnik.eventivo.viewmodel

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import hr.mcesnik.eventivo.R
import hr.mcesnik.eventivo.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class FavoritesViewModel : ViewModel() {
    private val _favoriteEvents = MutableStateFlow<List<Event>>(emptyList())
    val favoriteEvents: StateFlow<List<Event>> = _favoriteEvents

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var favoritesListener: ListenerRegistration? = null

    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())

    fun isFavorite(eventId: String): StateFlow<Boolean> =
        _favoriteIds.map { it.contains(eventId) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    init {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            favoritesListener?.remove()

            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                listenToFavoriteEvents(currentUser.uid)
            } else {
                _favoriteEvents.value = emptyList()
                _favoriteIds.value = emptySet()
            }
        }

        auth.addAuthStateListener(authStateListener)
    }

    private fun listenToFavoriteEvents(userId: String) {
        favoritesListener = firestore.collection("users").document(userId).collection("favorites")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FavoritesViewModel", "Listen failed", error)
                    return@addSnapshotListener
                }

                val events = snapshot?.documents?.mapNotNull {
                    it.toObject(Event::class.java)?.copy(id = it.id)
                } ?: emptyList()
                val ids = snapshot?.documents?.mapNotNull { it.id }?.toSet() ?: emptySet()

                _favoriteEvents.value = events
                _favoriteIds.value = ids
            }
    }

    private fun sendFavoriteAddedNotification(context: Context, eventTitle: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, "favorites_channel")
            .setSmallIcon(R.drawable.ic_favorite)
            .setContentTitle("Added to favorites")
            .setContentText("Event \"$eventTitle\" is added to favorites.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(eventTitle.hashCode(), notification)
    }

    fun addToFavorites(event: Event, context: Context) {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId.isNullOrBlank() || event.id.isBlank()) return

        firestore.collection("users")
            .document(currentUserId)
            .collection("favorites")
            .document(event.id)
            .set(event)
            .addOnSuccessListener {
                sendFavoriteAddedNotification(context, event.title)
            }
    }

    fun removeFromFavorites(eventId: String) {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId.isNullOrBlank() || eventId.isBlank()) return

        firestore.collection("users")
            .document(currentUserId)
            .collection("favorites")
            .document(eventId)
            .delete()
    }

    override fun onCleared() {
        super.onCleared()
        favoritesListener?.remove()
    }
}