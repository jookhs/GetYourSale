package com.example.getyoursale

data class Offer(val image: String, val name: String)
data class Brand(val image: String, val name: String)
data class Notification(val image: String, val name: String, val description: String, var stateRead: Boolean = false, var start: Long)