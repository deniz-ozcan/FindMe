package com.denizozcan.findme

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import com.denizozcan.findme.databinding.GameBinding
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.*


class Game : AppCompatActivity() {
    private lateinit var binding: GameBinding
    private lateinit var cUT : CountDownTimer
    private val drawables = arrayOf(R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.cc, R.drawable.d, R.drawable.e, R.drawable.f, R.drawable.g, R.drawable.gg, R.drawable.h, R.drawable.i, R.drawable.ii, R.drawable.j, R.drawable.k, R.drawable.l, R.drawable.m, R.drawable.n, R.drawable.o, R.drawable.oo, R.drawable.p, R.drawable.r, R.drawable.s, R.drawable.ss, R.drawable.t, R.drawable.u, R.drawable.uu, R.drawable.v, R.drawable.y, R.drawable.z)
    private val epsilon = arrayOf("A", "B", "C", "Ç", "D", "E", "F", "G", "Ğ", "H", "I", "İ", "J", "K", "L", "M", "N", "O", "Ö", "P", "R", "S", "Ş", "T", "U", "Ü", "V", "Y", "Z")
    private var genMat = Array(10){Array(8){0}}
    private val scoreMap = HashMap<String, Int>()
    private var turkishWords = ArrayList<String>()
    private var letMatris = ArrayList<Letter>()
    private var rmLetters = ArrayList<Letter>()
    var handler = Handler(Looper.getMainLooper())
    var runnable = Runnable {}
    private var lives = 3
    private var score = 0
    private var interval = 5
    val r = Random()
    @Deprecated("Deprecated in Java")
    override fun onBackPressed(){}
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = GameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        for (word in InputStreamReader(assets.open("turkish_words.txt", Context.MODE_PRIVATE), StandardCharsets.UTF_8).readLines()){turkishWords.add(word)}
        binding.removeBut.setOnClickListener{swipeLeft()}
        binding.correctBut.setOnClickListener{swipeRight()}
        for (i in epsilon.indices){scoreMap[epsilon[i]] = arrayOf(1, 3, 4, 4, 3, 1, 7, 5, 8, 5, 2, 1, 10, 1, 1, 2, 2, 7, 1, 5, 1, 2, 4, 1, 2, 3, 7, 3, 4)[i]}
        object : CountDownTimer(4000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val animation = AnimationUtils.loadAnimation(this@Game, R.anim.timer)
                binding.scoreText.startAnimation(animation)
                handler.removeCallbacks(runnable)
                animation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {
                        binding.scoreText.background = ColorDrawable(Color.parseColor(arrayOf("#F50057", "#FCAE72", "#99ABB5", "#596B89")[((millisUntilFinished)/1000).toInt()]))
                        if(((millisUntilFinished)/1000).toInt()>0) binding.scoreText.text =(((millisUntilFinished) / 1000).toInt()).toString()
                        if(((millisUntilFinished)/1000).toInt()==0) binding.scoreText.text = getString(R.string.yaz)
                    }
                    override fun onAnimationEnd(animation: Animation?) {}
                    override fun onAnimationRepeat(animation: Animation?) {}
                })
            }
            override fun onFinish() {
                binding.scoreText.background = ColorDrawable(Color.parseColor("#212121"))
                binding.scoreText.text = "0"
                binding.scoreText.setTextColor(ColorStateList.valueOf(Color.parseColor("#7e7e7e")))
                for (i in 0 until 24){ createRandomLetter(i/8, i%8,true) }
                cUT.start()
            }
        }.start()
        binding.mistake1.isClickable = false
        binding.mistake2.isClickable = false
        cUT = object : CountDownTimer(86400000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (score >= 100) {interval = 4}
                if (score >= 200) {interval = 3}
                if (score >= 300) {interval = 2}
                if (score >= 400) {interval = 1}
                if ((millisUntilFinished/1000)%interval==0L){
                    handler.postDelayed(runnable, 0)
                    createRandomLetter(0, r.nextInt(8))
                }
                handler.removeCallbacks(runnable)
            }
            override fun onFinish() {}
        }
        binding.gridLayout.setOnTouchListener(object : OnTouchListener {
            var startX = 0f
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                return when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startX = event.x
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        val endX = event.x
                        if (startX < endX) {swipeRight()}
                        else if (startX > endX) {swipeLeft()}
                        true
                    }
                    else -> false
                }
            }
        })
    }

    private fun createRandomLetter(cr: Int, cc: Int, initialState:Boolean = false){
        val tB = ToggleButton(binding.gridLayout.context)
        tB.setTextColor(Color.parseColor("#ffffff"))
        tB.isChecked = false
        tB.textSize = 21f
        val prob = doubleArrayOf(0.1158,0.0283,0.0112,0.0135,0.0462,0.0941,0.0053,0.0150,0.0091,0.0133,0.0386,0.0863,0.0016,0.0470,0.0608,0.0344,0.0680,0.0361,0.0073,0.0075,0.0681,0.0327,0.0156,0.0330,0.0344,0.0179,0.0173,0.0273,0.0143)
        val randNum = r.nextDouble()
        var cp = 0.0
        val letter:Letter
        for (q in prob.indices) {
            cp += prob[q]
            if (randNum <= cp) {
                tB.text = epsilon[q]
                tB.textOn = epsilon[q]
                tB.textOff = epsilon[q]
                var iced = 0
                if (r.nextFloat()<0.066f && !initialState && lives>0){
                    tB.setTextColor(ColorStateList.valueOf(Color.parseColor("#818c8e")))
                    if(arrayOf("A", "E", "I", "İ", "O", "Ö", "U", "Ü").contains(epsilon[q])) tB.background = AppCompatResources.getDrawable(this, R.drawable.icecircle1)
                    else tB.background = AppCompatResources.getDrawable(this, R.drawable.icerect1)
                    iced = 2
                } else{
                    tB.background = AppCompatResources.getDrawable(this, drawables[q])
                }
                if(initialState){
                    letter = Letter(button = tB, row = cr+7, col =  cc, frozen = 0)
                    tB.visibility = View.INVISIBLE
                    tB.animate().translationY(-150f).duration = 10
                    tB.animate().translationX((letter.col)*130f).duration = 10
                    object : CountDownTimer(2000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {}
                        override fun onFinish() {
                            tB.visibility = View.VISIBLE
                            tB.animate().translationY((letter.row)*130f+150f).duration = 1000 + 1500/(cr+1).toLong()
                        }
                    }.start()
                    tB.setOnClickListener {createWords(letter)}
                    genMat[cr+7][cc] = 1
                    letMatris.add(letter)
                }else{
                    letter = Letter(button = tB, row = 0, col =  cc, frozen = iced)
                    for (i in 9 downTo 0){
                        if (genMat[i][letter.col] == 0){
                            letter.row = i
                            letter.button!!.visibility = View.INVISIBLE
                            letter.button!!.animate().translationY(-150f).duration = 1
                            letter.button!!.animate().translationXBy((letter.col)*130f).duration = 1
                            object : CountDownTimer(500, 500) {
                                override fun onTick(millisUntilFinished: Long) {}
                                override fun onFinish() {
                                    letter.button!!.visibility = View.VISIBLE
                                    letter.button!!.animate().translationY((letter.row)*130f+150f).duration = 1000
                                }
                            }.start()
                            if(lives==0){
                                tB.text = ""
                                tB.textOn = ""
                                tB.textOff = ""
                                if(arrayOf("A", "E", "I", "İ", "O", "Ö", "U", "Ü").contains(epsilon[q])) tB.background = AppCompatResources.getDrawable(this, R.drawable.vowel)
                                else tB.background = AppCompatResources.getDrawable(this, R.drawable.consonant)
                                object : CountDownTimer(4000, 1000) {
                                    override fun onTick(millisUntilFinished: Long) {}
                                    override fun onFinish() {
                                        tB.background = AppCompatResources.getDrawable(this@Game, drawables[q])
                                        tB.setTextColor(ColorStateList.valueOf(Color.parseColor("#ffffff")))
                                        tB.text = epsilon[q]
                                        tB.textOn = epsilon[q]
                                        tB.textOff = epsilon[q]
                                        freezeAround(letter.row, letter.col, letter)
                                    }
                                }.start()
                            } else{
                                if(iced==2) freezeAround(letter.row, letter.col) else freezeAround(letter.row, letter.col, letter)
                            }
                            genMat[letter.row][letter.col] = 1
                            letMatris.add(letter)
                            letter.button!!.setOnClickListener{createWords(letter)}
                            break
                        }
                        if(i==0 && genMat[i][letter.col] == 1){
                            cUT.cancel()
                            val sharedPreferences = this.getSharedPreferences("com.denizozcan.findme", Context.MODE_PRIVATE)
                            if(sharedPreferences.getInt("$score",-1)==-1) sharedPreferences.edit().putInt("$score",score).apply()
                            val alert = AlertDialog.Builder(this@Game, R.style.AlertDialog)
                            alert.setTitle("GAME OVER!!!")
                            alert.setMessage("Your Score is ${score}. Do you want to try again?")
                            alert.setPositiveButton("") {_, _ ->
                                startActivity(intent)
                                finish()
                            }
                            alert.setPositiveButtonIcon(AppCompatResources.getDrawable(this,R.drawable.yes))
                            alert.setNegativeButtonIcon(AppCompatResources.getDrawable(this,R.drawable.no))
                            alert.setNegativeButton("") {_, _ ->
                                startActivity(Intent(applicationContext, MainPage::class.java))
                                finish()
                            }
                            binding.wordLayout.removeAllViews()
                            binding.gridLayout.removeAllViews()
                            System.gc()
                            try { alert.show() } catch (e:Exception) { e.printStackTrace()}
                        }
                    }
                }
                binding.gridLayout.addView(letter.button, ConstraintLayout.LayoutParams(130,130))
                break
            }
        }
    }

    private fun freezeAround(cr:Int, cc:Int, let:Letter? = null){
        for(k in letMatris.indices){
            if(cr-1 in 0..8 && letMatris[k].row == cr-1 && letMatris[k].col == cc && genMat[cr-1][cc]==1) freezeExtension(k, let)
            if(cr+1 in 1..9 && letMatris[k].row == cr+1 && letMatris[k].col == cc && genMat[cr+1][cc]==1) freezeExtension(k, let)
            if(cc-1 in 0..6 && letMatris[k].col == cc-1 && letMatris[k].row == cr && genMat[cr][cc-1]==1) freezeExtension(k, let)
            if(cc+1 in 1..7 && letMatris[k].col == cc+1 && letMatris[k].row == cr && genMat[cr][cc+1]==1) freezeExtension(k, let)
        }
    }
    private fun freezeExtension(k:Int, let:Letter?){
        if(letMatris[k].frozen == 2 && let != null) freezeAnim(let,1)
        if(letMatris[k].frozen == 0 && let == null) freezeAnim(letMatris[k])
    }

    private fun freezeAnim(let:Letter,i:Int = 5){
        object : CountDownTimer(5000, 5000/i.toLong()) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                let.frozen = 1
                let.button!!.setTextColor(ColorStateList.valueOf(Color.parseColor("#818c8e")))
                if(arrayOf("A", "E", "I", "İ", "O", "Ö", "U", "Ü").contains(let.button!!.text.toString())) let.button!!.background = AppCompatResources.getDrawable(this@Game, R.drawable.icecircle2)
                else let.button!!.background = AppCompatResources.getDrawable(this@Game, R.drawable.icerect2)
            }
        }.start()
    }

    private fun createWords(letter:Letter){
        if (letter.button!!.isChecked) {
            val textView = TextView(this)
            textView.id = "text(${letter.row},${letter.col})".hashCode()
            textView.text = letter.button!!.text.toString()
            textView.textSize = 20f
            textView.setTextColor(Color.parseColor("#ffffff"))
            binding.wordLayout.addView(textView)
            rmLetters.add(letter)
        } else {
            binding.wordLayout.removeView(binding.wordLayout.findViewById<TextView>("text(${letter.row},${letter.col})".hashCode()))
            rmLetters.remove(letter)
        }
    }

    private fun swipeLeft(){
        for (let in rmLetters) let.button!!.isChecked = false
        binding.wordLayout.removeAllViews()
        rmLetters.clear()
    }
    private fun swipeRight(){
        var counter = 0
        cUT.cancel()
        var s = ""
        for (i in 0 until binding.wordLayout.childCount){s += (binding.wordLayout.getChildAt(i) as TextView).text.toString()}
        if (turkishWords.contains(s) && s.length > 2) {
            for(let in rmLetters){
                if(let.frozen == 0){
                    binding.gridLayout.removeView(let.button)
                    genMat[let.row][let.col] = 0
                    letMatris.remove(let)
                }else{
                    let.frozen = 0
                    let.button!!.isChecked = false
                    let.button!!.setTextColor(ColorStateList.valueOf(Color.parseColor("#818c8e")))
                    if(arrayOf("A", "E", "I", "İ", "O", "Ö", "U", "Ü").contains(let.button!!.text.toString()))let.button!!.background = AppCompatResources.getDrawable(this@Game, R.drawable.icecircle3)
                    else let.button!!.background = AppCompatResources.getDrawable(this@Game, R.drawable.icerect3)
                }
                counter += 1
            }
            for (str in 9 downTo 1) {
                for (stn in 0 until 8){
                    if (genMat[str][stn]==0){
                        var  k = str - 1
                        while (k >= 0 && genMat[k][stn]==0){
                            k--
                        }
                        if (k >= 0){
                            genMat[str][stn] = 1
                            genMat[k][stn] = 0
                            for (let in letMatris){
                                if (let.row == k && let.col == stn){
                                    let.button!!.animate().translationY((str-k)*130f).duration = 100//Before up
                                    let.row = str
                                    let.button!!.animate().translationY((let.row)*130f+150f).duration = 200//After down
                                    if(let.frozen==2) freezeAround(let.row, let.col)
                                    else if(let.frozen==0)freezeAround(let.row, let.col,let)
                                    break
                                }
                            }
                        }
                    }
                }
            }
            if (interval>3) for (i in 0 until counter/2){createRandomLetter(0, r.nextInt(8))}
            binding.wordLayout.removeAllViews()
            binding.wordLayout.background = AppCompatResources.getDrawable(this@Game,R.drawable.list_grad1)
            rmLetters.clear()
            for (element in s){score += scoreMap[element.toString()]!!}
            binding.scoreText.text = score.toString()
        }else if(!turkishWords.contains(s) && s.length > 2){
            lives--
            if(lives==2) binding.mistake1.isChecked = true
            if(lives==1) binding.mistake2.isChecked = true
            if (lives == 0){
                binding.scoreText.text = ""
                binding.scoreText.background = AppCompatResources.getDrawable(this@Game,R.drawable.mistaken)
                object : CountDownTimer(2000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {}
                    override fun onFinish() {
                        binding.scoreText.background = ColorDrawable(Color.parseColor("#212121"))
                        binding.scoreText.text = score.toString()
                        binding.scoreText.setTextColor(ColorStateList.valueOf(Color.parseColor("#7e7e7e")))
                        binding.mistake1.isChecked = false
                        binding.mistake2.isChecked = false
                    }
                }.start()
                for (i in 0 until 8){createRandomLetter(0, i)}
                lives = 3
            }
            binding.wordLayout.background = AppCompatResources.getDrawable(this@Game,R.drawable.list_grad2)
        }
        val animationDrawable = binding.wordLayout.background as? AnimationDrawable
        animationDrawable?.start()
        object : CountDownTimer(1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                binding.wordLayout.background = AppCompatResources.getDrawable(this@Game,R.drawable.track)
                animationDrawable?.stop()
            }
        }.start()
        cUT.start()
    }
}
