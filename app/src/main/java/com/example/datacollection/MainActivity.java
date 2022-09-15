package com.example.datacollection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.startButton);

        // blinking animation
        Animation anim = new AlphaAnimation(0.0f, 1.0f);

        anim.setDuration(1000);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
//        startButton.startAnimation(anim);

        startButton.setOnClickListener((View v) -> {
                Intent intent = new Intent(MainActivity.this, QuestionActivity.class);
                startActivity(intent);
        });
    }
}