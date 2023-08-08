package org.matilda.di;

import dagger.Module;
import dagger.Provides;
import org.matilda.di.destructors.DestructionManager;

import javax.inject.Singleton;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Module
public class ExecutorServiceModule {
    private static final long TIMEOUT_SECONDS = 10;

    @Provides
    @Singleton
    ExecutorService executorService(DestructionManager destructionManager) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        destructionManager.addDestructor(() -> shutdownExecutorService(executorService));

        return executorService;
    }

    private static void shutdownExecutorService(ExecutorService executorService) {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(TIMEOUT_SECONDS / 2, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(TIMEOUT_SECONDS / 2, TimeUnit.SECONDS)) {
                    System.err.println("Failed to stop executor service!");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
