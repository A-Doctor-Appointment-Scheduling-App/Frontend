package com.example.doccur.entities


enum class AppointmentStatus(val value: String) {
    Pending("Pending"),
    Confirmed("Confirmed"),
    Rejected("Rejected"),
    Completed("Completed"),
    Cancelled("Cancelled");

    companion object {
        fun from(value: String): AppointmentStatus {
            return values().firstOrNull { it.value == value } ?: Pending
        }
    }
}
