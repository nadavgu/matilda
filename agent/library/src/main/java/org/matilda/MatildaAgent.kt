package org.matilda

import org.matilda.di.LoggerComponent
import org.matilda.di.MatildaComponent
import org.matilda.di.MatildaConnectionComponent
import org.matilda.di.create
import org.matilda.logger.Logger

class MatildaAgent(matildaConnection: MatildaConnection, vararg loggers: Logger) {
    private val mMatildaComponent: MatildaComponent

    init {
        mMatildaComponent = MatildaComponent::class.create(
            MatildaConnectionComponent::class.create(matildaConnection),
            LoggerComponent::class.create(loggers)
        )
        mMatildaComponent.destructionManager.addDestructor { matildaConnection.close() }
    }

    fun run() {
        try {
            mMatildaComponent.messageListener.start()
        } finally {
            mMatildaComponent.destructionManager.destruct()
        }
    }
}
