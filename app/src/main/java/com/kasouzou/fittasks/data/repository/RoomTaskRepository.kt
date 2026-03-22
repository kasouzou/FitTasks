package com.kasouzou.fittasks.data.repository

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.kasouzou.fittasks.data.local.dao.TaskGroupDao
import com.kasouzou.fittasks.data.local.dao.TaskGroupWithItems
import com.kasouzou.fittasks.data.local.entity.TaskGroupEntity
import com.kasouzou.fittasks.data.local.entity.TaskItemEntity
import com.kasouzou.fittasks.domain.model.TaskGroup
import com.kasouzou.fittasks.domain.model.TaskItem
import com.kasouzou.fittasks.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomTaskRepository(private val dao: TaskGroupDao) : TaskRepository {

    override fun getTaskGroups(): Flow<List<TaskGroup>> {
        return dao.getAllTaskGroupsWithItems().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun saveTaskGroup(taskGroup: TaskGroup) {
        val groupEntity = TaskGroupEntity(
            id = taskGroup.id,
            startTime = taskGroup.startTime,
            endTime = taskGroup.endTime
        )
        val itemEntities = taskGroup.tasks.map {
            TaskItemEntity(
                groupId = taskGroup.id,
                title = it.title,
                colorInt = it.color.toArgb()
            )
        }
        dao.saveTaskGroupWithItems(groupEntity, itemEntities)
    }

    override suspend fun deleteTaskGroup(taskGroup: TaskGroup) {
        dao.deleteTaskGroup(
            TaskGroupEntity(
                id = taskGroup.id,
                startTime = taskGroup.startTime,
                endTime = taskGroup.endTime
            )
        )
    }

    private fun TaskGroupWithItems.toDomain(): TaskGroup {
        return TaskGroup(
            id = group.id,
            startTime = group.startTime,
            endTime = group.endTime,
            tasks = items.map {
                TaskItem(
                    title = it.title,
                    color = Color(it.colorInt)
                )
            }
        )
    }
}
