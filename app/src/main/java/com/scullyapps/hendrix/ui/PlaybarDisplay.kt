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

    private var playedPaint : Paint = Paint()
    private var bookmarkPaint: Paint = Paint()
    private var backgroundPaint: Paint = Paint()

    private var bookmarks : ArrayList<Bookmark> = ArrayList()

    val cursor = Cursor()

    // current time on song
    var time : Int = 0
    // duration of song
    var duration : Int = 0

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

    fun drawBookmark(b : Bookmark, canvas: Canvas?) {
        // provides x position on playbar for bookmark
        val x = (b.timestamp.toFloat() / duration.toFloat()) * width
        val w : Int = 5

        canvas?.drawRect(x - (w / 2), 0F, x + (w / 2), height.toFloat() / 2, bookmarkPaint)
    }

    var finishedMoving : Boolean = false
    var movedToMillis : Int = 0

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if(event == null)
            return false;

        // if not in our playbar bounds, ignore
//        val inBounds = (event.rawY > y && event.rawY < (y + measuredHeight))
//
//        if(!inBounds) {
//            Log.d(TAG, "Ignoring touch ($touchX,$touchY) raw (${event.rawX}, ${event.rawY}), bar y: $y height: $measuredHeight")
//            return false
//        }

        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d(TAG, "Cursor grabbed")
                cursor.isGrabbed = true
            }

            MotionEvent.ACTION_MOVE -> {
                if(cursor.isGrabbed) {
                    Log.d(TAG, "Cursor moved")
                    cursor.movedX = event.rawX
                    invalidate()
                }
            }

            MotionEvent.ACTION_UP -> {
                if(cursor.isGrabbed ) {
                    Log.d(TAG, "Cursor released")
                    cursor.isGrabbed = false
                    finishedMoving = true
                    cursor.x = MathUtils.clamp(event.rawX, 0F, width.toFloat())
                }
            }
        }

        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
//        bitmap = Bitmap.createBitmap(Math.max(w,h), Math.max(w,h), Bitmap.Config.ARGB_8888)
//        canvas = Canvas(bitmap)
    }

    fun drawProgressBar(canvas: Canvas?) {
        // set width to how far we've progressed in song
        val rect = Rect()

        rect.left = 0
        rect.top = height / 2
        rect.bottom = height;

        if(cursor.isGrabbed) {
            rect.right = cursor.movedX.toInt()
        } else {
            rect.right = (width * progress).toInt()
        }

        canvas?.drawRect(rect, playedPaint)
    }


    override fun onDraw(canvas: Canvas?) {

        if(finishedMoving) {
            return
        }

        // draw background
        canvas?.drawRect(0F, 0F, width.toFloat(), height.toFloat(), backgroundPaint)

        drawProgressBar(canvas)

        for(b in bookmarks) {
            drawBookmark(b, canvas)
        }

        cursor.x = width * progress
        cursor.draw(canvas)
    }

    fun millisFromCursor() : Int {
        if(cursor.x == 0F) {
            return 0
        }
        return ((cursor.x / width) * duration).toInt()
    }


    class Cursor {
        private val TAG: String = "PlaybarDisplay.Cursor"

        // movedX is temporary to store where the pointer is on the X scale
        var isGrabbed = false
        var movedX = 0F

        // default position
        var x = -1F; var y = -1F

        // width of the cursor grab
        var w = 5

        fun draw(canvas: Canvas?) {
            val cursorBackgroundPaint : Paint = Paint()

            val height = canvas?.height ?: -1
            val width = canvas?.width ?: -1

            cursorBackgroundPaint.setARGB(255, 255, 255, 255)

            // switchout x depending on if we're grabbed
            val x : Float = if (isGrabbed) movedX else this.x

            canvas?.drawCircle(x, height.toFloat() / 2, 20F, cursorBackgroundPaint)
            canvas?.drawRect(x - w, height.toFloat() / 2, x + w, height.toFloat(), cursorBackgroundPaint)
        }

        fun isInBounds(x : Float, y : Float) : Boolean {
            // allow for clicks within certain distance to activate dragging
            val padding = 15

            val upperBound = this.x + padding
            val lowerBound = this.x - padding

            // -1 is sent if an error occurs previous to here
            if(x == -1F || y == -1F) {
                Log.d(TAG, "Touch was null from event; investigate")
                return false
            }

            return (x < upperBound && x > lowerBound)
        }
    }
}

