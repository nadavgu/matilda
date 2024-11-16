package org.matilda.di.destructors

import me.tatarka.inject.annotations.Inject
import org.matilda.commands.MatildaScope
import java.util.*

@MatildaScope
@Inject
class DestructionManager {
    private val mDestructors = Stack<Destructor>()

    fun addDestructor(destructor: Destructor) {
        mDestructors.push(destructor)
    }

    fun destruct() {
        while (!mDestructors.empty()) {
            mDestructors.pop().destruct()
        }
    }
}
