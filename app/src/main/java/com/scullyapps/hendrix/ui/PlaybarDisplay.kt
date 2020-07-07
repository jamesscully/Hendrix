package com.scullyapps.hendrix.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.math.MathUtils
import com.scullyapps.hendrix.models.song.Bookmark
import com.scullyapps.hendrix.ui.sound.SoundPlayer

class PlaybarDisplay(context : Context, attr: AttributeSet) : View(context, attr) {
    private val TAG: String = "PlaybarDisplay";

    private var bitmap : Bitmap? = null
    var canvas : Canvas? = null

    private var path : Path = Path()

    private var playedPaint : Paint = Paint()
    private var bookmarkPaint: Paint = Paint()
    private var backgroundPaint: Paint = Paint()

    private var bookmarks : ArrayList<Bookmark> = ArrayList()

    private val cursor = Cursor()

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

    fun drawBookmark(b : Bookmark) {
        // provides x position on playbar for bookmark
        val x = (b.timestamp.toFloat() / duration.toFloat()) * width
        val w : Int = 5

        canvas?.drawRect(x - (w / 2), 0F, x + (w / 2), height.toFloat() / 2, bookmarkPaint)
    }

    var finishedMoving : Boolean = false
    var movedToMillis : Int = 0

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x ?: -1F
        val touchY = event?.y ?: -1F

        when(event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if(cursor.isInBounds(touchX, touchY)) {
                    Log.d(TAG, "Cursor grabbed")
                    cursor.isGrabbed = true
                } else {
                    return false
                }
            }

            MotionEvent.ACTION_MOVE -> {
                Log.d(TAG, "Cursor moved: X: ${event?.rawX}")
                cursor.isGrabbed = true
                cursor.movedX = event?.rawX
            }

            MotionEvent.ACTION_UP -> {
                cursor.isGrabbed = false
                finishedMoving = true
                movedToMillis = millisFromX(event?.rawX)
            }
        }

        invalidate()

        return super.onTouchEvent(event)
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

        cursor.x = width * progress
        cursor.draw(canvas)

    }

    fun millisFromX(x : Float) : Int {
        if(x == 0F) {
            return 0
        }

        return ((x / width) * duration).toInt()
    }

    class Cursor {
        private val TAG: String = "PlaybarDisplay.Cursor"

        var isGrabbed = false
        var movedX = 0F

        var x = -1F; var y = -1F
        var w = 5

        fun draw(canvas: Canvas?) {
            val cursorBackgroundPaint : Paint = Paint()

            val height = canvas?.height ?: -1
            val width = canvas?.width ?: -1

            cursorBackgroundPaint.setARGB(255, 255, 255, 255)


            // switchout x depending on if we're grabbed
            val x : Float = if (isGrabbed) movedX else this.x
            Log.d(TAG, "Drawing, grabbed ($isGrabbed) at $x")

            canvas?.drawCircle(x, height.toFloat() / 2, 20F, cursorBackgroundPaint)
            canvas?.drawRect(x - w, height.toFloat() / 2, x + w, height.toFloat(), cursorBackgroundPaint)
        }

        fun isInBounds(x : Float, y : Float) : Boolean {
            val padding = 5

            val upperBound = this.x + padding
            val lowerBound = this.x - padding

            if(x == -1F || y == -1F) {
                Log.d(TAG, "Touch was null from event; investigate")
                return false
            }

            return (x < upperBound && x > lowerBound)
        }
    }
}

