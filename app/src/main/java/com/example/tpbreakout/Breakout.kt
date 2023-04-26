package com.example.tpbreakout

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity

class Breakout : AppCompatActivity()
{
    private lateinit var relativeLayout: RelativeLayout;
    private lateinit var breakoutView: BreakoutView;

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
        relativeLayout.setBackgroundColor(Color.WHITE);

        breakoutView = BreakoutView(this);
        relativeLayout.addView(breakoutView);

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

        //breakoutView.UpdateCollider();

        if(win) return;

        if(breakoutView.CheckWin())
        {
            win = true;
        }

        if(breakoutView.CheckLose())
        {
            lose = true;
        }

        if(!lose && !win)
        {
            breakoutView.UpdateSpawnBrick(deltaTime);
        }
    }
}