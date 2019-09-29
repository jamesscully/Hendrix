package com.scullyapps.hendrix.ui


import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.scullyapps.hendrix.R

class MusicDisplay(context: Context, title : String, img : Image) : FrameLayout(context)  {

    init {
        LayoutInflater.from(context).inflate(R.layout.widget_musicdisplay, this, true)
    }

}