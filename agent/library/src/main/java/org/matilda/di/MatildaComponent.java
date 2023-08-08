package org.matilda.di;

import dagger.Component;
import org.matilda.di.destructors.DestructionManager;
import org.matilda.messages.MessageDispatcher;

import javax.inject.Singleton;

@Component(modules = {MatildaConnectionModule.class, MessagesModule.class})
@Singleton
public interface MatildaComponent {
    MessageDispatcher messageDispatcher();

    DestructionManager destructionManager();
}
