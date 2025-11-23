# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Added full support for android native agents (arm32 & arm64). Matilda can now run agents in android native processes.
- Plugins can also be loaded in android native processes
- Added an api function that runs matilda in a new native process on an android device connected through adb
- Added option to run linux native matilda agent in a way that blocks until a debugger connects
- Added a platform-independent filesystem abstraction

### Fixed

- Considering mavenLocal in repositories to allow using local dependencies
- Not throwing exceptions in native across interop border to prevent ABI problems
- ./setup.sh install also installs matilda's gradle libraries locally

### Changed

- Upgraded to Kotlin 2.2
- Upgraded to KSP 2
- Upgraded to dagger 2.57

### Removed

- Removed the MatildaProcess.services API

## [0.8.0] - 2025-07-17

### Added

- Added full support for android java agents. Matilda can now run agents in android jvm processes.
- Plugins can also be loaded in android jvm processes
- Added an api function that runs matilda in a new java process on an android device connected through adb

## [0.7.0] - 2025-06-07

### Added

- Added full support for native agents. Matilda can now run in native linux X86 64 processes
- Command generator supports generating kotlin native code
- Plugins can also be loaded in linux X86 64 processes
- Improved setup script and added options
- Added system tests

### Changed

- Plugin entry point API changed to support multiple platforms with different binaries and entry points
- Plugins now receive a kotlin.random.Random object as a dependency instead of java.util.Random

### Fixed

- Fixed packaging issues that caused protos and resources to not be included in final installed library
- Fixed accidentally removed generation of python code that calls java callbacks
- Fixed bug with handling commands that receive/return google protobuf generated message classes
- Fixed bug with dynamic service void functions
- Fixed bug with commands returning a list of lists

## [0.6.0] - 2025-01-10

### Added

- Command generator can now generate kotlin code that supports kotlin multiplatform

### Fixed

- Closing resources even if closing other resources fails
- Exiting gracefully when agent crashes, instead of blocking forever

### Changed

- The entire codebase now supports kotlin multiplatform, generated code as well.
  - kotlinpoet instead of javapoet
  - pbandk protobuf plugin instead of google protobuf
  - kotlin-inject instead of dagger
  - kotlinx.coroutines instead of java.util.concurrent
  - kotlinx.io instead of java.io/java.nio
  - kotlinx.datetime instead of java time
- Command generator protobuf files are now in a separate compileOnly library - command-generator-protos
- Upgraded to gradle 8.9

## [0.5.0] - 2024-11-09

### Added

- command generator can now be used with ksp, in addition to annotation processor anx kapt

### Changed

- The entire codebase was converted from java to kotlin (except for generated code)

## [0.4.0] - 2024-09-14

### Added

- Random object now exported to plugins

### Changed

- Java reflection functionality exported to plugin

## [0.3.0] - 2024-09-07

### Added

- Plugins can now be loaded to matilda to extend functionality


## [0.2.1] - 2024-06-28

### Fixed

- Fix maddie dependency link in setup.py


## [0.2.0] - 2024-06-28

### Added

- Callback API from java to python in matilda
- API to create a java proxy object from a list of interfaces and a callback
- API to access java constructors

### Fixed

- Indicative error when for some reason a command doesn't exist on the java side


### Changed

### Removed

## [0.1.0] - 2024-06-23

### Added

- Matilda python library with functionality to run the matilda agent in a remote process
- API to run the matilda agent in a new java process
- python API to access java classes, methods, and fields using reflection
- command generator annotation processor that allows to easily create commands between python side & java side

[unreleased]: https://github.com/nadavgu/matilda/compare/0.8.0...dev
[0.8.0]: https://github.com/nadavgu/matilda/compare/0.7.0...0.8.0
[0.7.0]: https://github.com/nadavgu/matilda/compare/0.6.0...0.7.0
[0.6.0]: https://github.com/nadavgu/matilda/compare/0.5.0...0.6.0
[0.5.0]: https://github.com/nadavgu/matilda/compare/0.4.0...0.5.0
[0.4.0]: https://github.com/nadavgu/matilda/compare/0.3.0...0.4.0
[0.3.0]: https://github.com/nadavgu/matilda/compare/0.2.1...0.3.0
[0.2.1]: https://github.com/nadavgu/matilda/compare/0.2.0...0.2.1
[0.2.0]: https://github.com/nadavgu/matilda/compare/0.1.0...0.2.0
[0.1.0]: https://github.com/nadavgu/matilda/releases/tag/0.1.0
