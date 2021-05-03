package com.luoqiaoyou.library

import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class Test {
    @ExperimentalTime
    @Performance
    fun testBlockBody(int: Int): Boolean {
        measureTime {
            println("exec logic")
            if (int == 1) {
                println("touch center")
                return true
            }
            if (int == 2) {
                println("return measureTime")
                return@measureTime
            }
        }

        println("touch bottom")
        return false
    }

    @Performance
    fun testInline() = println("testInline")

    @Performance
    fun testUnit() {
        println("testUnit")
    }
}