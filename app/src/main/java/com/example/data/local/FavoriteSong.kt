package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteSong(
    @PrimaryKey val videoId: String,
    val title: String,
    val artist: String,
    val duration: Long,
    val catboxMp3: String,
    val catboxThumb: String,
    val addedAt: Long = System.currentTimeMillis()
)
