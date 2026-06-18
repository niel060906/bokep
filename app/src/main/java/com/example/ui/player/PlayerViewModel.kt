package com.example.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.FavoriteRepository
import com.example.player.MusicController
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val musicController: MusicController,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    val currentSong = musicController.currentSong
    val isPlaying = musicController.isPlaying
    val playbackState = musicController.playbackState
    val currentPosition = musicController.currentPosition
    val duration = musicController.duration
    val playbackError = musicController.error

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite = _isFavorite.asStateFlow()

    init {
        currentSong.onEach { song ->
            song?.let { 
                _isFavorite.value = favoriteRepository.isFavorite(it.video_id)
            }
        }.launchIn(viewModelScope)
    }

    fun togglePlayPause() = musicController.togglePlayPause()
    fun next() = musicController.next()
    fun previous() = musicController.previous()
    fun seekTo(position: Long) = musicController.seekTo(position)

    fun toggleFavorite() {
        val song = currentSong.value ?: return
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(song)
            _isFavorite.value = favoriteRepository.isFavorite(song.video_id)
        }
    }
}
