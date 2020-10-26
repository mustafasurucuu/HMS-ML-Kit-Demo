package com.msapp.wirelessms.presenters

import android.os.Bundle
import android.util.Log
import android.util.Pair
import com.huawei.hms.mlsdk.common.MLApplication
import com.huawei.hms.mlsdk.langdetect.MLLangDetectorFactory
import com.huawei.hms.mlsdk.langdetect.local.MLLocalLangDetectorSetting
import com.huawei.hms.mlsdk.tts.*
import com.msapp.wirelessms.R
import com.msapp.wirelessms.Utils.Constants
import com.msapp.wirelessms.Utils.Timbres
import com.msapp.wirelessms.interfaces.TTSInterface
import com.msapp.wirelessms.ui.TTS
import java.text.BreakIterator
import java.util.*
import kotlin.collections.ArrayList

class TTSPresenter (var view: TTS) : TTSInterface.TPresenter {

    private lateinit var mlTtsEngine: MLTtsEngine
    private lateinit var mlConfigs: MLTtsConfig
    private val sentences = ArrayList<String>()
    val tag = "Kant"
    private var i = 0
    private var k = 0
    private var j = 0
    val mapping:HashMap<String,String> = HashMap<String,String>()

    private var callback: MLTtsCallback?= object : MLTtsCallback {
        override fun onError(taskId: String, err: MLTtsError) {
            Log.d(tag, "onError: $err")
        }

        override fun onWarn(taskId: String, warn: MLTtsWarn) {
            Log.d(tag, "onWarn: $warn")
        }

        override fun onRangeStart(taskId: String, start: Int, end: Int) {
            mapping[taskId] = sentences[k]
            mapping[taskId]?.let {
                view.listen(it) }
            k++
            Log.d("range", "onRangeStart: " + taskId + "," + start + "," + end + mapping[taskId])
        }

        override fun onAudioAvailable(taskId: String, audioFragment: MLTtsAudioFragment, offset: Int, range: Pair<Int, Int>,
                                      bundle: Bundle) {
        }

        override fun onEvent(taskId: String, eventId: Int, bundle: Bundle?) {
            when (eventId) {
                MLTtsConstants.EVENT_PLAY_START -> {
                    view.ttsButton().setOnClickListener {
                        mlTtsEngine.pause()
                        view.ttsButton().setImageResource(R.drawable.ic_pause)}

                }
                MLTtsConstants.EVENT_PLAY_STOP -> {
                    view.ttsButton().setImageResource(R.drawable.ic_play)
                    view.ttsButton().setOnClickListener {
                       giveText(view.sourceText())
                         }
                }
                MLTtsConstants.EVENT_PLAY_RESUME -> {
                    view.ttsButton().setOnClickListener {
                        mlTtsEngine.pause()
                        view.ttsButton().setImageResource(R.drawable.ic_pause)}

                }
                MLTtsConstants.EVENT_PLAY_PAUSE -> {
                    view.ttsButton().setOnClickListener {
                        mlTtsEngine.resume()
                        view.ttsButton().setImageResource(R.drawable.ic_play)}

                }
                MLTtsConstants.EVENT_SYNTHESIS_START -> {

                }
                MLTtsConstants.EVENT_SYNTHESIS_END -> {
                }
                MLTtsConstants.EVENT_SYNTHESIS_COMPLETE -> {
                }

            }
        }
    }

    override fun init() {
        i = 0
        MLApplication.getInstance().apiKey = Constants.API_KEY
        mlTtsEngine = MLTtsEngine(mlConfigs)
        mlTtsEngine.setTtsCallback(callback)
        view.useEngine(mlTtsEngine)

        while (i < sentences.size) {
            mlTtsEngine.speak(sentences[i], MLTtsEngine.QUEUE_APPEND)
            Log.d("kontrol", "init: " + sentences[i].toString())
            i++
        }
    }

    override fun giveText(txt: String) {
        k = 0
        mapping.clear()
        sentences.clear()
        val iterator = BreakIterator.getSentenceInstance(Locale.US)
        iterator.setText(txt)
        var start = iterator.first()
        var end = iterator.next()
        while (end != BreakIterator.DONE) {
                sentences.add(txt.substring(start, end))
                start = end
                end = iterator.next()
            }
        Log.d("tag", "giveText: " + sentences.toString())
			 while (j < sentences.size) {
            if (sentences[j] == "\n") {
                sentences.remove(sentences[j])
            }
                 j++
        }
        Log.d("tag", "giveText: " + sentences.toString())
        detectLanguage()
    }

    override fun detectLanguage() {
        val factory = MLLangDetectorFactory.getInstance()
        val setting = MLLocalLangDetectorSetting.Factory()
                .setTrustedThreshold(0.01f)
                .create()
        val mlLocalLangDetector = factory.getLocalLangDetector(setting)
        val firstBestDetectTask = mlLocalLangDetector.firstBestDetect(view.sourceText())
        firstBestDetectTask.addOnSuccessListener { language ->
            Log.d("LanguageDetected", "onSuccess: $language")
            mlConfigs = MLTtsConfig()
                    .setSpeed(view.setSpeed())
                    .setVolume(view.setVolume())
            when (language) {
                "en" -> view.selectSpeaker(language)
                "zh" -> view.selectSpeaker(language)
                "de" -> view.selectSpeaker(language)
                "es" -> view.selectSpeaker(language)
                "it" -> view.selectSpeaker(language)
                "fr" -> view.selectSpeaker(language)
            }

        }.addOnFailureListener { e -> Log.d("LanguageFail", "onFailure: $e") }
    }

    override fun setConfigs(lng: String, gender: String ) {
        if (lng == "en") {
            if(gender == "male"){
            mlConfigs.setLanguage(Timbres.TTS_EN_US).person = Timbres.TTS_SPEAKER_MALE_EN
            } else if (gender == "female") {
                mlConfigs.setLanguage(Timbres.TTS_EN_US).person = Timbres.TTS_SPEAKER_FEMALE_EN
            }
        } else if (lng == "zh"){
            if(gender == "male"){
                mlConfigs.setLanguage(Timbres.TTS_ZH).person = Timbres.TTS_SPEAKER_MALE_ZH
            } else if (gender == "female") {
                mlConfigs.setLanguage(Timbres.TTS_ZH).person = Timbres.TTS_SPEAKER_FEMALE_ZH
            }
        }
        when (lng) {
            "de" -> mlConfigs.setLanguage(Timbres.TTS_DE).person = Timbres.TTS_SPEAKER_FEMALE_DE
            "es" -> mlConfigs.setLanguage(Timbres.TTS_ES).person = Timbres.TTS_SPEAKER_FEMALE_ES
            "it" -> mlConfigs.setLanguage(Timbres.TTS_IT).person = Timbres.TTS_SPEAKER_FEMALE_IT
            "fr" -> mlConfigs.setLanguage(Timbres.TTS_FR).person = Timbres.TTS_SPEAKER_FEMALE_FR
        }
        init()
    }
}