# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Android app for producing colour-sample images of orthotic devices (装具). The user picks a colour for
each part of a device — leather belts, plastic shell, sponge, buttons, strings — and the app renders the
combination so it can be saved, shared, or shown to a fabricator.

## Build Commands

```bash
./gradlew assembleDebug      # Build
./gradlew testDebugUnitTest  # Unit tests
./gradlew lintDebug          # Lint (fails the build on errors)
./gradlew assembleRelease    # Release build
./gradlew clean
./gradlew connectedAndroidTest   # Instrumented tests (needs a device/emulator)
```

## Architecture

### Technology Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose + Material3 (no XML layouts, no DataBinding/ViewBinding)
- **Navigation**: Navigation Compose, single Activity
- **Min SDK**: 21, Target/Compile SDK: 35
- **Build**: AGP 8.7.2, Kotlin 2.1.0, Gradle KTS + `gradle/libs.versions.toml` version catalog

### The one idea that shapes the whole app

All six device screens are **the same screen**: stack N part images on top of each other, tint each one
with a colour picked from that part's palette, then draw an untinted overlay on top. The only differences
between devices are the part count, the images, the labels, and which palette each part uses.

That difference lives entirely in **`data/DeviceType.kt`** — one enum entry per device, each holding a
`List<PartSpec>`. There is exactly one screen composable (`ui/device/DeviceColorScreen.kt`) and one
ViewModel (`ui/device/DeviceColorViewModel.kt`) serving all six.

**To add or change a device, edit `DeviceType` and nothing else.** Do not add a screen or a ViewModel.

### Package Structure
- `com.nokopi.colorsample` — `MainActivity` (the only Activity; hosts the NavHost and the in-app update flow)
- `…​.navigation` — `Destinations` (routes) and `ColorSampleNavHost`
- `…​.data` — `DeviceType` / `PartSpec` (the device table) and `Palette` / `ColorOption` (the colours)
- `…​.ui.home` — device selection screen
- `…​.ui.device` — the shared colour screen: `DeviceColorScreen`, `DeviceColorViewModel`, `ColorPreview`, `ColorPicker`
- `…​.ui.theme` — Material3 colour scheme and typography
- `…​.util` — `ImageExport` (save to gallery / share)

### Colour system

`data/Palette.kt` holds every colour as a single `ColorOption(labelRes, Color)`. Each `Palette` enum entry
is the ordered list of options for one kind of material (`LEATHER`, `ORTHOSIS_PLASTIC`, `SPONGE`, `STRING`,
`BUTTON`, `BAND`, `PL_SPONGE`, `PLASTIC`, `WHITE_BLACK`).

Colour names are string resources (`color_white`, `color_beige`, …), so several distinct hex values can
share one Japanese name — which is how the original data worked.

Tinting happens at draw time via `ColorFilter.tint(...)` on the `Image` in `ColorPreview`. Nothing mutates
a `Drawable`. Colour swatches are drawn as Compose circles, not drawable resources.

### State

`DeviceColorViewModel` keeps everything in `SavedStateHandle` (`personName` plus an `IntArray` of the
selected option index per part), so state survives rotation and process death. The ViewModel holds no
`Context` — labels are resolved with `stringResource` in the composables — which keeps it testable with
plain JUnit. The device type arrives as the `type` route argument.

### Resource Organization
- Part images live in `res/drawable/` as `<device><n>.png` (`a1`–`a11`, `nb1`–`nb9`, …). The highest number
  in each set is the untinted overlay. `a.png`, `nb.png`, … are the home-screen thumbnails.
  These must stay in `drawable/`, not `drawable-v24/`, because minSdk is 21.
- `res/values/` holds only strings, the launcher-icon colour, and a minimal `Theme.ColorSample` used as the
  window background before Compose takes over. All real theming is in `ui/theme/`.

### Testing
- `DeviceCatalogTest` pins the device table and every palette against values transcribed independently
  from the pre-Compose implementation. If you change `DeviceType` or `Palette`, update this test
  deliberately — it is the guard against silently dropping a part or reordering a palette.
- `DeviceColorViewModelTest` covers selection, reset, validation, and `SavedStateHandle` restore.

### Notes
- In-app updates use `AppUpdateManager` with `ActivityResultContracts.StartIntentSenderForResult`.
- Saving writes via `MediaStore` on API 29+, and via `WRITE_EXTERNAL_STORAGE` + public Pictures on API 28
  and below. Sharing goes through a `FileProvider` rooted at `cacheDir/OColorDesign`.
- The preview canvas is deliberately a fixed light colour in both light and dark themes, so colours can be
  compared consistently. Do not make it theme-dependent.
- Colour options are hardcoded — they mirror the materials the fabricator actually stocks.
