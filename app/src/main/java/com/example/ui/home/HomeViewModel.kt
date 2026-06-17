package com.example.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.MusicRepository
import com.example.domain.Song
import com.example.player.MusicController
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    private val repository: MusicRepository,
    private val musicController: MusicController
) : ViewModel() {

    val songs: StateFlow<List<Song>> = repository.getSongs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val currentSong = musicController.currentSong
    val isPlaying = musicController.isPlaying

    fun onSongClick(song: Song) {
        musicController.playSong(song, songs.value)
    }

    fun togglePlayPause() {
        musicController.togglePlayPause()
    }
}
