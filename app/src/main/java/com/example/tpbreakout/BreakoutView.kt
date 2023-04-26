package com.example.tpbreakout

import android.content.Context
import android.graphics.Canvas
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

class BreakoutView(context: Context) : View(context), SensorEventListener
{
    private lateinit var paddle: Paddle;
    private lateinit var ball: Ball;
    private lateinit var brick: Brick;

    private var screenWidth = 0;
    private var screenHeight = 0;

    private lateinit var sensorManager: SensorManager;
    private lateinit var accelerometer: Sensor;

    private var deltaTime: Float = 0f;
    private var lastUpdateTime: Long = 0;

    private var screenInit:Boolean = false;
    private var drawBall:Boolean = true;

    init
    {
        paddle = Paddle();
        ball = Ball();
        brick = Brick();

        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)

        screenInit = false;
        drawBall = true;
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean
    {
        if(screenInit)
        {
            paddle.onTouchEvent(event);
        }

        return true;
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        screenHeight = h;

        paddle.onSizeChanged(w, h);
        ball.onSizeChanged(w, h);
        brick.onSizeChanged(w, h);

        screenInit = true;

        lastUpdateTime = System.currentTimeMillis();
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int)
    {
        super.onDetachedFromWindow();
        sensorManager.unregisterListener(this);
    }

    override fun onSensorChanged(event: SensorEvent)
    {
        if(screenInit)
        {
            paddle.onSensorChanged(event);
        }
    }

    override fun onDraw(canvas: Canvas)
    {
        super.onDraw(canvas);

        val currentTime = System.currentTimeMillis();
        //val currentTime = SystemClock.elapsedRealtime();
        deltaTime = (currentTime - lastUpdateTime) / 1000.0f;
        lastUpdateTime = currentTime;

        if(screenInit)
        {
            UpdateCollider();

            paddle.onDraw(canvas, deltaTime);
            if(drawBall)
            {
                ball.onDraw(canvas, deltaTime);
            }
            brick.onDraw(canvas);
        }

        invalidate();
    }

    public fun UpdateCollider()
    {
        ball.CheckScreen();
        ball.CheckColliderWithPaddle(paddle.GetCollider());
        ball.CheckColliderWithBrick(brick.GetBricks());
    }

    public fun CheckWin() : Boolean
    {
        if(brick.GetBricks().isEmpty() && screenInit)
        {
            return true;
        }

        return false;
    }

    public fun CheckLose() : Boolean
    {
        if(ball.CheckOnGround() && screenInit)
        {
            drawBall = false;
            return true;
        }

        return false;
    }

    public fun UpdateSpawnBrick(dt:Float)
    {
        if(screenInit)
        {
            brick.UpdateSpawnBrick(dt);
        }
    }
}