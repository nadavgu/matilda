package org.matilda

import org.matilda.di.DaggerMatildaComponent
import org.matilda.di.LoggerModule
import org.matilda.di.MatildaComponent
import org.matilda.di.MatildaConnectionModule
import org.matilda.logger.Logger

class MatildaAgent(matildaConnection: MatildaConnection, vararg loggers: Logger) {
    private val mMatildaComponent: MatildaComponent

    init {
        mMatildaComponent = DaggerMatildaComponent.builder()
            .matildaConnectionModule(MatildaConnectionModule(matildaConnection))
            .loggerModule(LoggerModule(*loggers))
            .build()
        mMatildaComponent.destructionManager().addDestructor { matildaConnection.close() }
    }

    fun run() {
        try {
            mMatildaComponent.messageListener().start()
        } finally {
            mMatildaComponent.destructionManager().destruct()
        }
    }
}
