package com.example.vt6002_assignment_tamtakwa217011259

/**
@Description/Purpose : create task change function
 */
interface TaskRowListener {
    fun onTaskChange(objectId: String, price:String, isDone: Boolean)
}