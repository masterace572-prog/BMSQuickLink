# BMS Quick Link & Control (7.0 Production-Ready Solid Edition)

**Version:** 7.0 (Production-Ready Solid UI Edition)  
**Platform:** Android  
**Language:** Kotlin  
**UI Framework:** Jetpack Compose (Material 3)  
**Design System:** Solid Architectural Palette (Zero Neon, Zero Gradients)  
**Backend:** Local SQLite Database (Saved Devices & Connected-Only Audit Logs)  
**Communication:** Bluetooth Low Energy (BLE)  
**Architecture:** MVVM + Repository + SharedPreferences Flows + StateFlow + Coroutines

---

## Product Vision & Objective

**BMS Quick Link & Control** is an elite, lightweight BLE utility designed exclusively for establishing a reliable Bluetooth Low Energy connection with compatible LiFePO4 Battery Management Systems (BMS) and safely controlling four hardware functions.

**Explicitly Excluded:**  
Per the PRD, the application intentionally excludes every monitoring, telemetry, graphing, analytics, configuration, calibration, historical logging, or battery diagnostic feature (no voltage, current, power, battery percentage, cell voltages, charts, temperature graphs, password management, user accounts, cloud synchronization, export/import, data recording, analytics, battery health estimation, capacity calculation, or SOC prediction).

---

## Production-Ready Solid Architectural Specifications (v7.0)

### 💎 Professional Solid Palette (Zero Neon & Zero Gradients)
To deliver the ultimate corporate hardware dashboard finish, all harsh neon tones have been replaced with rich, classic solid architectural colors:
- **Rich Solid Dark Mode:** Deep solid charcoal background (`0xFF121212`) and solid surface (`0xFF1E1E1E`) with crisp solid borders (`0xFF383838`).
- **Crisp Solid Light Mode:** Light neutral background (`0xFFF5F5F5`) and pure solid white surface (`0xFFFFFFFF`).
- **Classic Solid Accent Swatches:** Offers 6 professional hardware utility pairings: `Classic Blue` (`0xFF1976D2`), `Classic Green` (`0xFF2E7D32`), `Classic Orange` (`0xFFE65100`), `Classic Crimson` (`0xFFC62828`), `Classic Teal` (`0xFF00695C`), and `Classic Purple` (`0xFF6A1B9A`).

### 🌟 Production-Ready Layout Adjustments
- **Generous Dialog Formats:** All dialogs (`AddDeviceDialog`, `EditDeviceDialog`, `ConfirmationDialog`, `PermissionRationaleDialog`) feature generous 32dp corner rounding, prominent typography titles, and spacious primary button padding.
- **Uncluttered Top App Bars:** Perfectly colored solid backgrounds matching the active theme mode across `AppearanceScreen`, `DeveloperScreen`, `TermsScreen`, and `PrivacyScreen`.
- **Streamlined Navigation & Spacing:** Enforces robust bottom padding (`110.dp`) above the floating navigation dock across all console tabs to guarantee full unhindered scrolling.

---

## Getting Started

### Prerequisites
- Android Studio Iguana / Jellyfish (or newer)
- JDK 17
- Minimum Android SDK: API 26 (Android 8.0)
- Target Android SDK: API 34 (Android 14)

### Building the Project
Open the `BMSQuickLink` directory in Android Studio. Gradle will automatically sync the required AndroidX, Jetpack Compose, and Navigation dependencies. Build and deploy directly to a BLE-capable Android physical device.
