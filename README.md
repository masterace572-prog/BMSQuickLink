# BMS Quick Link & Control (3.0 Ultra-Premium Edition)

**Version:** 3.0 (Ultra-Premium Redesign)  
**Platform:** Android  
**Language:** Kotlin  
**UI Framework:** Jetpack Compose (Material 3)  
**Design System:** Ultra-Premium Minimal Flat Palette (Obsidian Dark Mode / Arctic Light Mode)  
**Backend:** Local SQLite Database (Saved Devices & Connected-Only Audit Logs)  
**Communication:** Bluetooth Low Energy (BLE)  
**Architecture:** MVVM + Repository + SQLiteOpenHelper + StateFlow + Coroutines

---

## Product Vision & Objective

**BMS Quick Link & Control** is an ultra-premium, lightweight BLE utility designed exclusively for establishing a reliable Bluetooth Low Energy connection with compatible LiFePO4 Battery Management Systems (BMS) and safely controlling four hardware functions.

**Explicitly Excluded:**  
Per the PRD, the application intentionally excludes every monitoring, telemetry, graphing, analytics, configuration, calibration, historical logging, or battery diagnostic feature (no voltage, current, power, battery percentage, cell voltages, charts, temperature graphs, password management, user accounts, cloud synchronization, export/import, data recording, analytics, battery health estimation, capacity calculation, or SOC prediction).

---

## Premium Architecture & UI Specifications (v3.0)

### 🌟 Ultra-Premium Design System & UI
- **Obsidian & Arctic Palettes (Zero Gradients):** High-end solid flat colors (Deep Obsidian `0xFF0A0B0E` for Dark Mode, Pristine Arctic `0xFFF8FAFC` for Light Mode) offering breathtaking contrast and professional architectural aesthetics.
- **High-End Components:** Designed with expansive 24dp/28dp rounded corners, elegant status badge pill tags, custom icon background highlights, and generous layout padding.
- **Professional Typography:** Configured with precise letter spacing, custom line heights, and refined font weights across all Material 3 components.
- **Fully Functional Dark/Light Mode Toggle:** Accessible in Settings with dynamic real-time state support and animated Sun/Moon icons.

### 🗄️ Local Backend & Connected-Only Logging (`BmsDatabaseHelper.kt`, `BmsRepository.kt`)
- **Connected-Only Audit Logging:** Per user specification, audit logs are strictly reserved for successfully connected devices. Toggles and active connections are recorded to the local database table (`audit_logs`) while ambient scanning or disconnect events are completely filtered out.
- **SQLite Database (`BmsDatabaseHelper.kt`):** Manages local tables without external server dependencies or internet permissions:
  - `saved_devices`: Allows users to attach custom nicknames to known MAC addresses for fast connection shortcuts.
  - `audit_logs`: Generates an automated audit history of all physical switch operations (e.g., `CHARGE_TOGGLE_ON`, `DISCHARGE_TOGGLE_OFF`, `CONNECT`) for transparency and verification logging.

### 🧭 Navigation & User Flows
- **Bottom Navigation Bar (`MainScreen.kt`):**
  - **Controls Tab:** Features a premium connection header, action buttons, clean dividers, and minimal control switch cards.
  - **Saved Profiles Tab:** Database CRUD screen displaying saved BMS devices, allowing users to add/edit nicknames, delete devices, and view connection history.
  - **Settings Tab:** Premium settings layout featuring the Dark/Light Mode toggle, Developer Mode toggle, and scrollable recent hardware audit logs view.

---

## Getting Started

### Prerequisites
- Android Studio Iguana / Jellyfish (or newer)
- JDK 17
- Minimum Android SDK: API 26 (Android 8.0)
- Target Android SDK: API 34 (Android 14)

### Building the Project
Open the `BMSQuickLink` directory in Android Studio. Gradle will automatically sync the required AndroidX, Jetpack Compose, and Navigation dependencies. Build and deploy directly to a BLE-capable Android physical device.
