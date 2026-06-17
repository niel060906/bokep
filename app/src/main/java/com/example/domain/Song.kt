package com.example.domain

import kotlinx.serialization.Serializable

@Serializable
data class Song(
    val video_id: String = "",
    val title: String = "",
    val artist: String = "",
    val duration: Long = 0,
    val catbox_mp3: String = "",
    val catbox_thumb: String = "",
    val uploaded_at: String = ""
)
