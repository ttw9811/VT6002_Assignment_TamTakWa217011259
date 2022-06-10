package com.example.vt6002_assignment_tamtakwa217011259

import android.graphics.BitmapFactory

/**
@Description/Purpose : Create task to save database data
 */
class Task {
    companion object Factory {
        fun create(): Task = Task()
    }

    var objectId: String? = null
    var name: String? = null
    var price: String? = null
    var image: ByteArray? = null
    var done: Boolean? = false
}
