package org.matilda.commands.info

data class StaticServiceInfo(val serviceInfo: ServiceInfo,
                             val hasInjectConstructor: Boolean)
