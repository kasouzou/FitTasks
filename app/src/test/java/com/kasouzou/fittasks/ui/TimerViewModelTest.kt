package com.kasouzou.fittasks.ui

import androidx.compose.ui.graphics.Color
import app.cash.turbine.test
import com.kasouzou.fittasks.domain.model.TaskGroup
import com.kasouzou.fittasks.domain.model.TaskItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalTime

@OptIn(ExperimentalCoroutinesApi::class)
class TimerViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val sampleTaskGroup = TaskGroup(
        startTime = LocalTime.of(9, 0),
        endTime = LocalTime.of(10, 0),
        tasks = listOf(
            TaskItem("Task 1", Color.Red),
            TaskItem("Task 2", Color.Blue)
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        val viewModel = TimerViewModel(sampleTaskGroup)
        
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertEquals(0, initialState.currentTaskIndex)
            // 60 minutes / 2 tasks = 30 minutes = 1800 seconds
            assertEquals(1800L, initialState.remainingSeconds)
            assertEquals(true, initialState.isRunning) // Starts in init
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `timer ticks and decrements remaining seconds`() = runTest {
        val viewModel = TimerViewModel(sampleTaskGroup)
        
        viewModel.uiState.test {
            skipItems(1) // skip initial state
            
            testDispatcher.scheduler.advanceTimeBy(1000)
            val stateAfterOneSec = awaitItem()
            assertEquals(1799L, stateAfterOneSec.remainingSeconds)
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `timer automatically moves to next task when time is up`() = runTest {
        val viewModel = TimerViewModel(sampleTaskGroup)
        
        viewModel.uiState.test {
            skipItems(1) // skip initial
            
            // Advance time by 1800 seconds
            testDispatcher.scheduler.advanceTimeBy(1800 * 1000)
            skipItems(1800) // 1799, 1798, ..., 0
            
            // One more tick should switch task
            testDispatcher.scheduler.advanceTimeBy(1000)
            val stateAfterSwitch = awaitItem()
            assertEquals(1, stateAfterSwitch.currentTaskIndex)
            assertEquals(1800L, stateAfterSwitch.remainingSeconds)
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `skipTask moves to next task immediately`() = runTest {
        val viewModel = TimerViewModel(sampleTaskGroup)
        
        viewModel.uiState.test {
            skipItems(1) // skip initial
            
            viewModel.skipTask()
            val stateAfterSkip = awaitItem()
            assertEquals(1, stateAfterSkip.currentTaskIndex)
            assertEquals(1800L, stateAfterSkip.remainingSeconds)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
}
