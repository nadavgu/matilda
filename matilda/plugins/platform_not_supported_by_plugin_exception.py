from matilda.platform.matilda_platform import MatildaPlatform


class PlatformNotSupportedByPluginException(Exception):
    def __init__(self, platform: MatildaPlatform, plugin_name: str):
        super().__init__(f"Plugin {plugin_name} does not support platform {platform}")
