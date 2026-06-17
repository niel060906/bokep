package com.example

import android.app.Application
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.repository.MusicRepository
import com.example.data.repository.FavoriteRepository
import com.example.player.MusicController
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MusicApplication : Application() {
    
    lateinit var musicRepository: MusicRepository
    lateinit var favoriteRepository: FavoriteRepository
    lateinit var musicController: MusicController

    override fun onCreate() {
        super.onCreate()
        
        val options = FirebaseOptions.Builder()
            .setApiKey("AIzaSyChJUtOEnYGW9iryHYqst2ql-oNDdJyysw")
            .setApplicationId("1:457308432728:web:01568ae63de26444105d2d")
            .setProjectId("marketplace-ea770")
            .setStorageBucket("marketplace-ea770.firebasestorage.app")
            .build()
            
        FirebaseApp.initializeApp(this, options)

        val db = Room.databaseBuilder(this, AppDatabase::class.java, "music_stream_db").build()
        musicRepository = MusicRepository(Firebase.firestore)
        favoriteRepository = FavoriteRepository(db.songDao())
        musicController = MusicController(this)
    }
}
