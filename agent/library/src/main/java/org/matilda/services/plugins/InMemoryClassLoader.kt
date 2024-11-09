package org.matilda.services.plugins

class InMemoryClassLoader(private val mClasses: Map<String, ByteArray>) : ClassLoader() {
    override fun findClass(name: String): Class<*> {
        val classData = mClasses.getOrElse(name) {
            throw ClassNotFoundException(name)
        }
        return defineClass(name, classData, 0, classData.size)
    }
}
