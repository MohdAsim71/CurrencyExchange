package com.sample.exchange.test

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

val _mutableFlow: MutableSharedFlow<Int> = MutableSharedFlow<Int>(replay = 3)
private val stateFlow = _mutableFlow
val job = Job()
val scope = CoroutineScope(Dispatchers.IO + job)

fun main() {
    anotherMain()
}

fun anotherMain() = runBlocking {
    scope.launch {
        val jobReturnFlow = launch { returnFlowTest() }
        val jobObserveFlow = launch { observingFlow() }

        jobReturnFlow.join() // Wait for returnFlowTest to finish updating the flow
        jobObserveFlow.join() // Wait for observingFlow to finish collecting
    }
    job.join()
}

suspend fun observingFlow() {
    stateFlow.collect {
        println(it)
    }
}

suspend fun returnFlowTest() {
    val list = mutableListOf(1, 2, 3, 4)
    list.forEach {
        delay(100)
        stateFlow.emit(it)
    }
}