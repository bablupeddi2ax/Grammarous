package com.example.grammarous.alphabets

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.grammarous.R
import com.example.grammarous.ShakeDetector
import com.example.grammarous.words.WordList
import kotlin.random.Random


class ShakeAlphabets : AppCompatActivity(), ShakeDetector.OnShakeListener{
    private var mSensorManager: SensorManager? = null
    private var mShakeDetector: ShakeDetector? = null
    private var  alphabets  = arrayListOf<Char>()
    private  var randLetter:String = ""
    private lateinit var txtAlphabet:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shake_alphabets)
        txtAlphabet = findViewById(R.id.txtAlphabet)
        // Set up the shake detector
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mShakeDetector = ShakeDetector()
        mShakeDetector!!.setOnShakeListener(this)
        for(i in 1..26){
            alphabets.add((i+64).toChar())
        }
        txtAlphabet.setOnClickListener{
            if(randLetter.isNotEmpty() && randLetter.isNotBlank()) {
                val intent = Intent(this@ShakeAlphabets, WordList::class.java)
                intent.putExtra("alphabet", randLetter)
                startActivity(intent)
            }
        }

    }

    override fun onShake(count: Int) {
        val rand = Random
         randLetter = alphabets[rand.nextInt(alphabets.size)].toString()
        txtAlphabet.text = randLetter.toString()
    }


    override fun onPause() {
        super.onPause()
        mSensorManager?.unregisterListener(mShakeDetector)
    }

    override fun onResume() {
        super.onResume()
        mSensorManager?.registerListener(mShakeDetector,mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_UI)
    }

    override fun onDestroy() {
        super.onDestroy()
        mSensorManager?.unregisterListener(mShakeDetector)
    }

}