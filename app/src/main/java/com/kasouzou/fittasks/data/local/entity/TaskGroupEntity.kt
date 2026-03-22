package com.kasouzou.fittasks.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity(tableName = "task_groups")
data class TaskGroupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startTime: LocalTime,
    val endTime: LocalTime
)
