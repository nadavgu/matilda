package org.matilda.commands.info

data class ProjectServices(val services: List<StaticServiceInfo>, val dynamicServices: List<ServiceInfo>) {
    fun forEachService(action: (ServiceInfo) -> Unit) {
        services.forEach {
            action(it.serviceInfo)
        }
        dynamicServices.forEach(action)
    }

    fun forEachStaticService(action: (StaticServiceInfo) -> Unit) {
        services.forEach(action)
    }

    fun forEachCommand(action: (CommandInfo) -> Unit) {
        forEachService { it.commands.forEach(action) }
    }

    fun forEachStaticCommand(action: (CommandInfo) -> Unit) {
        forEachStaticService { it.serviceInfo.commands.forEach(action) }
    }
}
