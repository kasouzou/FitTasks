package com.kasouzou.fittasks.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kasouzou.fittasks.data.local.dao.TaskGroupDao
import com.kasouzou.fittasks.data.local.entity.TaskGroupEntity
import com.kasouzou.fittasks.data.local.entity.TaskItemEntity

@Database(
    entities = [TaskGroupEntity::class, TaskItemEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FitTasksDatabase : RoomDatabase() {
    abstract fun taskGroupDao(): TaskGroupDao
}
