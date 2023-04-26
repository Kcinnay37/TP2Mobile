package com.example.tpbreakout

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import kotlin.math.abs

class Ball()
{
    private val paintBall = Paint().apply { color = Color.BLACK }

    private lateinit var rectBall: Rect;
    private lateinit var colliderBall: BoxCollider;

    private var speedX: Float = 450f;
    private var speedY: Float = 700f;

    private var dirX: Int = 1;
    private var dirY: Int = 1;

    private var screenWidth = 0;
    private var screenHeight = 0;

    private var offsetMidPaddle = 25f;

    private var offsetSpawn = -150f;

    private var onGround: Boolean = false;

    init
    {
        rectBall = Rect(0f, 0f, 50f, 50f);
        colliderBall = BoxCollider();
    }

    fun onDraw(canvas: Canvas, dt: Float)
    {
        rectBall.pX += speedX * dirX * dt;
        rectBall.pY += speedY * dirY * dt;

        canvas.save();
        canvas.translate(rectBall.pX, rectBall.pY);
        canvas.scale(1f, 1f);
        canvas.drawRect(0f, 0f, rectBall.sX, rectBall.sY, paintBall);
        canvas.restore();

        UpdateCollider();
    }

    private fun UpdateCollider()
    {
        colliderBall.UpdateCollider(rectBall);
    }

    fun onSizeChanged(sW: Int, sH: Int)
    {
        screenWidth = sW;
        screenHeight = sH;

        rectBall.pX = (screenWidth / 2) - (rectBall.sX / 2);
        rectBall.pY = (screenHeight / 2) - (rectBall.sY / 2) + offsetSpawn;
    }

    public fun CheckScreen()
    {
        if(rectBall.pX < 0f)
        {
            rectBall.pX = 0f;
            dirX = 1;
        }
        else if(rectBall.pX > screenWidth - rectBall.sX)
        {
            rectBall.pX = screenWidth - rectBall.sX;
            dirX = -1;
        }

        if(rectBall.pY < 0f)
        {
            rectBall.pY = 0f;
            dirY = 1;
        }
        else if(rectBall.pY > screenHeight - rectBall.sY)
        {
            rectBall.pY = screenHeight - rectBall.sY;
            dirY = -1;
            onGround = true;
        }
    }

    public fun CheckColliderWithPaddle(boxCollider: BoxCollider)
    {
        var collision: Array<Int> = colliderBall.CheckColliderWith(boxCollider, 25f);

        if(collision[0] != 0)
        {
            dirX = -collision[0];
        }
        if(collision[1] != 0)
        {
            dirY = -collision[1];

            var ballPos = rectBall.pX + rectBall.sX / 2;
            var paddlePos = boxCollider.rect.pX + boxCollider.rect.sX / 2;

            if(ballPos <= paddlePos + offsetMidPaddle && ballPos >= paddlePos - offsetMidPaddle) return;

            if(ballPos < paddlePos)
            {
                dirX = -1;
            }
            else if(ballPos > paddlePos)
            {
                dirX = 1;
            }
        }
    }

    public fun CheckColliderWithBrick(listBrick: MutableList<BrickData>)
    {
        var listToDelete = mutableListOf<BrickData>()
        for(dataBrick: BrickData in listBrick)
        {
            var collision: Array<Int> = colliderBall.CheckColliderWith(dataBrick.collider, 30f);

            var delete: Boolean = false;

            if(collision[0] != 0)
            {
                dirX = -collision[0];
                delete = true;
            }
            if(collision[1] != 0)
            {
                dirY = -collision[1];
                delete = true;
            }

            if(delete)
            {
                listToDelete.add(dataBrick);
            }
        }

        for(dataBrick: BrickData in listToDelete)
        {
            listBrick.remove(dataBrick);
        }
    }

    public fun CheckOnGround(): Boolean
    {
        return onGround;
    }
}