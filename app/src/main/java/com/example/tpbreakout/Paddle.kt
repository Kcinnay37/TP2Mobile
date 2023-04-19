package com.example.tpbreakout

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import kotlin.math.abs
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager



class Paddle(context:Context) : View(context), SensorEventListener
{
    private val paintPaddle = Paint().apply { color = Color.BLUE }
    private var paintButtonLeft = Paint().apply { color = Color.argb(128, 255, 0, 0) }
    private var paintButtonRight = Paint().apply { color = Color.argb(128, 255, 0, 0)  }
    val paintText = Paint().apply {
        color = Color.BLACK
        textSize = 100f
        textAlign = Paint.Align.CENTER
    }

    private var moveSpeed = 10f;
    private var moveDir: Int = 0;

    private var offsetSpawn = 150f;
    private var offsetButton = 5f;

    private var touchePos = 0f;

    private lateinit var rectPaddle: Rect;
    private lateinit var colliderPaddle: BoxCollider;

    private lateinit var rectButtonLeft: Rect;
    private lateinit var colliderButtonLeft: BoxCollider;
    private var textButtonLeft = "<-";

    private lateinit var rectButtonRight: Rect;
    private lateinit var colliderButtonRight: BoxCollider;
    private var textButtonRight = "->";

    private var screenWidth = 0;
    private var screenHeight = 0;

    private lateinit var sensorManager: SensorManager;
    private lateinit var accelerometer: Sensor;

    private var moveWithInput: Boolean = false;

    init
    {
        rectPaddle = Rect(0f, 0f, 200f, 50f);
        colliderPaddle = BoxCollider();

        rectButtonLeft = Rect(0f, 0f, 100f, 100f);
        colliderButtonLeft = BoxCollider();

        rectButtonRight = Rect(0f, 0f, 100f, 100f);
        colliderButtonRight = BoxCollider();

        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
    }

    //comment je peux faire que cela s'appel chaque frame que lecran est appuyer?
    override fun onTouchEvent(event: MotionEvent?): Boolean
    {
        if (event != null)
        {
            when (event.action)
            {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE ->
                {
                    if(!CheckClickButton(event.x, event.y))
                    {
                        var paddlePos = rectPaddle.pX + rectPaddle.sX / 2;

                        touchePos = event.x;

                        if(abs(touchePos - paddlePos) > moveSpeed)
                        {
                            if(touchePos - paddlePos < 0)
                            {
                                moveDir = -1;
                            }
                            else
                            {
                                moveDir = 1;
                            }
                        }
                        else
                        {
                            moveDir = 0;
                        }
                    }
                    moveWithInput = true;
                }
                MotionEvent.ACTION_UP ->
                {
                    moveDir = 0
                    ResetButtonView();
                    moveWithInput = false;
                }
            }
        }

        return true;
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        screenHeight = h;

        rectPaddle.pY = screenHeight - rectPaddle.sY - offsetSpawn;
        rectPaddle.pX = (screenWidth / 2) - (rectPaddle.sX / 2);

        rectButtonLeft.pY = screenHeight - rectButtonLeft.sY;
        rectButtonLeft.pX = 0f;
        rectButtonLeft.sX = (screenWidth / 2f) - offsetButton;

        rectButtonRight.pY = screenHeight - rectButtonRight.sY;
        rectButtonRight.pX = (screenWidth / 2f) + offsetButton;
        rectButtonRight.sX = (screenWidth / 2f) - offsetButton;
    }

    override fun onDraw(canvas: Canvas)
    {
        super.onDraw(canvas)

        rectPaddle.pX += moveSpeed * moveDir;

        if(rectPaddle.pX < 0f)
        {
            rectPaddle.pX = 0f;
        }
        if(rectPaddle.pX + rectPaddle.sX > screenWidth)
        {
            rectPaddle.pX = screenWidth - rectPaddle.sX;
        }

        var paddlePos = rectPaddle.pX + rectPaddle.sX / 2;
        if(abs(touchePos - paddlePos) <= moveSpeed)
        {
            moveDir = 0;
        }

        canvas.save();
        canvas.translate(rectPaddle.pX, rectPaddle.pY);
        canvas.scale(1f, 1f);
        canvas.drawRect(0f, 0f, rectPaddle.sX, rectPaddle.sY, paintPaddle);
        canvas.restore();

        canvas.save();
        canvas.translate(rectButtonLeft.pX, rectButtonLeft.pY);
        canvas.scale(1f, 1f);
        canvas.drawRect(0f, 0f, rectButtonLeft.sX, rectButtonLeft.sY, paintButtonLeft);
        canvas.restore();

        canvas.save();
        canvas.translate(rectButtonRight.pX, rectButtonRight.pY);
        canvas.scale(1f, 1f);
        canvas.drawRect(0f, 0f, rectButtonRight.sX, rectButtonRight.sY, paintButtonRight);
        canvas.restore();

        val leftTextX = rectButtonLeft.pX + rectButtonLeft.sX / 2
        val leftTextY = rectButtonLeft.pY + (rectButtonLeft.sY / 2) - (paintText.descent() + paintText.ascent()) / 2
        canvas.drawText(textButtonLeft, leftTextX, leftTextY, paintText)

        val rightTextX = rectButtonRight.pX + rectButtonRight.sX / 2
        val rightTextY = rectButtonRight.pY + (rectButtonRight.sY / 2) - (paintText.descent() + paintText.ascent()) / 2
        canvas.drawText(textButtonRight, rightTextX, rightTextY, paintText)


        UpdateCollider();
        invalidate();
    }

    private fun UpdateCollider()
    {
        colliderPaddle.UpdateCollider(rectPaddle);
        colliderButtonLeft.UpdateCollider(rectButtonLeft);
        colliderButtonRight.UpdateCollider(rectButtonRight);
    }

    private fun CheckClickButton(clickPosX: Float, clickPosY: Float) : Boolean
    {
        ResetButtonView();

        if(colliderButtonLeft.CheckColliderWith(clickPosX, clickPosY))
        {
            paintButtonLeft = Paint().apply { color = Color.argb(255, 255, 0, 0) }
            moveDir = -1;
            touchePos = 0f;
            return true;
        }
        if(colliderButtonRight.CheckColliderWith(clickPosX, clickPosY))
        {
            paintButtonRight = Paint().apply { color = Color.argb(255, 255, 0, 0)  }
            moveDir = 1;
            touchePos = screenWidth.toFloat();
            return true;
        }
        return false;
    }

    private fun ResetButtonView()
    {
        paintButtonLeft = Paint().apply { color = Color.argb(128, 255, 0, 0) }
        paintButtonRight = Paint().apply { color = Color.argb(128, 255, 0, 0)  }
    }


    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER)
        {
            if(moveWithInput) return;

            if(event.values[0] > 2 || event.values[0] < -2)
            {
                if(event.values[0] > 2 )
                {
                    moveDir = -1;
                }
                if(event.values[0] < -2)
                {
                    moveDir = 1;
                }

            }
            else
            {
                moveDir = 0;
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int)
    {
        super.onDetachedFromWindow();
        sensorManager.unregisterListener(this);
    }

    public fun GetCollider() : BoxCollider
    {
        return colliderPaddle;
    }
}