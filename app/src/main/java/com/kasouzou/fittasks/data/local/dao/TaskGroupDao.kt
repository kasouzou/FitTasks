package com.kasouzou.fittasks.data.local.dao

import androidx.room.*
import com.kasouzou.fittasks.data.local.entity.TaskGroupEntity
import com.kasouzou.fittasks.data.local.entity.TaskItemEntity
import kotlinx.coroutines.flow.Flow

data class TaskGroupWithItems(
    @Embedded val group: TaskGroupEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "groupId"
    )
    val items: List<TaskItemEntity>
)

@Dao
interface TaskGroupDao {
    @Transaction
    @Query("SELECT * FROM task_groups")
    fun getAllTaskGroupsWithItems(): Flow<List<TaskGroupWithItems>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskGroup(group: TaskGroupEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskItems(items: List<TaskItemEntity>)

    @Query("DELETE FROM task_items WHERE groupId = :groupId")
    suspend fun deleteTaskItems(groupId: Long)

    @Delete
    suspend fun deleteTaskGroup(group: TaskGroupEntity)

    @Transaction
    suspend fun saveTaskGroupWithItems(group: TaskGroupEntity, items: List<TaskItemEntity>) {
        val groupId = insertTaskGroup(group)
        // Update existing or new items
        deleteTaskItems(groupId)
        insertTaskItems(items.map { it.copy(groupId = groupId) })
    }
}
