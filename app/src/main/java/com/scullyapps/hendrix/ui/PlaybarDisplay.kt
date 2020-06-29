package com.scullyapps.hendrix.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.math.MathUtils
import com.scullyapps.hendrix.models.song.Bookmark

class PlaybarDisplay(context : Context, attr: AttributeSet) : View(context, attr) {
    private val TAG: String = "PlaybarDisplay";

    private var bitmap : Bitmap? = null
    private var canvas : Canvas? = null

    private var path : Path = Path()


    private var playedPaint : Paint = Paint()
    private var bookmarkPaint: Paint = Paint()
    private var backgroundPaint: Paint = Paint()




    private var bookmarks : ArrayList<Bookmark> = ArrayList()

    // current time on song
    var time : Int = 1
    // duration of song
    var duration : Int = 1

    // gives percentage of way through with up/low bounds
    var progress : Float = 0.0f
        get() {
            if(duration == 0 || time == 0) {
                return 0F
            }
            else {
                val prog = (time.toFloat() / duration.toFloat())
                return MathUtils.clamp(prog, 0.0f, 1.0f)
            }
        }

    init {
        setPaintBrushes()
    }

    private fun setPaintBrushes() {
        playedPaint.style = Paint.Style.FILL;
        playedPaint.setARGB(255,20,6,54);

        backgroundPaint.setARGB(255, 74, 54, 107)
    }

    fun setBookmarks(marks : ArrayList<Bookmark>) {
        bookmarks = marks
    }

    fun drawAllBookmarks() {
        for(x in bookmarks) {
            drawBookmark(x)
        }
    }

    fun inv() {

    }

    fun drawBookmark(b : Bookmark) {

        // provides x position on playbar for bookmark
        val x = (b.timestamp.toFloat() / duration.toFloat()) * width
        val w : Int = 5

        Log.d(TAG, "Drawing Bookmark: $b x: $x)")

        canvas?.drawRect(x - (w / 2), 0F, x + (w / 2), height.toFloat() / 2, bookmarkPaint)

//        canvas?.drawRect(0F, 0F, width.toFloat(), height.toFloat(), paint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        bitmap = Bitmap.createBitmap(Math.max(w,h), Math.max(w,h), Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
    }

    override fun onDraw(canvas: Canvas?) {
        this.canvas = canvas

        // draw background
        canvas?.drawRect(0F, 0F, width.toFloat(), height.toFloat(), backgroundPaint)

        // set width to how far we've progressed in song
        canvas?.drawRect(0F, (height.toFloat() / 2), width.toFloat() * progress, height.toFloat(), playedPaint)

        drawAllBookmarks()
    }

}