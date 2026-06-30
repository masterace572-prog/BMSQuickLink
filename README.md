# BMS Quick Link & Control (6.0 Ultimate Feature Pack Edition)

**Version:** 6.0 (Ultimate Pro Feature Pack & UI/UX Redesign)  
**Platform:** Android  
**Language:** Kotlin  
**UI Framework:** Jetpack Compose (Material 3)  
**Design System:** Elite Appearance Console + Hardware Configuration Suite  
**Backend:** Local SQLite Database (Saved Devices & Connected-Only Audit Logs)  
**Communication:** Bluetooth Low Energy (BLE)  
**Architecture:** MVVM + Repository + SharedPreferences Flows + StateFlow + Coroutines

---

## Product Vision & Objective

**BMS Quick Link & Control** is an elite, lightweight BLE utility designed exclusively for establishing a reliable Bluetooth Low Energy connection with compatible LiFePO4 Battery Management Systems (BMS) and safely controlling four hardware functions.

**Explicitly Excluded:**  
Per the PRD, the application intentionally excludes every monitoring, telemetry, graphing, analytics, configuration, calibration, historical logging, or battery diagnostic feature (no voltage, current, power, battery percentage, cell voltages, charts, temperature graphs, password management, user accounts, cloud synchronization, export/import, data recording, analytics, battery health estimation, capacity calculation, or SOC prediction).

---

## Ultimate Pro Feature Pack (v6.0)

### 🛠️ Elite Fully Functional Hardware Features
- **BLE Notify Verification Timeout Customizer (`SettingsTab.kt`):** Customize the precise time allowance for GATT Notify hardware verification (`1.0s`, `2.0s`, `3.0s`, `5.0s`). Ensures seamless compatibility with both ultra-fast and high-latency BMS microcontrollers.
- **Quick Link Direct Launch (`ControlsTab.kt`):** Type in or paste any known Bluetooth MAC address to instantly establish a direct GATT connection without waiting for ambient radio scans!
- **BMS Profile Nickname Live Editor (`SavedDevicesTab.kt`):** Full interactive database CRUD capabilities allowing users to update and edit custom nicknames for saved battery profiles at any time.
- **Offline Demo / Simulation Mode (`BleManager.kt`, `CommandEngine.kt`):** Toggle a virtual offline proving ground in Settings. Instantly discover and connect to virtual LiFePO4 BMS hardware, test switch latency, simulate Notify verification packets, and generate real database audit logs without needing physical batteries nearby!

### 🎨 Fully Functional Real-Time Appearance Console
Accessible in the Settings screen, the Appearance Console provides full-featured, highly intuitive customization that instantly re-themes the entire application in real-time:
- **3-Way Theme Mode Selection:** Easily toggle between **Dark Mode (Obsidian)**, **Light Mode (Arctic)**, and **System Default**.
- **Dynamic Accent Swatch Palette:** Instantly change the app's accent color across active switches, primary buttons, floating navigation indicators, and badge pills. Choose from an elite palette of 6 striking pairings: `Electric Blue`, `Emerald Green`, `Sunset Orange`, `Rose Crimson`, `Cyber Cyan`, and `Royal Purple`.
- **3-Way Card Style Engine (`LocalCardStyle`):** Change the architectural appearance of cards across the entire application instantly (`Solid Clean`, `Border Outlined`, `Glassmorphism`).

### 🌟 High-End Scaffolding & Layouts
- **Floating Pill Navigation Dock (`MainScreen.kt`):** A spectacularly modern floating navigation bar offering custom pill indicator highlights, zero tonal distortion, and beautiful floating elevation.
- **Live RSSI Signal Meter Bars (`ConnectionHeader.kt`):** An absolute masterpiece header featuring dedicated square icon highlight containers, MAC address presentation, and an explicit live RSSI signal meter bar.
- **Left Accent Indicator Bars (`ControlPanel.kt`):** Hardware switches feature an elegant 6dp left accent indicator bar, explicit status tags (`ACTIVE` / `OFF`), and custom animated content expansion.

---

## Getting Started

### Prerequisites
- Android Studio Iguana / Jellyfish (or newer)
- JDK 17
- Minimum Android SDK: API 26 (Android 8.0)
- Target Android SDK: API 34 (Android 14)

### Building the Project
Open the `BMSQuickLink` directory in Android Studio. Gradle will automatically sync the required AndroidX, Jetpack Compose, and Navigation dependencies. Build and deploy directly to a BLE-capable Android physical device.
