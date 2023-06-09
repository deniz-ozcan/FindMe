package com.denizozcan.findme
import android.annotation.SuppressLint
import android.content.Context
import com.denizozcan.findme.databinding.MainpageBinding
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView

class MainPage : AppCompatActivity() {
    private lateinit var binding : MainpageBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = MainpageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.startBut.setOnClickListener {
            val intent =  Intent(applicationContext,Game::class.java)
            startActivity(intent)
        }
        val arr = ArrayList<Int>()
        val sharedPreferences = this.getSharedPreferences("com.denizozcan.findme", Context.MODE_PRIVATE)
        for(i in 0 until 10000){
            if(sharedPreferences.getInt("$i",-1)!=-1){
                val q = sharedPreferences.getInt("$i",-1)
                arr.add(q)
            }
        }
        if(arr.size>0){
            val q = arr.sortedDescending()
            for (i in q.indices){
                if(i<10){
                    val textView = TextView(this)
                    textView.text = "${i+1}.Score ${q[i]}"
                    textView.textSize = 20f
                    textView.gravity = Gravity.CENTER
                    textView.setTextColor(Color.parseColor("#ff0088"))
                    binding.leadertable.addView(textView)
                }else{
                    sharedPreferences.edit().remove("${q[i]}").apply()
                }
            }
        }
    }
}