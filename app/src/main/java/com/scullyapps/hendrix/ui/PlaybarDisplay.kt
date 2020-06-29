package com.scullyapps.hendrix.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.math.MathUtils

class PlaybarDisplay(context : Context, attr: AttributeSet) : View(context, attr) {
    private val TAG: String = "PlaybarDisplay";

    private var bitmap : Bitmap? = null
    private var canvas : Canvas? = null

    private var path : Path = Path()
    private var paint : Paint = Paint()

    private var playedPaint : Paint = Paint()

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
        paint.style = Paint.Style.FILL;
        paint.strokeJoin = Paint.Join.ROUND;
        paint.strokeWidth = 20.0f;
        paint.strokeCap = Paint.Cap.ROUND;
        paint.setARGB(255,0,0,0);
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        bitmap = Bitmap.createBitmap(Math.max(w,h), Math.max(w,h), Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)
    }

    override fun onDraw(canvas: Canvas?) {
        Log.d(TAG, "Drawing playbar:\nWidth/Height: $width,$height\nTime/Duration: ($time,$duration)\nProgress: $progress, ${width.toFloat() * progress}\n")
        canvas?.drawColor(Color.YELLOW)

        // set width to how far we've progressed in song
        canvas?.drawRect(0F, (height.toFloat() / 2), width.toFloat() * progress, height.toFloat(), paint)
    }

}