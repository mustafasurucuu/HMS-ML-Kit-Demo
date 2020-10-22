package com.msapp.wirelessms.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.huawei.hms.mlsdk.tts.MLTtsEngine
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import com.msapp.wirelessms.R
import com.msapp.wirelessms.Utils.Constants
import com.msapp.wirelessms.interfaces.TTSInterface
import com.msapp.wirelessms.presenters.TTSPresenter
import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.openxml4j.exceptions.OpenXML4JException
import org.apache.poi.xwpf.extractor.XWPFWordExtractor
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.xmlbeans.XmlException
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class TTS : AppCompatActivity(), TTSInterface.TView, View.OnClickListener {

    private lateinit var presenter: TTSInterface.TPresenter
    private lateinit var myText: EditText
    private lateinit var repeat: ImageButton
    private lateinit var ttsBtn: ImageButton
    private lateinit var next: ImageButton
    private lateinit var prev: ImageButton
    private var engine: MLTtsEngine? = null
    private lateinit var clearText: ImageButton
    private var myFileIntent: Intent?= null
    private var extractor: XWPFWordExtractor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_t_t_s)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        presenter = TTSPresenter(this)
        //next = findViewById(R.id.buttonNext)
        //prev = findViewById(R.id.buttonPrevious)
        ttsBtn = findViewById(R.id.buttonPlay)
        //repeat = findViewById(R.id.buttonRepeat)
        clearText = findViewById(R.id.clear_text)
        myText = findViewById(R.id.tts_text)
        myText.setTextIsSelectable(true)
        ttsBtn.setOnClickListener(this as View.OnClickListener)
        clearText.setOnClickListener(this as View.OnClickListener)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE), PackageManager.PERMISSION_GRANTED)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.myDocs -> {
                if (engine != null) {
                    engine!!.stop()
                    engine!!.shutdown()
                    openFile()
                } else {
                    openFile()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10 && resultCode == RESULT_OK) {
            if (data != null) {
                val uri = data.data
                Log.d("PATH", "onActivityResult: " + uri!!.path)
                try {
                    readDocument(uri)
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: OpenXML4JException) {
                    e.printStackTrace()
                } catch (e: XmlException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    @Throws(Exception::class)
    fun readDocument(uri: Uri?) {
        myText.setText("")
        var fullPath: String? = null
        if (uri!!.path!!.contains(Constants.PRIMARY)) {
            fullPath = Constants.LOCAL_STORAGE + uri.path!!.split(Constants.COLON).toTypedArray()[1]
        }
        if (uri.path!!.contains("home") && uri.path!!.contains("document")) {
            fullPath = Constants.EXT_STORAGE + "Documents/" + uri.path!!.split(Constants.COLON).toTypedArray()[1]
        }
        if (fullPath != null && fullPath.contains(".pdf")) {
            Log.v("URI", uri.path + " " + fullPath)
            val stringParser: String
            val pdf = File(fullPath)
            try {
                val pdfReader = PdfReader(pdf.path)
                stringParser = PdfTextExtractor.getTextFromPage(pdfReader, 1).trim { it <= ' ' }
                pdfReader.close()
                myText.setText(stringParser)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            Log.v("URI", uri.path + " " + fullPath)
            val word = File(fullPath)
            if (uri.path!!.split(Constants.COLON).toTypedArray()[1].toLowerCase().endsWith(".docx")) {
                val doc = XWPFDocument(FileInputStream(word))
                extractor = XWPFWordExtractor(doc)
                val extractedText = extractor!!.text
                myText.setText(extractedText)
            } else {
                val document = HWPFDocument(FileInputStream(word))
                val range = document.range
                val len = range.numParagraphs()
                val builder = StringBuilder()
                for (i in 0 until len) {
                    builder.append(range.getParagraph(i).text())
                    myText.setText(builder.toString())
                }
            }
        }
    }

    private fun openFile() {
        myFileIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        myFileIntent!!.type = "*/*"
        startActivityForResult(myFileIntent, 10)
    }

    override fun useEngine(mlTtsEngine: MLTtsEngine?) {
        engine = mlTtsEngine
    }

    override fun ttsButton(): ImageButton {
        return ttsBtn
    }


    override fun sourceText(): String {
        return myText.text.toString()
    }
	
	 override fun listen(str: String) {
        runOnUiThread(Runnable {
            val newStr = str.trim { it <= ' ' }.replace("\n".toRegex(), "")
            val spannableString = SpannableString(myText.text)
            val backgroundSpans = spannableString.getSpans(0, spannableString.length, BackgroundColorSpan::class.java)
            for (span: BackgroundColorSpan? in backgroundSpans) {
                spannableString.removeSpan(span)
            }
            var index = spannableString.toString().indexOf(newStr)
            if (index >= 0) {
                spannableString.setSpan(BackgroundColorSpan(Color.YELLOW), index, index + newStr.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                index = spannableString.toString().indexOf(newStr, index + newStr.length)
            }
            myText.setText(spannableString)
        })
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.clear_text -> {
                myText.setText("")
                clearText.visibility = View.INVISIBLE
            }
            R.id.buttonPlay -> presenter.giveText(myText.text.toString())
        }
    }

}