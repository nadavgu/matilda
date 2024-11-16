package org.matilda.di

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import org.matilda.commands.MatildaScope
import org.matilda.di.destructors.DestructionManager
import org.matilda.logger.Logger
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Component
@MatildaScope
interface ExecutorServiceComponent {
    @Provides
    @MatildaScope
    fun executorService(destructionManager: DestructionManager, logger: Logger): ExecutorService {
        val executorService = Executors.newCachedThreadPool()
        destructionManager.addDestructor { shutdownExecutorService(executorService, logger) }
        return executorService
    }

    companion object {
        private const val TIMEOUT_SECONDS: Long = 10
        private fun shutdownExecutorService(executorService: ExecutorService, logger: Logger) {
            executorService.shutdown()
            try {
                if (!executorService.awaitTermination(TIMEOUT_SECONDS / 2, TimeUnit.SECONDS)) {
                    executorService.shutdownNow()
                    if (!executorService.awaitTermination(TIMEOUT_SECONDS / 2, TimeUnit.SECONDS)) {
                        logger.log("Failed to stop executor service!")
                    }
                }
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }
    }
}
