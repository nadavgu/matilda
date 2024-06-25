package org.matilda.commands.info

data class ProjectServices(val services: List<StaticServiceInfo>, val dynamicServices: List<DynamicServiceInfo>) {
    fun forEachService(action: (ServiceInfo) -> Unit) {
        forEachStaticService { action(it.serviceInfo) }
        forEachDynamicService { action(it.serviceInfo) }
    }

    fun forEachStaticService(action: (StaticServiceInfo) -> Unit) {
        services.forEach(action)
    }

    private fun forEachDynamicService(action: (DynamicServiceInfo) -> Unit) {
        dynamicServices.forEach(action)
    }

    fun forEachDynamicInterface(action: (ServiceInfo) -> Unit) {
        dynamicServices.filter { it.isInterface }.forEach { action(it.serviceInfo) }
    }



    fun forEachCommand(action: (CommandInfo) -> Unit) {
        forEachService { it.commands.forEach(action) }
    }

    fun forEachStaticCommand(action: (CommandInfo) -> Unit) {
        forEachStaticService { it.serviceInfo.commands.forEach(action) }
    }
}
