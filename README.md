# BMS Quick Link & Control (4.0 Elite Floating Edition)

**Version:** 4.0 (Elite Floating UI/UX Redesign)  
**Platform:** Android  
**Language:** Kotlin  
**UI Framework:** Jetpack Compose (Material 3)  
**Design System:** Elite Minimal Flat Palette (Obsidian Dark Mode / Arctic Light Mode)  
**Backend:** Local SQLite Database (Saved Devices & Connected-Only Audit Logs)  
**Communication:** Bluetooth Low Energy (BLE)  
**Architecture:** MVVM + Repository + SQLiteOpenHelper + StateFlow + Coroutines

---

## Product Vision & Objective

**BMS Quick Link & Control** is an elite, lightweight BLE utility designed exclusively for establishing a reliable Bluetooth Low Energy connection with compatible LiFePO4 Battery Management Systems (BMS) and safely controlling four hardware functions.

**Explicitly Excluded:**  
Per the PRD, the application intentionally excludes every monitoring, telemetry, graphing, analytics, configuration, calibration, historical logging, or battery diagnostic feature (no voltage, current, power, battery percentage, cell voltages, charts, temperature graphs, password management, user accounts, cloud synchronization, export/import, data recording, analytics, battery health estimation, capacity calculation, or SOC prediction).

---

## Elite UI/UX Masterpiece Features (v4.0)

### 🌟 Breathtaking Design & UI
- **Floating Pill Navigation Dock (`MainScreen.kt`):** A spectacularly modern floating navigation bar (modeled after the latest high-end dynamic interfaces) offering custom pill indicator highlights, zero tonal distortion, and beautiful floating elevation.
- **Live RSSI Signal Meter Bars (`ConnectionHeader.kt`):** An absolute masterpiece header featuring dedicated square icon highlight containers, MAC address presentation, and an explicit live RSSI signal meter bar (representing signal strength via 4 clean vertical pill bars).
- **Left Accent Indicator Bars (`ControlPanel.kt`):** Hardware switches now feature an elegant 6dp left accent indicator bar, explicit status tags (`ACTIVE` / `OFF`), and custom animated content expansion.
- **Dashed Empty States (`SavedDevicesTab.kt`):** Beautifully crafted dashed border boxes for empty states with prompt action triggers to ensure a world-class user onboarding experience.
- **Obsidian & Arctic Palettes (Zero Gradients):** High-end solid flat colors (Deep Obsidian `0xFF0A0B0E` for Dark Mode, Pristine Arctic `0xFFF8FAFC` for Light Mode) offering breathtaking contrast and professional architectural aesthetics.

### 🗄️ Local Backend & Connected-Only Logging (`BmsDatabaseHelper.kt`, `BmsRepository.kt`)
- **Connected-Only Audit Logging:** Per user specification, audit logs are strictly reserved for successfully connected devices. Toggles and active connections are recorded to the local database table (`audit_logs`) while ambient scanning or disconnect events are completely filtered out.
- **SQLite Database (`BmsDatabaseHelper.kt`):** Manages local tables without external server dependencies or internet permissions:
  - `saved_devices`: Allows users to attach custom nicknames to known MAC addresses for fast connection shortcuts.
  - `audit_logs`: Generates an automated audit history of all physical switch operations (e.g., `CHARGE_TOGGLE_ON`, `DISCHARGE_TOGGLE_OFF`, `CONNECT`) for transparency and verification logging.

---

## Getting Started

### Prerequisites
- Android Studio Iguana / Jellyfish (or newer)
- JDK 17
- Minimum Android SDK: API 26 (Android 8.0)
- Target Android SDK: API 34 (Android 14)

### Building the Project
Open the `BMSQuickLink` directory in Android Studio. Gradle will automatically sync the required AndroidX, Jetpack Compose, and Navigation dependencies. Build and deploy directly to a BLE-capable Android physical device.
