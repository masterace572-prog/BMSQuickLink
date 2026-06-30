package com.bms.quicklink.ble

import com.bms.quicklink.data.SwitchType
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import android.util.Log

data class CommandTask(
    val switchType: SwitchType,
    val targetState: Boolean,
    val payload: ByteArray,
    val verifyPredicate: (ByteArray) -> Boolean,
    val onSuccess: () -> Unit,
    val onFailure: (String) -> Unit
)

class CommandEngine(private val bleWriter: (ByteArray) -> Boolean) {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val queue = Channel<CommandTask>(Channel.UNLIMITED)
    private val notifyFlow = MutableSharedFlow<ByteArray>(replay = 0, extraBufferCapacity = 10)
    private val mutex = Mutex()
    
    private var job: Job? = null

    fun start() {
        if (job?.isActive == true) return
        job = scope.launch {
            for (task in queue) {
                mutex.withLock {
                    executeTaskWithRetry(task)
                }
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
        // Drain queue
        while (queue.tryReceive().isSuccess) {}
    }

    fun enqueue(task: CommandTask) {
        scope.launch {
            queue.send(task)
        }
    }

    fun onNotifyReceived(data: ByteArray) {
        scope.launch {
            notifyFlow.emit(data)
        }
    }

    private suspend fun executeTaskWithRetry(task: CommandTask) {
        val maxAttempts = 2 // 1 initial attempt + 1 retry
        var success = false

        for (attempt in 1..maxAttempts) {
            Log.d("CommandEngine", "Executing command for ${task.switchType}, attempt $attempt")
            
            // Write to BLE
            val writeSuccess = bleWriter(task.payload)
            if (!writeSuccess) {
                Log.w("CommandEngine", "BLE Write returned false on attempt $attempt")
                delay(200)
                continue
            }

            // Await Notify with 2 second timeout
            val notification = withTimeoutOrNull(2000L) {
                notifyFlow.firstOrNull { incoming -> task.verifyPredicate(incoming) }
            }

            if (notification != null) {
                Log.d("CommandEngine", "Notify verification succeeded for ${task.switchType}")
                success = true
                break
            } else {
                Log.w("CommandEngine", "Timeout waiting for Notify on attempt $attempt")
            }
        }

        withContext(Dispatchers.Main) {
            if (success) {
                task.onSuccess()
            } else {
                task.onFailure("Command failed")
            }
        }
    }
}
