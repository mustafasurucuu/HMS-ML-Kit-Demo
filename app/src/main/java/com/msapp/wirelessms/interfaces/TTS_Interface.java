package com.msapp.wirelessms.interfaces;

        import android.widget.ImageButton;

        import com.huawei.hms.mlsdk.tts.MLTtsEngine;

public interface TTS_Interface {

    interface ttsPresenter {
        void detectLanguage();
        void init();
        void giveText(String txt);
    }

    interface ttsView{
        void useEngine(MLTtsEngine mlTtsEngine);
        ImageButton ttsButton();
        ImageButton nextButton();
        ImageButton prevButton();
        ImageButton repeatButton();
        String sourceText();
    }
}
