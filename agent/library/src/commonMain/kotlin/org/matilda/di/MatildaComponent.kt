package org.matilda.di

import me.tatarka.inject.annotations.Component
import org.matilda.commands.CommandsComponent
import org.matilda.commands.MatildaScope
import org.matilda.di.destructors.DestructionManager
import org.matilda.messages.MessageServer

@Component
@MatildaScope
abstract class MatildaComponent(@Component val connectionComponent: MatildaConnectionComponent,
                                @Component val loggerComponent: LoggerComponent) :
    MessagesComponent, CommandsComponent, UtilsComponent {
    abstract val messageListener: MessageServer
    abstract val destructionManager: DestructionManager
}
