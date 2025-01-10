package org.matilda.di.destructors

import me.tatarka.inject.annotations.Inject
import org.matilda.commands.MatildaScope

@MatildaScope
@Inject
class DestructionManager {
    private val mDestructors = ArrayDeque<Destructor>()

    fun addDestructor(destructor: Destructor) {
        mDestructors.addFirst(destructor)
    }

    fun destruct() {
        while (!mDestructors.isEmpty()) {
            mDestructors.removeFirst().destruct()
        }
    }
}
