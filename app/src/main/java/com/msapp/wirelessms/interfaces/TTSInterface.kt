package com.msapp.wirelessms.interfaces

import android.widget.ImageButton
import com.huawei.hms.mlsdk.tts.MLTtsEngine

interface TTSInterface {

    interface TPresenter {
        fun detectLanguage()
        fun init()
        fun giveText(txt: String)
    }

    interface TView {
        fun useEngine(mlTtsEngine: MLTtsEngine?)
        fun ttsButton(): ImageButton?
        fun sourceText(): String?
    }
}