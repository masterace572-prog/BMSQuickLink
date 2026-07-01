# BMS Quick Link & Control (8.0 Final Professional Corporate Edition)

**Version:** 8.0 (Final Professional Corporate Edition)  
**Platform:** Android  
**Language:** Kotlin  
**UI Framework:** Jetpack Compose (Material 3)  
**Design System:** Professional Corporate Palette (Absolute Zero Neon, Zero Pastel Tints, Zero Gradients)  
**Backend:** Local SQLite Database (Connected-Only Audit Logs)  
**Communication:** Bluetooth Low Energy (BLE)  
**Architecture:** MVVM + Repository + SharedPreferences Flows + StateFlow + Coroutines

---

## Product Vision & Objective

**BMS Quick Link & Control** is an elite, lightweight BLE utility designed exclusively for establishing a reliable Bluetooth Low Energy connection with compatible LiFePO4 Battery Management Systems (BMS) and safely controlling four hardware functions.

**Explicitly Excluded:**  
Per the PRD, the application intentionally excludes every monitoring, telemetry, graphing, analytics, configuration, calibration, historical logging, or battery diagnostic feature (no voltage, current, power, battery percentage, cell voltages, charts, temperature graphs, password management, user accounts, cloud synchronization, export/import, data recording, analytics, battery health estimation, capacity calculation, or SOC prediction).

---

## Elite Non-AI Corporate Specifications (v8.0 Final)

### 💎 Strict Professional Solid Palette & Layouts (Absolute Zero Neon)
To guarantee a true real-world corporate engineering look and eliminate all clunky AI-generated aesthetics, the entire application has been redesigned around strict professional design guidelines:
- **High-End Neutral Palette:** Deep solid charcoal background (`0xFF121212`) and solid surface (`0xFF1E1E1E`) for Dark Mode; crisp light neutral background (`0xFFF5F5F7`) and pure white surface (`0xFFFFFFFF`) for Light Mode.
- **Classic Solid Utility Accent Tones:** Muted corporate swatches (`Corporate Blue`, `Corporate Green`, `Corporate Orange`, `Corporate Red`, `Corporate Teal`, `Corporate Purple`) replace all neon shades.
- **Refined Typographic Hierarchy (`Type.kt`):** Completely scaled down massive, clunky titles in favor of a crisp, professional corporate typographic scale (`headlineLarge` -> 24sp bold, `titleMedium` -> 16sp medium, `bodyMedium` -> 13sp regular).
- **Clean Icon Styling:** Fully removed all glitchy SVG paths and pastel-tinted icon background boxes. All components use perfectly rendered standard Material Icons sitting cleanly on the surface.

### 🧭 Streamlined Navigation Suite (`MainScreen.kt`)
The bottom navigation bar has been re-architected into a compact, professional 3-tab layout:
1. **Connection Tab (`ConnectionTab.kt`):** Dedicated exclusively to connection lifecycle management, featuring the `ConnectionHeader`, primary Action Buttons, Quick Link Direct Launch, and discovered devices radar. No hardware switches are present here.
2. **Controls Tab (`ControlsTab.kt`):** Dedicated exclusively to hardware controls (`ControlPanel`). If no BLE connection is active, it presents an elegant professional empty state locking the switches.
3. **Settings Tab (`SettingsTab.kt`):** Houses the Appearance Console, Developer Profile, Legal copy, Verification Timeout customizer, and connected-only SQLite Audit Logs.

---

## Getting Started

### Prerequisites
- Android Studio Iguana / Jellyfish (or newer)
- JDK 17
- Minimum Android SDK: API 26 (Android 8.0)
- Target Android SDK: API 34 (Android 14)

### Building the Project
Open the `BMSQuickLink` directory in Android Studio. Gradle will automatically sync the required AndroidX, Jetpack Compose, and Navigation dependencies. Build and deploy directly to a BLE-capable Android physical device.
