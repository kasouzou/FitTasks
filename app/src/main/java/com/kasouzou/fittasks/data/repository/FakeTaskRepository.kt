package com.kasouzou.fittasks.data.repository

import com.kasouzou.fittasks.domain.model.TaskGroup
import com.kasouzou.fittasks.domain.model.TaskItem
import com.kasouzou.fittasks.domain.repository.TaskRepository
import com.kasouzou.fittasks.ui.theme.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalTime

class FakeTaskRepository : TaskRepository {
    private val taskGroups = listOf(
        TaskGroup(
            id = 1,
            startTime = LocalTime.of(7, 30),
            endTime = LocalTime.of(8, 0),
            tasks = listOf(
                TaskItem("洗顔・スキンケア", PastelBlue),
                TaskItem("朝食", PastelYellow),
                TaskItem("着替え", PastelPink),
                TaskItem("持ち物チェック", PastelGreen)
            )
        ),
        TaskGroup(
            id = 2,
            startTime = LocalTime.of(12, 0),
            endTime = LocalTime.of(13, 0),
            tasks = listOf(
                TaskItem("昼食", PastelOrange),
                TaskItem("散歩", PastelGreen),
                TaskItem("コーヒータイム", PastelPurple)
            )
        ),
        TaskGroup(
            id = 3,
            startTime = LocalTime.of(19, 0),
            endTime = LocalTime.of(21, 0),
            tasks = listOf(
                TaskItem("夕食の準備", PastelPink),
                TaskItem("夕食", PastelYellow),
                TaskItem("片付け", PastelBlue),
                TaskItem("入浴", PastelPurple),
                TaskItem("読書", PastelGreen)
            )
        )
    )

    private val _taskGroupsFlow = MutableStateFlow(taskGroups)

    override fun getTaskGroups(): Flow<List<TaskGroup>> {
        return _taskGroupsFlow.asStateFlow()
    }

    override suspend fun saveTaskGroup(taskGroup: TaskGroup) {
        val current = _taskGroupsFlow.value.toMutableList()
        val index = if (taskGroup.id != 0L) {
            current.indexOfFirst { it.id == taskGroup.id }
        } else {
            -1
        }
        
        if (index != -1) {
            current[index] = taskGroup
        } else {
            val newId = (current.maxOfOrNull { it.id } ?: 0) + 1
            current.add(taskGroup.copy(id = newId))
        }
        _taskGroupsFlow.value = current
    }

    override suspend fun deleteTaskGroup(taskGroup: TaskGroup) {
        val current = _taskGroupsFlow.value.toMutableList()
        current.remove(taskGroup)
        _taskGroupsFlow.value = current
    }
}
