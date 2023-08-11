package org.matilda.commands.info

data class ProjectServices(val services: List<ServiceInfo>) {
    fun forEachService(action: (ServiceInfo) -> Unit) {
        services.forEach(action)
    }

    fun forEachCommand(action: (CommandInfo) -> Unit) {
        forEachService { it.commands.forEach(action) }
    }
}
