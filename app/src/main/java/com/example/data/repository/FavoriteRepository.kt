package com.example.data.repository

import com.example.data.local.FavoriteSong
import com.example.data.local.SongDao
import com.example.domain.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteRepository(
    private val songDao: SongDao
) {
    val favorites: Flow<List<Song>> = songDao.getFavorites().map { list ->
        list.map { it.toSong() }
    }

    suspend fun toggleFavorite(song: Song) {
        if (songDao.isFavorite(song.video_id)) {
            songDao.removeFavorite(song.toFavorite())
        } else {
            songDao.addFavorite(song.toFavorite())
        }
    }

    suspend fun isFavorite(videoId: String): Boolean = songDao.isFavorite(videoId)

    private fun Song.toFavorite() = FavoriteSong(
        videoId = video_id,
        title = title,
        artist = artist,
        duration = duration,
        catboxMp3 = catbox_mp3,
        catboxThumb = catbox_thumb
    )

    private fun FavoriteSong.toSong() = Song(
        video_id = videoId,
        title = title,
        artist = artist,
        duration = duration,
        catbox_mp3 = catboxMp3,
        catbox_thumb = catboxThumb
    )
}
