package com.rooze.insta_2.presentation.notification

enum class NotificationType(val value: Int) {
    POSTING_STATUS(0), POST(1);

    fun getNotificationId(dataId: Int): Int = dataId * values().size + value

    companion object {
        fun fromValue(value: Int): NotificationType = when (value) {
            POSTING_STATUS.value -> POSTING_STATUS
            POST.value -> POST
            else -> throw IllegalArgumentException("Invalid value $value")
        }
    }
}