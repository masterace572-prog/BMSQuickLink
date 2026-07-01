# BMS Quick Link & Control (14.0 Final Terminal Console Edition)

**Version:** 14.0 (Final Live Connection Terminal Edition)  
**Platform:** Android  
**Language:** Kotlin  
**UI Framework:** Jetpack Compose (Material 3)  
**Design System:** Masterpiece Floating Dock + Live Scan Terminal + Real-Time Customization Engine  
**Security:** Resolvable Private Addresses (RPA) + EncryptedSharedPreferences (AES-256) + R8 Sandboxing  
**Backend:** Local SQLite Database (Connected-Only Audit Logs)  
**Communication:** Bluetooth Low Energy (BLE)  
**Architecture:** MVVM + Repository + StateFlow + Coroutines

---

## Product Vision & Objective

**BMS Quick Link & Control** is an elite, lightweight BLE utility designed exclusively for establishing a reliable Bluetooth Low Energy connection with compatible LiFePO4 Battery Management Systems (BMS) and safely controlling four hardware functions.

**Explicitly Excluded:**  
Per the PRD, the application intentionally excludes every monitoring, telemetry, graphing, analytics, configuration, calibration, historical logging, or battery diagnostic feature (no voltage, current, power, battery percentage, cell voltages, charts, temperature graphs, password management, user accounts, cloud synchronization, export/import, data recording, analytics, battery health estimation, capacity calculation, or SOC prediction).

---

## Ultimate Terminal Console Specs (v14.0 Final)

### 🌟 Live Scan & Connection Terminal (`ConnectionTab.kt`, `BleManager.kt`)
Per user specification, the manual Quick Link Direct Launch card has been permanently removed from the Connection tab in favor of an elite, real-time **Live Connection Terminal Console**:
- **Real-Time Log Stream:** Displays an automated, timestamped console stream tracking the entire BLE scanning lifecycle, active device discoveries, GATT connection state callbacks, MTU 247 negotiation, service discovery, and simulated hardware responses.
- **Interactive Log Clearing:** Includes an explicit "Clear Logs" trigger in the terminal header to instantly wipe the active terminal buffer.
- **Masterpiece Presentation:** Enclosed in a beautifully styled, high-contrast terminal box (`surfaceVariant.copy(alpha = 0.6f)`) that dynamically adapts to your chosen Card Style and Corner Style settings!

### 🧭 Streamlined Navigation Suite (`MainScreen.kt`)
The bottom navigation bar has been re-architected into a compact, professional 3-tab layout:
1. **Connection Tab (`ConnectionTab.kt`):** Dedicated exclusively to connection lifecycle management, featuring the `ConnectionHeader`, primary Action Buttons, discovered devices radar list, and the Live Connection Terminal. No hardware switches are present here.
2. **Controls Tab (`ControlsTab.kt`):** Displays the four physical hardware switches (`ControlPanel`). If no BLE connection is active, it presents an elegant professional information banner and keeps the actual hardware switches fully visible but safely disabled (dimmed/unclickable) until a connection is made!
3. **Settings Tab (`SettingsTab.kt`):** Houses the Appearance Console, Developer Profile, Legal copy, Verification Timeout customizer, and connected-only SQLite Audit Logs.

---

## Getting Started

### Prerequisites
- Android Studio Iguana / Jellyfish (or newer)
- JDK 17
- Minimum Android SDK: API 26 (Android 8.0)
- Target Android SDK: API 34 (Android 14)

### Building the Project
Open the `BMSQuickLink` directory in Android Studio. Gradle will automatically sync the required AndroidX, Jetpack Compose, Navigation, and Security Crypto dependencies. Build and deploy directly to a BLE-capable Android physical device.
