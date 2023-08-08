package org.matilda.di;

import dagger.Component;
import org.matilda.messages.MessageDispatcher;

@Component(modules = {MatildaConnectionModule.class, MessagesModule.class})
public interface MatildaComponent {
    MessageDispatcher messageDispatcher();
}
