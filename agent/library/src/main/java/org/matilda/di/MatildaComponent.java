package org.matilda.di;

import dagger.Component;
import org.matilda.messages.MessageReceiver;

@Component(modules = {MatildaConnectionModule.class, MessagesModule.class})
public interface MatildaComponent {
    MessageReceiver messageReceiver();
}
