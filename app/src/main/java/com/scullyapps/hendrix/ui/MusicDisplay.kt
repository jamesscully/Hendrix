package com.scullyapps.hendrix.ui


import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.scullyapps.hendrix.R
import kotlinx.android.synthetic.main.widget_musicdisplay.view.*

class MusicDisplay(context: Context, title : String) : FrameLayout(context)  {

    enum class Type {
        ALBUM, SONG, ARTIST
    }



    init {
        LayoutInflater.from(context).inflate(R.layout.widget_musicdisplay, this, true)

        md_name.text = title
    }

    // if we're representing a song, then we'll need to show length
    // todo add song length to layout
    constructor(context: Context, title: String, length : Int) : this(context, title) {

    }

    // if we're representing an album, then we'll need to show song count / length
    constructor(context: Context, title: String, songCount : Int, albLength : Float) : this(context, title) {

    }


}