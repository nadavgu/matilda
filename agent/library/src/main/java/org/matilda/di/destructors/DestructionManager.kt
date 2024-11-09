package org.matilda.di.destructors

import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DestructionManager @Inject constructor() {
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
