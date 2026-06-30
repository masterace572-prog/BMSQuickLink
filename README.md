# BMS Quick Link & Control (2.0 Redesign)

**Version:** 2.0 (Redesigned Premium Edition)  
**Platform:** Android  
**Language:** Kotlin  
**UI Framework:** Jetpack Compose (Material 3)  
**Design System:** Premium Minimal Flat Palette (Zero Gradients)  
**Backend:** Local SQLite Database (Saved Devices & Audit Logs) + SHA-256 PIN Authentication  
**Communication:** Bluetooth Low Energy (BLE)  
**Architecture:** MVVM + Repository + SQLiteOpenHelper + StateFlow + Coroutines

---

## Product Vision & Objective

**BMS Quick Link & Control** is a lightweight, premium BLE utility designed exclusively for establishing a reliable Bluetooth Low Energy connection with compatible LiFePO4 Battery Management Systems (BMS) and safely controlling four hardware functions.

**Explicitly Excluded:**  
Per the PRD, the application intentionally excludes every monitoring, telemetry, graphing, analytics, configuration, calibration, historical logging, or battery diagnostic feature (no voltage, current, power, battery percentage, cell voltages, charts, temperature graphs, password management, user accounts, cloud synchronization, export/import, data recording, analytics, battery health estimation, capacity calculation, or SOC prediction).

---

## Premium Redesign Features (v2.0)

### 🌟 Design System & UI
- **Zero Gradients:** Solid, premium flat colors (Rich Slate/Charcoal for Dark Mode, Crisp Pure White/Light Gray for Light Mode) offering exceptional contrast and high-end aesthetics.
- **Professional Typography:** Configured with exact letter spacing, line heights, and elegant font weights.
- **Fully Functional Dark/Light Mode Toggle:** Accessible in Settings with dynamic support and beautiful animated Sun/Moon icons.

### 🗄️ Local Backend & Database Operations
- **SHA-256 PIN Security (`AuthManager.kt`):** A custom local PIN keypad setup and locking screen prevents unauthorized physical MOSFET switching.
- **SQLite Database (`BmsDatabaseHelper.kt`):** Manages local tables without external server dependencies or internet permissions:
  - `user_auth`: Securely stores hashed PIN credentials.
  - `saved_devices`: Allows users to add custom nicknames to known MAC addresses for quick connection shortcuts.
  - `audit_logs`: Records a detailed hardware audit history of all switch operations and connection events.

### 🧭 Navigation & User Flows
- **Bottom Navigation Bar (`AppNavigation.kt`, `MainScreen.kt`):**
  - **Controls Tab:** Features a premium connection header, action buttons, clean dividers, and minimal control switch cards.
  - **Saved Devices Tab:** Database CRUD screen displaying saved BMS devices, allowing users to add/edit nicknames, delete devices, and view connection history.
  - **Settings Tab:** Premium settings layout featuring the Dark/Light Mode toggle, PIN lock management, Developer Mode toggle, and recent audit logs view.

---

## System Architecture

```
Presentation Layer (Compose Navigation / AuthScreen / MainScreen)
       ↓
   ViewModel (BmsViewModel / AuthState / Database Flows)
       ↓
  Repository (BmsRepository / BmsDatabaseHelper / AuthManager)
       ↓
 BLE Manager (BleManager / CommandEngine / GATT lifecycle)
       ↓
BluetoothGatt (Android BLE API)
```

---

## Getting Started

### Prerequisites
- Android Studio Iguana / Jellyfish (or newer)
- JDK 17
- Minimum Android SDK: API 26 (Android 8.0)
- Target Android SDK: API 34 (Android 14)

### Building the Project
Open the `BMSQuickLink` directory in Android Studio. Gradle will automatically sync the required AndroidX, Jetpack Compose, and Navigation dependencies. Build and deploy directly to a BLE-capable Android physical device.
