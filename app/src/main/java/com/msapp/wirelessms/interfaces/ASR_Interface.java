package com.msapp.wirelessms.interfaces;

import android.content.Context;
import android.content.Intent;

public interface ASR_Interface {


    interface asrPresenter{
        void init();
        void docOps();
    }

    interface asrView{
        void startMyActivity(Intent intent);
        String getSpeech();
        void addNotes();
        Context getContext();
    }

}
