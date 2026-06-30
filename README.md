# BMS Quick Link & Control

**Version:** 1.0  
**Platform:** Android  
**Language:** Kotlin  
**UI Framework:** Jetpack Compose  
**Design System:** Material 3  
**Communication:** Bluetooth Low Energy (BLE)  
**Architecture:** MVVM + Repository + StateFlow + Coroutines

---

## Product Vision & Objective

**BMS Quick Link & Control** is a lightweight BLE utility designed exclusively for establishing a reliable Bluetooth Low Energy connection with compatible LiFePO4 Battery Management Systems (BMS) and safely controlling four hardware functions.

**Explicitly Excluded:**  
Per the PRD, the application intentionally excludes every monitoring, telemetry, graphing, analytics, configuration, calibration, historical logging, or battery diagnostic feature (no voltage, current, power, battery percentage, cell voltages, charts, temperature graphs, password management, user accounts, cloud synchronization, export/import, data recording, analytics, battery health estimation, capacity calculation, or SOC prediction).

The application exists only to:
- Discover compatible BMS devices
- Establish a stable BLE connection
- Maintain the connection
- Display connection state
- Send authenticated switch commands
- Verify switch execution using Notify responses
- Gracefully recover from disconnects

---

## System Architecture

```
Presentation Layer (Compose UI / BmsScreen / Dialogs)
       ↓
   ViewModel (BmsViewModel / StateFlow FSM)
       ↓
  Repository (BmsRepository / Command verification)
       ↓
 BLE Manager (BleManager / CommandEngine / GATT lifecycle)
       ↓
BluetoothGatt (Android BLE API)
       ↓
 Battery BMS (Hardware)
```

### Core Modules & Specifications

1. **OS Permissions Engine (`MainActivity.kt`, `PermissionRationaleDialog.kt`)**
   - Implements Android 12+ `BLUETOOTH_SCAN` (`neverForLocation`), `BLUETOOTH_CONNECT`, and legacy `ACCESS_FINE_LOCATION`.
   - Explicitly omits `INTERNET` permission to guarantee full offline privacy and security.
   - Elegant runtime check, rationale explanation, and direct deep-link to App Settings for permanent denials.

2. **Intelligent BLE Filtering (`ScanFilterHelper.kt`)**
   - Prioritizes scan results to eliminate scan clutter.
   - Supported prefixes: `BMS-`, `LOSSIGY`, `LSG-`, `JK-`, `DALY`, `JBD`, `LLT`, `SMARTBMS`.
   - Unknown devices remain hidden by default, with an optional developer mode toggle in the App Bar to expose all BLE devices.

3. **BLE Finite State Machine (`BleFsmState.kt`, `BleManager.kt`)**
   - Deterministic 4-state FSM: `Disconnected` → `Scanning` → `Connecting` → `Connected`.
   - Connection Phase automates `BluetoothGatt.connect()`, `requestMtu(247)`, `discoverServices()`, matches RX (Write/Write No Response) and TX (Notify) characteristics, enables notification, and writes the CCCD descriptor (`0x2902`).

4. **Hardware Control & Command Engine (`CommandEngine.kt`, `BmsRepository.kt`)**
   - Supports four physical hardware switches: **Charge MOSFET**, **Discharge MOSFET**, **Auto Balance**, and **Heating**.
   - Features a serialized command queue using Coroutines, Channels, and Mutexes. Ensures no parallel writes occur.
   - Optimistic UI pending state locks switches while awaiting GATT confirmation.
   - Implements a strict 2-second Notify response timeout with 1 automatic retry, instantly rolling back the UI and displaying a Snackbar upon verification failure.

5. **Material 3 UI Blueprint (`BmsScreen.kt`, `ControlPanel.kt`, `ConnectionHeader.kt`)**
   - Single-screen Scaffold following Material 3 design guidelines.
   - Status color mapping (`SurfaceVariant` for Disconnected, `Secondary` for Scanning, `Tertiary` for Connecting, `Primary` for Connected).
   - `ConfirmationDialog.kt` enforces safe confirmation prior to every physical hardware change.

---

## Getting Started

### Prerequisites
- Android Studio Iguana / Jellyfish (or newer)
- JDK 17
- Minimum Android SDK: API 26 (Android 8.0)
- Target Android SDK: API 34 (Android 14)

### Building the Project
Open the `BMSQuickLink` directory in Android Studio. Gradle will automatically sync the required AndroidX and Jetpack Compose dependencies. Build and deploy directly to a BLE-capable Android physical device (BLE scanning is not supported on standard emulators).
