package com.example.coroutinestutorials

import CustomScope
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myFirstGlobalScope()
        //myFirstGlobalScopeBestPractice()
    }

    private fun myFirstGlobalScope() {
        (1..10000).forEach {
            lifecycleScope.launch {
                val threadName = Thread.currentThread().name
                println("myCoroutines: $it printed on thread $threadName")
            }
        }
    }

    private fun myFirstGlobalScopeBestPractice() {
        lifecycleScope.launch {
            (1..500).forEach {
                val threadName = Thread.currentThread().name
                println("myCoroutines: $it printed on thread $threadName")
            }
        }
    }
}