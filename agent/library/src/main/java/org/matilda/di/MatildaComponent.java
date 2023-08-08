package org.matilda.di;

import dagger.Component;
import org.matilda.di.destructors.DestructionManager;
import org.matilda.messages.MessageListener;

import javax.inject.Singleton;

@Component(modules = {MatildaConnectionModule.class, MessagesModule.class})
@Singleton
public interface MatildaComponent {
    MessageListener messageListener();

    DestructionManager destructionManager();
}
