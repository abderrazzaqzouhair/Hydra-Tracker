package com.app.hydratracker.data

import java.util.Calendar
import java.util.Date

class CustomDate(var year: Int, var month: Int, var day: Int) {
    fun toDate(): Date {
        return Calendar.getInstance().apply {
            set(year, month - 1, day) // Month is 0-based in Calendar
        }.time
    }
}