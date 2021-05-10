package com.ikalkan.mis49m_catchthekenny

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    var score = 0
    var coordinateList = ArrayList<Pair<Float,Float>>()
    var handler = Handler()
    var runnable = Runnable{}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels.toFloat()
        val width = displayMetrics.widthPixels.toFloat()

        val coordinate1 = Pair(width/9, height/4)
        val coordinate2 = Pair(width/9+width/6, height/4)
        val coordinate3 = Pair(width/9+width/3, height/4)
        val coordinate4 = Pair(width/9, height/8)
        val coordinate5 = Pair(width/9+width/6, height/8)
        val coordinate6 = Pair(width/9+width/3, height/8)
        val coordinate7 = Pair(width/9, height/8+height/2)
        val coordinate8 = Pair(width/9+width/6, height/8+height/2)
        val coordinate9 = Pair(width/9+width/3, height/8+height/2)

        coordinateList.add(coordinate1)
        coordinateList.add(coordinate2)
        coordinateList.add(coordinate3)
        coordinateList.add(coordinate4)
        coordinateList.add(coordinate5)
        coordinateList.add(coordinate6)
        coordinateList.add(coordinate7)
        coordinateList.add(coordinate8)
        coordinateList.add(coordinate9)

        object : CountDownTimer(15000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                timeText.text = "Time: ${millisUntilFinished/1000}"
            }

            override fun onFinish() {
                timeText.text = "Time: 0"

                handler.removeCallbacks(runnable)

                val alert = AlertDialog.Builder(this@MainActivity)
                alert.setTitle("Game Over")
                alert.setMessage("You want to play again?")
                alert.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                    val intent = intent
                    finish()
                    startActivity(intent)
                })

                alert.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                    Toast.makeText(this@MainActivity, "Game Over", Toast.LENGTH_LONG).show()
                })

                alert.show()

            }

        }.start()

        moveImage()

    }

    fun increaseScore(view : View) {
        score += 1
        scoreText.text = "Score: ${score}"
    }

    fun moveImage() {

        runnable = object : Runnable{
            override fun run() {
                val random = Random()
                val randomIndex = random.nextInt(9)

                imageView.x = coordinateList[randomIndex].first
                imageView.y = coordinateList[randomIndex].second

                handler.postDelayed(runnable, 500)
            }

        }

        handler.post(runnable)

    }
}