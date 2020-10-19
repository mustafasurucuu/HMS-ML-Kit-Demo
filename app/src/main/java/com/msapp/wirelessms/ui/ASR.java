package com.msapp.wirelessms.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.huawei.hms.mlplugin.asr.MLAsrCaptureConstants;
import com.msapp.wirelessms.R;
import com.msapp.wirelessms.interfaces.ASR_Interface;
import com.msapp.wirelessms.presenters.ASR_Presenter;

public class ASR extends AppCompatActivity implements ASR_Interface.asrView, View.OnClickListener {

    ASR_Interface.asrPresenter presenter;
    String text;
    ImageButton mic;
    CardView card;
    TextView myText;
    ImageButton word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_s_r_);

        mic = findViewById(R.id.mic);
        word = findViewById(R.id.makeitWord);
        card = findViewById(R.id.card_widget);
        //card.setVisibility(View.INVISIBLE);
        myText = findViewById(R.id.speechtext);
        presenter = new ASR_Presenter(this);
        mic.setOnClickListener((View.OnClickListener) this);
        word.setOnClickListener((View.OnClickListener) this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mic:
                presenter.init();
                break;
            case R.id.makeitWord:
                presenter.docOps();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            switch (resultCode) {
                case MLAsrCaptureConstants.ASR_SUCCESS:
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        if (bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_RESULT)) {
                            text = bundle.getString(MLAsrCaptureConstants.ASR_RESULT);
                        }
                    }
                    break;
                case MLAsrCaptureConstants.ASR_FAILURE:
                    if(data != null) {
                        Bundle bundle = data.getExtras();
                        if(bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_ERROR_CODE)) {
                            int errorCode = bundle.getInt(MLAsrCaptureConstants.ASR_ERROR_CODE);
                        }
                        if(bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_ERROR_MESSAGE)){
                            String errorMsg = bundle.getString(MLAsrCaptureConstants.ASR_ERROR_MESSAGE);
                        }
                        if(bundle != null && bundle.containsKey(MLAsrCaptureConstants.ASR_SUB_ERROR_CODE)) {
                            int subErrorCode = bundle.getInt(MLAsrCaptureConstants.ASR_SUB_ERROR_CODE);
                        }
                    }
                default:
                    break;
            }
        }
    }

    @Override
    public void startMyActivity(Intent intent) {
        startActivityForResult(intent, 100);
    }

    @Override
    public String getSpeech() {
       return myText.toString();
    }

    @Override
    public void addNotes() {
        if(!myText.toString().equals("")) {
            myText.append(text);
            myText.append("\n");
        } else {
            myText.setText(text);
        }
        card.setVisibility(View.VISIBLE);
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

}