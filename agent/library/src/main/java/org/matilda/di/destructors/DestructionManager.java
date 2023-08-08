package org.matilda.di.destructors;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Stack;

@Singleton
public class DestructionManager {
    private final Stack<Destructor> mDestructors;

    @Inject
    public DestructionManager() {
        mDestructors = new Stack<>();
    }

    public void addDestructor(Destructor destructor) {
        mDestructors.push(destructor);
    }

    public void destruct() {
        while (!mDestructors.empty()) {
            mDestructors.pop().destruct();
        }
    }
}
