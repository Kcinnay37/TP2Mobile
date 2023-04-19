package com.example.tpbreakout

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity

class Breakout : AppCompatActivity()
{
    private lateinit var relativeLayout: RelativeLayout;
    private lateinit var paddle: Paddle;
    private lateinit var ball: Ball;
    private lateinit var brick: Brick;

    private val UPDATE_INTERVAL = 16L;
    private val handler = Handler(Looper.getMainLooper());

    private var win: Boolean = false;
    private var lose: Boolean = false;

    private var deltaTime: Float = 0f;
    private var lastUpdateTime: Long = 0;

    private val updateRunnable = object : Runnable
    {
        override fun run()
        {
            Update();
            handler.postDelayed(this, UPDATE_INTERVAL);
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState);

        relativeLayout = RelativeLayout(this);

        paddle = Paddle(this);
        ball = Ball(this);
        brick = Brick(this);

        relativeLayout.addView(paddle);
        relativeLayout.addView(ball);
        relativeLayout.addView(brick);

        setContentView(relativeLayout);

        handler.post(updateRunnable);
    }

    override fun onDestroy()
    {
        super.onDestroy();
        handler.removeCallbacks(updateRunnable);
    }

    private fun Update()
    {
        if(lose) return;

        val currentTime = System.currentTimeMillis()
        deltaTime = (currentTime - lastUpdateTime) / 1000.0f
        lastUpdateTime = currentTime

        ball.CheckScreen();
        ball.CheckColliderWithPaddle(paddle.GetCollider());
        ball.CheckColliderWithBrick(brick.GetBricks());

        if(win) return;

        if(brick.GetBricks().count() == 0)
        {
            win = true;
        }

        if(ball.CheckOnGround())
        {
            lose = true;
            relativeLayout.removeView(ball);
        }

        if(!lose && !win)
        {
            brick.UpdateSpawnBrick(deltaTime);
        }
    }
}