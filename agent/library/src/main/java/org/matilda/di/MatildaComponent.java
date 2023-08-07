package org.matilda.di;

import dagger.Component;
import org.matilda.MatildaConnection;

@Component(modules = MatildaConnectionModule.class)
public interface MatildaComponent {
    MatildaConnection matildaConnection();
}
