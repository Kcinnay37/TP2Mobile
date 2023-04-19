package com.example.tpbreakout

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class Menu : AppCompatActivity(), OnClickListener
{
    private lateinit var linearLayout: LinearLayout;
    private lateinit var playButton: Button;
    private lateinit var quitButton: Button;

    private var idButtonPlay: Int = -1;
    private var idButtonQuit: Int = -1;

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState);

        linearLayout = LinearLayout(this);
        linearLayout.orientation = LinearLayout.VERTICAL;

        playButton = Button(this);
        playButton.text = "Play";
        playButton.setOnClickListener(this);
        playButton.id = View.generateViewId();
        idButtonPlay = playButton.id;

        quitButton = Button(this);
        quitButton.text = "Quit";
        quitButton.setOnClickListener(this);
        idButtonQuit = quitButton.id;

        linearLayout.addView(playButton);
        linearLayout.addView(quitButton);

        setContentView(linearLayout);
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onClick(p0: View?)
    {
        when(p0?.id)
        {
            idButtonPlay ->
            {
                val intent = Intent(this, Breakout::class.java);
                startActivity(intent);
            }
            idButtonQuit ->
            {
                finish();
            }
        }
    }
}