package com.example.grammarous.story

import android.provider.MediaStore.Audio.Media
import java.net.URL

data class Story(
    var id:String,
    var name:String,
    var authorName:String?,
    var audio:URL?,
    var images:List<URL?>,
    var thumbNail:URL
)
