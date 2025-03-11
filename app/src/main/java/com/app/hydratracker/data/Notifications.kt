package com.app.hydratracker.data
import java.util.Date

data class Notifications(
    var title:String,
    var image: Int,
    var description: String,
    var date: Date,
    var readed:Boolean
)
