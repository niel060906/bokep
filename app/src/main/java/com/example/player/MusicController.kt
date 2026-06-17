package com.example.player

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.example.domain.Song
import com.example.player.service.PlaybackService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MusicController(
    private val context: Context
) : Player.Listener {

    private var browserFuture: ListenableFuture<MediaBrowser>? = null
    private var browser: MediaBrowser? = null

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _playbackState = MutableStateFlow(Player.STATE_IDLE)
    val playbackState = _playbackState.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration = _duration.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        initializeBrowser()
        startPositionPolling()
    }

    private fun startPositionPolling() {
        scope.launch {
            while (true) {
                browser?.let {
                    if (it.isPlaying) {
                        _currentPosition.value = it.currentPosition
                        _duration.value = it.duration.coerceAtLeast(0L)
                    }
                }
                delay(1000L)
            }
        }
    }

    private fun initializeBrowser() {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        browserFuture = MediaBrowser.Builder(context, sessionToken).buildAsync()
        browserFuture?.addListener({
            browser = browserFuture?.get()
            browser?.addListener(this)
        }, MoreExecutors.directExecutor())
    }

    fun playSong(song: Song, allSongs: List<Song>) {
        val browser = browser ?: return
        
        val mediaItems = allSongs.map { s ->
            MediaItem.Builder()
                .setMediaId(s.video_id)
                .setUri(s.catbox_mp3)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(s.title)
                        .setArtist(s.artist)
                        .setArtworkUri(android.net.Uri.parse(s.catbox_thumb))
                        .build()
                )
                .build()
        }

        val startIndex = allSongs.indexOfFirst { it.video_id == song.video_id }.coerceAtLeast(0)
        
        browser.setMediaItems(mediaItems, startIndex, 0L)
        browser.prepare()
        browser.play()
        _currentSong.value = song
    }

    fun togglePlayPause() {
        browser?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    fun next() { browser?.seekToNext() }
    fun previous() { browser?.seekToPrevious() }

    fun seekTo(position: Long) {
        browser?.seekTo(position)
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        _isPlaying.value = isPlaying
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        _playbackState.value = playbackState
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        mediaItem?.let { item ->
            _currentSong.value = Song(
                video_id = item.mediaId,
                title = item.mediaMetadata.title?.toString() ?: "",
                artist = item.mediaMetadata.artist?.toString() ?: "",
                catbox_thumb = item.mediaMetadata.artworkUri?.toString() ?: ""
            )
        }
    }

    fun destroy() {
        scope.cancel()
        browserFuture?.let { MediaBrowser.releaseFuture(it) }
        browser?.removeListener(this)
        browser = null
    }
}
