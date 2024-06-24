package org.matilda.di;

import dagger.Component;
import org.matilda.commands.CommandsModule;
import org.matilda.di.destructors.DestructionManager;
import org.matilda.messages.MessageServer;

import javax.inject.Singleton;

@Component(modules = {MatildaConnectionModule.class, MessagesModule.class, LoggerModule.class,
        CommandsModule.class, UtilsModule.class})
@Singleton
public interface MatildaComponent {
    MessageServer messageListener();

    DestructionManager destructionManager();
}
