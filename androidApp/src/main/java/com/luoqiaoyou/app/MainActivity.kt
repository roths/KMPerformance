package com.luoqiaoyou.app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.luoqiaoyou.library.Performance
import com.luoqiaoyou.library.Test
import kotlin.time.ExperimentalTime

class MainActivity : AppCompatActivity() {

    @ExperimentalTime
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Test().testBlockBody(1)
        Test().testBlockBody(2)
        Test().testBlockBody(3)
        Test().testInline()
        Test().testUnit()
        testCustomUse()
    }

    @Performance
    fun testCustomUse() {
        println("testCustomUse")
    }
}
