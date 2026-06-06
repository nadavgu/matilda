# Matilda (core)

Matilda is a Python library providing infrastructure for a dynamic JVM/native debugger
(in the spirit of Frida). A Python client drives a Kotlin/Java **agent** loaded into a
target process; the two sides communicate over a pair of I/O streams using a
protobuf-based RPC. See `README.md` for the user-facing API and `docs/matilda_rpc.md`
for the RPC/codegen design.

This repo is the **core**: the Python client, the Kotlin Multiplatform agent, and the
RPC code generator that plugins depend on.

## Layout

- `matilda/` — the Python client package (published as the `matilda` pip package).
  - `matilda.py` — the `Matilda` entry class: `run_in_java_process`,
    `run_in_native_process`, `run_in_android_java_process`,
    `run_in_android_native_process`, or `run(MatildaRunner)` for custom injection.
  - `*_matilda_runner.py` — runners that launch/connect the agent (java process, native
    executable, adb, …).
  - `commands/`, `messages/`, `di/` — the RPC core: command registry/sender/runner,
    message server & serialization, maddie DI wiring.
  - `platform/supported_platforms.py` — the platform constants (`JVM`, `ANDROID`,
    `LINUX_X64`, `ANDROID_NATIVE_ARM32/64`).
  - `plugins/` — plugin discovery/loading via the `matilda.plugins` entry point group.
  - `generated/`, `protos/*_pb2.py`, `resources/` — **build output, gitignored. Do not
    hand-edit.**
- `agent/` — the Kotlin Multiplatform Gradle build (`rootProject.name = "Matilda Agent"`).
  - `library/` — the agent itself; `src/commonMain` plus per-target source sets
    (`jvmMain`, `androidMain`, `linuxMain`, `androidNativeArm32/64Main`, `javaMain`).
    Entry points: `MatildaAgent`, `MatildaConnection`.
  - `commands-generator/` + `commands-generator-api*` + `commands-generator-protos` —
    the KSP processor and its API/protos that turn `@MatildaService`/`@MatildaCommand`/
    `@MatildaDynamicService` annotations into generated Kotlin + Python + protobuf code.
  - `gradle-plugin/` — Gradle plugin scaffold (`MatildaPlugin` is currently a stub).
  - `test-plugin/`, `test-java-plugin/`, `test-java-plugin-android/` — fixtures used by
    the Python tests.
- `main.py` — example/scratch usage of the API and plugins.

## RPC & code generation

Annotate Kotlin/Java with `@MatildaService` (commands callable from Python),
`@MatildaCommand` (a command), and `@MatildaDynamicService` (interfaces that carry
callbacks across the boundary). At **agent build time**, KSP generates matching Python
classes into `matilda/generated/` and protobuf `*_pb2.py`. DI on the agent side is
**kotlin-inject**; on the Python side it is **maddie** (`DependencyContainer.get(...)`).
Protobuf is `pbandk` (Kotlin) / Google protobuf (Python).

**Always regenerate after changing a service, command, or `.proto`** — the Python
`generated/` tree is produced by the Gradle build, not committed.

## Build / test

- `./setup.sh install` — `gradlew assemble publishAllPublicationsToMavenLocalRepository`
  (publishes `org.matilda:*` to **mavenLocal** so plugins can consume them), then
  `pip install .`. **Run this before building any plugin.**
- `./setup.sh clean` — Gradle clean + remove generated code, `*_pb2.py`, build outputs,
  and non-committed resources.
- `./setup.sh dist` — assemble + build sdist/wheel.
- Agent only: `cd agent && ./gradlew assemble` (Kotlin 2.2, KSP 2.2.0-2.0.2, AGP 8.11.1,
  Dagger 2.57; needs the heap settings already in `agent/gradle.properties`).

To verify a change, build then run both test suites:

- Kotlin unit tests: `cd agent && ./gradlew test` (JUnit 5; real tests currently only in
  `commands-generator`).
- Python end-to-end tests: `./setup.sh install` then `pytest` from the repo root. They run
  the agent in real processes, parametrized across platforms and plugin type. Android/native
  targets are skipped unless you pass `--test-on-connected-android-device` (needs a device
  on adb).

## Notes

- Python dependencies: `maddie` (git+ssh, `dev` branch) and `protobuf~=5.28`.
- Versioning follows `CHANGELOG.md` (Keep a Changelog / SemVer); current version 0.9.0.
- Don't commit generated code, `resources/` binaries, or `agent/.../build` output — all
  gitignored. Commit/push only when asked; working branch is `dev`.
