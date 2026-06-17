package com.example.data.repository

import com.example.domain.Song
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class MusicRepository(
    private val firestore: FirebaseFirestore
) {
    fun getSongs(): Flow<List<Song>> = callbackFlow {
        val subscription = firestore.collection("songs")
            .orderBy("uploaded_at", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val songs = snapshot?.toObjects(Song::class.java) ?: emptyList()
                trySend(songs)
            }
        awaitClose { subscription.remove() }
    }

    fun searchSongs(query: String): Flow<List<Song>> = callbackFlow {
        val subscription = firestore.collection("songs")
            .limit(100)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val songs = snapshot?.toObjects(Song::class.java) ?: emptyList()
                val filtered = songs.filter { 
                    it.title.contains(query, ignoreCase = true) || 
                    it.artist.contains(query, ignoreCase = true) ||
                    it.video_id.contains(query, ignoreCase = true)
                }
                trySend(filtered)
            }
        awaitClose { subscription.remove() }
    }
}
