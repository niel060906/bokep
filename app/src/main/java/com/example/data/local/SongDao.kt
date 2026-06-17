package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    fun getFavorites(): Flow<List<FavoriteSong>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(song: FavoriteSong)

    @Delete
    suspend fun removeFavorite(song: FavoriteSong)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE videoId = :videoId)")
    suspend fun isFavorite(videoId: String): Boolean
}
