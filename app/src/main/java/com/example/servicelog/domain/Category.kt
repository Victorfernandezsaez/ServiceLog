package com.example.servicelog.domain


enum class Category(val label: String) {
    ENGINE("Engine"),
    TRANSMISSION("Transmission"),
    BRAKES("Brakes"),
    TIRES("Tires & Rims"),
    ELECTRICAL("Electrical"),
    BODY("Body work"),
    LEGAL("Paperwork"),
    INTERIOR("Interior"),
    OTHER("Other")
}