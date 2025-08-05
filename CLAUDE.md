# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an Android application built for creating color sample images for prosthetic devices. The app allows users to select colors for different materials (leather, plastic, strings, buttons, etc.) and generates visual samples.

## Build Commands

### Building the project
```bash
./gradlew build
```

### Running tests
```bash
# Unit tests
./gradlew test

# Instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest
```

### Creating release build
```bash
./gradlew assembleRelease
```

### Clean build
```bash
./gradlew clean
```

## Project Architecture

### Technology Stack
- **Language**: Kotlin
- **UI Framework**: Android Data Binding
- **Architecture Pattern**: MVVM (Model-View-ViewModel)
- **Min SDK**: 21, Target SDK: 34
- **Build Tools**: Android Gradle Plugin 8.2.2

### Package Structure
- `com.nokopi.colorsample` - Main package containing activities
- `com.nokopi.colorsample.ui` - ViewModels and UI-related classes
- `com.nokopi.colorsample.utils` - Utility classes including color definitions and binding adapters
- `com.nokopi.colorsample.view` - Custom UI components

### Key Components

#### Activities
The app follows a multi-activity pattern where each prosthetic type has its own activity:
- `MainActivity` - Home screen with navigation to different prosthetic types
- `ACustomColor`, `NBCustomColor`, `SLBCustomColor`, `FTNCustomColor`, `PLCustomColor`, `POGOCustomColor` - Individual color customization activities

#### Color System
The color system is centralized in two main classes:
- `CustomColor` - Object containing hex color definitions for all available colors
- `ChangeColors` - Class containing color maps that associate drawable resources with Japanese color names, and methods for applying colors to drawables

Color categories include:
- Leather colors (`leathersColorMap`)
- Plastic colors (`plasticsColorMap`, `nbSLBAPogoPlasticsColorMap`)
- String colors (`stringsColorMap`)
- Button colors (`buttonsColorMap`)
- Sponge colors (`spongesColorMap`, `plSpongesColorMap`)
- Band colors (`bandsColorMap`)
- Binary colors (`whiteBlackMap`)

#### MVVM Implementation
- ViewModels (e.g., `AViewModel`) manage drawable states using LiveData
- Activities use Data Binding to connect UI elements with ViewModels
- Custom binding adapters in `BindingAdapters` handle complex UI updates
- ViewModels use factory pattern for dependency injection of Android Context

#### Custom Components
- `CustomSpinnerAdapter` - Handles dropdown menus for color selection
- Custom button classes for different prosthetic types
- `KeyboardUtils` - Handles keyboard visibility

### Data Binding Usage
The project extensively uses Android Data Binding:
- Layout files are bound to ViewModels
- Custom binding adapters handle spinner population
- LiveData observers automatically update UI when data changes

### App Features
- In-app update functionality using Google Play Core library
- Portrait-only orientation for all activities
- Custom color tinting system that applies colors to base drawable images
- Privacy policy integration
- Multi-language support (Japanese color names)

### Resource Organization
- Drawable resources organized by prosthetic type (a1-a11, ftn1-ftn10, etc.)
- Color definitions as both drawable XML files and hex codes
- Landscape-specific layouts available
- Multiple app icon densities and adaptive icons

### Development Notes
- The project uses view binding and data binding simultaneously
- Color application works by tinting base drawable images
- Each activity follows similar patterns for spinner setup and color handling
- ViewModels maintain separate LiveData for each drawable component
- The app is designed for a specific use case (prosthetic color sampling) with hardcoded color options