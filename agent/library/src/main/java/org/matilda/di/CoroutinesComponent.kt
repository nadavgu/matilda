package org.matilda.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newFixedThreadPoolContext
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import org.matilda.commands.MatildaScope
import org.matilda.di.destructors.DestructionManager
import org.matilda.logger.Logger
import kotlin.coroutines.CoroutineContext

@Component
@MatildaScope
interface CoroutinesComponent {

    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    @Provides
    @MatildaScope
    fun coroutineContext(destructionManager: DestructionManager, logger: Logger): CoroutineContext =
        newFixedThreadPoolContext(4, "MatildaThreadPool").also {
            destructionManager.addDestructor { it.close() }
        }

    @Provides
    @MatildaScope
    fun coroutineScope(coroutineContext: CoroutineContext) = CoroutineScope(coroutineContext)
}
