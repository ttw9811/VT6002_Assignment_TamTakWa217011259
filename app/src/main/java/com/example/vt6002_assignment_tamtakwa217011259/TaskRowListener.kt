package com.example.vt6002_assignment_tamtakwa217011259

interface TaskRowListener {
    fun onTaskChange(objectId: String, isDone: Boolean)
    fun onTaskDelete(objectId: String)
}