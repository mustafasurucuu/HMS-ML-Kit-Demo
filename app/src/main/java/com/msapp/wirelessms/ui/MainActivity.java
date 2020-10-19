package com.msapp.wirelessms.ui;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.msapp.wirelessms.R;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button asr;
    Button tts;
    Button translate;
    Button langdet;
    Button aft;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        asr = findViewById(R.id.asr);
        tts = findViewById(R.id.tts);
        translate = findViewById(R.id.translate);
        langdet = findViewById(R.id.langdetect);
        aft = findViewById(R.id.aft);

        asr.setOnClickListener(this);
        tts.setOnClickListener(this);
        translate.setOnClickListener(this);
        langdet.setOnClickListener(this);
        aft.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.asr:
                intent = new Intent(MainActivity.this, ASR.class);
                startActivity(intent);
                break;
            case R.id.aft:
                intent = new Intent(MainActivity.this, AFT.class);
                startActivity(intent);
                break;
            case R.id.tts:
                intent = new Intent(MainActivity.this, TTSkt.class);
                startActivity(intent);
                break;
            case R.id.translate:
                intent = new Intent(MainActivity.this, Translation.class);
                startActivity(intent);
                break;
            case R.id.langdetect:
                intent = new Intent(MainActivity.this, LanguageDetection.class);
                startActivity(intent);
        }
    }
}

