package com.msapp.wirelessms.presenters;

import android.content.Intent;

import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.huawei.hms.mlplugin.asr.MLAsrCaptureActivity;
import com.huawei.hms.mlplugin.asr.MLAsrCaptureConstants;
import com.huawei.hms.mlsdk.asr.MLAsrConstants;
import com.huawei.hms.mlsdk.common.MLApplication;
import com.msapp.wirelessms.interfaces.ASR_Interface;


import java.io.File;


public class ASR_Presenter implements ASR_Interface.asrPresenter {

    ASR_Interface.asrView view;
    Document doc;
    DocumentBuilder builder;
    String sdPath;

    public ASR_Presenter(ASR_Interface.asrView view){
        this.view = view;
    }

    @Override
    public void init() {
        MLApplication.getInstance().setApiKey("CgB6e3x9gkYNv19X0AeMU1gKH0iy/hf3w+MSb6ogkZO0LXvKgrCLZQcToSOP3gUrr8PtGJtWWbGGSRZIq6IIUXKc");
        Intent intent = new Intent(view.getContext(), MLAsrCaptureActivity.class)
                .putExtra(MLAsrCaptureConstants.LANGUAGE, MLAsrConstants.LAN_DE_DE)
                .putExtra(MLAsrCaptureConstants.FEATURE, MLAsrCaptureConstants.FEATURE_ALLINONE);
        view.startMyActivity(intent);
        view.addNotes();
    }



    @Override
    public void docOps() {
        try {
            doc = new Document();
            builder = new DocumentBuilder(doc);
            File myDir = view.getContext().getFilesDir();
            sdPath = myDir + File.separator;
            builder.write(view.getSpeech());
            doc.save(sdPath + "SpeechRecord.docx");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
