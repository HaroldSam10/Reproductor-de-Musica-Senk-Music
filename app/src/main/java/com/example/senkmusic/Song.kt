package com.example.senkmusic

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Song(
    val title: String,
    val artist: String,
    val duration: String,
    val coverArt: String, // vamos hacer una ruta por eso el string
    val musicFile: Int // de aqui papi es el raw
) : Parcelable