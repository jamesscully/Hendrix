package com.scullyapps.hendrix.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class PlaybarDisplay(context : Context) : View(context) {
    private val TAG: String = "PlaybarDisplay";

    private var bitmap : Bitmap? = null
    private var canvas : Canvas? = null

    private var path : Path = Path()
    private var paint : Paint = Paint()


    constructor(context: Context, attr: AttributeSet) : this(context) {

    }

    init {
        paint.style = Paint.Style.STROKE;
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
        canvas?.drawColor(Color.YELLOW)
    }

}