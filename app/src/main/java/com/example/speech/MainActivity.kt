package com.example.speech

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener, TextToSpeech.OnInitListener {

    lateinit var bRecord: Button
    lateinit var bSpeak: Button

    lateinit var textToBeSpoken: EditText
    lateinit var parsedSpeech: TextView

    lateinit var ttsObject: TextToSpeech
    var ttsResult = 0

    private val REQUEST_CODE_SPEECH_INPUT = 5000
    private val TAG = MainActivity::class.qualifiedName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bRecord = findViewById(R.id.bRecord)
        bSpeak = findViewById(R.id.bSpeak)

        textToBeSpoken = findViewById(R.id.textToBeSpoken)
        parsedSpeech = findViewById(R.id.parsedSpeech)

        bRecord.setOnClickListener(this)
        bSpeak.setOnClickListener(this)
        ttsObject = TextToSpeech(this, this)
    }

    override fun onClick(v: View?) {
        when (v?.getId()) {
            R.id.bRecord -> {
                speak()
            }
            R.id.bSpeak -> {
                readText()
            }
        }
    }

    private fun readText() {
        if (ttsResult === TextToSpeech.LANG_NOT_SUPPORTED || ttsResult === TextToSpeech.LANG_MISSING_DATA) {
            Toast.makeText(this, "Text to Speech not supported", Toast.LENGTH_SHORT).show()
        } else {
            var text = textToBeSpoken.getText().toString()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ttsObject.speak(text,TextToSpeech.QUEUE_FLUSH,null,null)
            } else {
                ttsObject.speak(text, TextToSpeech.QUEUE_FLUSH, null)
            }
        }
    }

    private fun speak() {
        val mIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        mIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        mIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        mIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi Speak Something")

        try {
            startActivityForResult(mIntent, REQUEST_CODE_SPEECH_INPUT)
        }
        catch (e: Exception)
        {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode === Activity.RESULT_OK && null != data) {
            when(requestCode) {
                REQUEST_CODE_SPEECH_INPUT -> {
                    val result = data.getStringArrayExtra(RecognizerIntent.EXTRA_RESULTS)
                    parsedSpeech.text = result[0]
                }
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            ttsResult = ttsObject.setLanguage(Locale.UK)
        } else {
            Toast.makeText(this, "Text to Speech not supported", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (ttsObject != null) {
            ttsObject.stop()
            ttsObject.shutdown()
        }
    }
}
