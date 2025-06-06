import pytest
import tests.java_plugin

from matilda.plugins.platform_not_supported_by_plugin_exception import PlatformNotSupportedByPluginException


class TestPluginLoading:
    def test_plugin_without_linux_entry_point_fails_on_linux(self, matilda_native_process):
        with pytest.raises(PlatformNotSupportedByPluginException):
            matilda_native_process.plugins.load_plugin(tests.java_plugin, "test")
