from matilda.platform.architecture import Architecture
from matilda.platform.jvm_matilda_platform import JvmMatildaPlatform
from matilda.platform.jvm_system import JvmSystem
from matilda.platform.native_matilda_platform import NativeMatildaPlatform
from matilda.platform.operating_system import OperatingSystem

JVM = JvmMatildaPlatform(JvmSystem.PC)
ANDROID = JvmMatildaPlatform(JvmSystem.ANDROID)
LINUX_X64 = NativeMatildaPlatform(OperatingSystem.LINUX, Architecture.X86_64)
ANDROID_NATIVE_ARM32 = NativeMatildaPlatform(OperatingSystem.ANDROID, Architecture.ARM32)
ANDROID_NATIVE_ARM64 = NativeMatildaPlatform(OperatingSystem.ANDROID, Architecture.ARM64)