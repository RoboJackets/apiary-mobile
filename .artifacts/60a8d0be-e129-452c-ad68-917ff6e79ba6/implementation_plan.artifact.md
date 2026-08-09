# Implementation Plan - Fix Dagger/MissingBinding for DeviceService

The project is currently failing to build because `Mrd5Manager` (annotated with `@Singleton`) depends on `DeviceRepository` (annotated with `@ActivityRetainedScoped`). In Hilt, a singleton cannot depend on an activity-scoped binding because the singleton component is a parent of the activity component.

To resolve this, I will move the networking-related Hilt modules and repositories from `ActivityRetainedComponent` to `SingletonComponent`. This is appropriate for repositories and network services that should persist across activity life cycles.

## Proposed Changes

### DI Modules
Move all network-related modules to `SingletonComponent`.

#### [MODIFY] [MainActivityModule.kt](file:///C:/Users/evans/Documents/GitHub/apiary-mobile/app/src/main/java/org/robojackets/apiary/di/MainActivityModule.kt)
- Change `@InstallIn(ActivityRetainedComponent::class)` to `@InstallIn(SingletonComponent::class)`.
- Update imports accordingly.

#### [MODIFY] [BaseModule.kt](file:///C:/Users/evans/Documents/GitHub/apiary-mobile/base/src/main/java/org/robojackets/apiary/base/di/BaseModule.kt)
- Change `@InstallIn(ActivityRetainedComponent::class)` to `@InstallIn(SingletonComponent::class)`.
- Update imports accordingly.

#### [MODIFY] [AttendanceModule.kt](file:///C:/Users/evans/Documents/GitHub/apiary-mobile/attendance/src/main/java/org/robojackets/apiary/attendance/di/AttendanceModule.kt)
- Change `@InstallIn(ActivityRetainedComponent::class)` to `@InstallIn(SingletonComponent::class)`.
- Update imports accordingly.

#### [MODIFY] [MerchandiseModule.kt](file:///C:/Users/evans/Documents/GitHub/apiary-mobile/merchandise/src/main/java/org/robojackets/apiary/merchandise/di/MerchandiseModule.kt)
- Change `@InstallIn(ActivityRetainedComponent::class)` to `@InstallIn(SingletonComponent::class)`.
- Update imports accordingly.

### Repositories and Managers
Update all repositories and state managers to use `@Singleton` instead of `@ActivityRetainedScoped`.

#### [MODIFY] [DeviceRepository.kt](file:///C:/Users/evans/Documents/GitHub/apiary-mobile/base/src/main/java/org/robojackets/apiary/base/repository/DeviceRepository.kt)
- Change `@ActivityRetainedScoped` to `@Singleton`.

#### [MODIFY] [MeetingsRepository.kt](file:///C:/Users/evans/Documents/GitHub/apiary-mobile/base/src/main/java/org/robojackets/apiary/base/repository/MeetingsRepository.kt)
- Change `@ActivityRetainedScoped` to `@Singleton`.

#### [MODIFY] [ServerInfoRepository.kt](file:///C:/Users/evans/Documents/GitHub/apiary-mobile/base/src/main/java/org/robojackets/apiary/base/repository/ServerInfoRepository.kt)
- Change `@ActivityRetainedScoped` to `@Singleton`.

#### [MODIFY] [AttendanceRepository.kt](file:///C:/Users/evans/Documents/GitHub/apiary-mobile/attendance/src/main/java/org/robojackets/apiary/attendance/network/AttendanceRepository.kt)
- Change `@ActivityRetainedScoped` to `@Singleton`.

#### [MODIFY] [MerchandiseRepository.kt](file:///C:/Users/evans/Documents/GitHub/apiary-mobile/merchandise/src/main/java/org/robojackets/apiary/merchandise/network/MerchandiseRepository.kt)
- Change `@ActivityRetainedScoped` to `@Singleton`.

#### [MODIFY] [AuthStateManager.kt](file:///C:/Users/evans/Documents/GitHub/apiary-mobile/auth/src/main/java/org/robojackets/apiary/auth/AuthStateManager.kt)
- Change `@ActivityRetainedScoped` to `@Singleton`.

#### [MODIFY] [UserRepository.kt](file:///C:/Users/evans/Documents/GitHub/apiary-mobile/auth/src/main/java/org/robojackets/apiary/auth/network/UserRepository.kt)
- Change `@ActivityRetainedScoped" to "@Singleton`.

#### [MODIFY] [AuthManager.kt](file:///C:/Users/evans/Documents/GitHub/apiary-mobile/auth/src/main/java/org/robojackets/apiary/auth/oauth2/AuthManager.kt)
- Change `@ActivityRetainedScoped` to `@Singleton`.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:hiltJavaCompileDebug` to verify that the Dagger compilation error is resolved.
- Run a full build: `./gradlew assembleDebug`.

### Manual Verification
- N/A (Build fix)
