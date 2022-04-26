package com.example.getyoursale

data class Offer(val image: String, val name: String, val url: String)
data class Brand(val image: String, val name: String)
data class Notification(var image: String, var name: String, var description: String, var stateRead: Boolean = false, var start: Long, var url: String)