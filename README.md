# BMS Quick Link & Control (5.0 Ultimate Customization Edition)

**Version:** 5.0 (Ultimate Appearance Customization Edition)  
**Platform:** Android  
**Language:** Kotlin  
**UI Framework:** Jetpack Compose (Material 3)  
**Design System:** Full-Featured Appearance Console (Dynamic Themes, Swatch Palette, Card Styles)  
**Backend:** Local SQLite Database (Saved Devices & Connected-Only Audit Logs)  
**Communication:** Bluetooth Low Energy (BLE)  
**Architecture:** MVVM + Repository + SharedPreferences Flows + StateFlow + Coroutines

---

## Product Vision & Objective

**BMS Quick Link & Control** is an elite, lightweight BLE utility designed exclusively for establishing a reliable Bluetooth Low Energy connection with compatible LiFePO4 Battery Management Systems (BMS) and safely controlling four hardware functions.

**Explicitly Excluded:**  
Per the PRD, the application intentionally excludes every monitoring, telemetry, graphing, analytics, configuration, calibration, historical logging, or battery diagnostic feature (no voltage, current, power, battery percentage, cell voltages, charts, temperature graphs, password management, user accounts, cloud synchronization, export/import, data recording, analytics, battery health estimation, capacity calculation, or SOC prediction).

---

## Ultimate Appearance Console Features (v5.0)

### 🎨 Fully Functional Real-Time Appearance Customization
Accessible in the Settings screen, the new Appearance Console provides full-featured, highly intuitive customization that instantly re-themes the entire application in real-time:
- **3-Way Theme Mode Selection:** Easily toggle between **Dark Mode (Obsidian)**, **Light Mode (Arctic)**, and **System Default**.
- **Dynamic Accent Swatch Palette:** Instantly change the app's accent color across active switches, primary buttons, floating navigation indicators, and badge pills. Choose from an elite palette of 6 striking pairings:
  - `Electric Blue` (`0xFF3B82F6`)
  - `Emerald Green` (`0xFF10B981`)
  - `Sunset Orange` (`0xFFF59E0B`)
  - `Rose Crimson` (`0xFFF43F5E`)
  - `Cyber Cyan` (`0xFF06B6D4`)
  - `Royal Purple` (`0xFF8B5CF6`)
- **3-Way Card Style Engine (`LocalCardStyle`):** Change the architectural appearance of cards across the entire application instantly:
  - `Solid Clean (FILLED)`: High-contrast solid surface cards with zero borders.
  - `Border Outlined (OUTLINED)`: Transparent cards defined by crisp, elegant 1dp outline borders.
  - `Glassmorphism (GLASS)`: Premium semi-transparent translucent cards (`0.35f` alpha) with subtle border lines.

### 🌟 High-End Scaffolding & Layouts
- **Floating Pill Navigation Dock (`MainScreen.kt`):** A spectacularly modern floating navigation bar offering custom pill indicator highlights, zero tonal distortion, and beautiful floating elevation.
- **Live RSSI Signal Meter Bars (`ConnectionHeader.kt`):** An absolute masterpiece header featuring dedicated square icon highlight containers, MAC address presentation, and an explicit live RSSI signal meter bar (representing signal strength via 4 clean vertical pill bars).
- **Left Accent Indicator Bars (`ControlPanel.kt`):** Hardware switches feature an elegant 6dp left accent indicator bar, explicit status tags (`ACTIVE` / `OFF`), and custom animated content expansion.

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
