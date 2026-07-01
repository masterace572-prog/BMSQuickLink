# BMS Quick Link & Control (15.0 Ultimate Native Masterpiece Edition)

**Version:** 15.0 (Ultimate Native Masterpiece Edition)  
**Platform:** Android  
**Language:** Kotlin  
**UI Framework:** Jetpack Compose (Material 3)  
**Design System:** Masterpiece Floating Dock + Real-Time Telemetry Dashboard + Live Scan Terminal  
**Security:** Resolvable Private Addresses (RPA) + EncryptedSharedPreferences (AES-256) + R8 Sandboxing  
**Backend:** Local SQLite Database (Connected-Only Audit Logs)  
**Communication:** Bluetooth Low Energy (BLE)  
**Architecture:** MVVM + Repository + StateFlow + Coroutines

---

## Product Vision & Objective

**BMS Quick Link & Control** is an elite, lightweight BLE utility designed exclusively for establishing a reliable Bluetooth Low Energy connection with compatible LiFePO4 Battery Management Systems (BMS) and safely controlling four hardware functions.

**Explicitly Excluded:**  
Per the PRD, the application intentionally excludes every form of account cloud synchronization, export/import, data recording, analytics, or background tracking to ensure maximum security and simplicity.

---

## Ultimate Native Masterpiece Specs (v15.0 Final)

### 🌟 Comprehensive 5-Tab Architectural Suite (`MainScreen.kt`)
The application has been expanded into a complete, flagship-tier smart hardware dashboard featuring an elite floating navigation dock (`Link`, `Dashboard`, `Analytics`, `Controls`, `Settings`):

#### 1. Link Tab (`ConnectionTab.kt`)
- Dedicated strictly to ambient BLE radar scanning and automated connection lifecycle management.
- **Jaw-Dropping Header:** Features a large 64dp square icon container, prominent MAC address presentation, and an explicit live RSSI signal meter bar (representing physical connection strength via 4 clean vertical pill bars).
- **Spectacular Real Terminal Console:** Displays an automated, timestamped console stream tracking the entire BLE scanning lifecycle, active device discoveries, GATT connection state callbacks, MTU 247 negotiation, and service discovery. Includes an explicit "Clear Logs" icon trigger on the classic macOS/Ubuntu window header bar (`bash - bms-quicklink-core ~ 80x24`).

#### 2. Dashboard Tab (`DashboardTab.kt`)
- **Large Circular Progress Indicator:** Displays real-time State of Charge (SOC) percentage (`socPercentage`).
- **Live Readout Row:** Real-time metrics for total Voltage (V), Current (A, positive for charging, negative for discharging), and Power (W).
- **Operating State Banner:** Dynamic full-width status banners reflecting `SYSTEM STANDBY`, `CHARGING ACTIVE`, `DISCHARGING (LOAD ACTIVE)`, or `SYSTEM FAULT LOCK`.

#### 3. Analytics Tab (`AnalyticsTab.kt`)
- **Delta Highlights Banner:** Instant mathematical readout of the highest cell voltage, lowest cell voltage, and the delta voltage gap.
- **Individual Cell Voltages Grid:** A beautiful 16-cell grid view dynamically highlighting the highest cell in blue and lowest cell in orange!
- **Diagnostic Sensors Row:** Evaluates hardware health with live readouts of MOSFET temperature, ambient temperature, battery cycle count, and battery health percentage.

#### 4. Controls Tab (`ControlsTab.kt`)
- **Interactive Switch Panel:** Manages four core physical hardware switches (**Charge MOSFET**, **Discharge MOSFET**, **Auto Balance**, and **Heating**) with strict confirmation dialogs and optimistic pending locks.
- **Safe View-Only Mode:** When disconnected, the actual control switches remain fully visible on the screen in a beautifully dimmed, unclickable view-only mode accompanied by an elegant professional info banner.
- **PIN Protected Safety Parameters View:** A secure configuration page displaying hardware safety limits (e.g., Cut-off voltage per cell, high-temperature thresholds) unlocked via an interactive 6-digit PIN step.

#### 5. Settings Tab (`SettingsTab.kt`)
- Houses the Appearance Console (3-Way Theme Mode, 6 Accent Swatches, Card/Corner Styles), Developer Profile, Legal copy, Verification Timeout customizer, and connected-only SQLite Audit Logs.

### 🛡️ Senior Android Security Engineer Layers
- **Layer 1: OS-Level MAC Anonymization (RPA):** Enforces low latency scan modes (`SCAN_MODE_LOW_LATENCY`), zero report delay (`setReportDelay(0)`), and strict manifest scanning flags (`neverForLocation`), mandating that the mobile phone's real physical MAC address is scrambled and dynamically rotated at the OS level during every scan session.
- **Layer 2: Secure Application Sandboxing & Obfuscation (`proguard-rules.pro`):** Configured with aggressive R8 optimization, repackaging (`-repackageclasses ''`), and advanced code obfuscation to block decompilation, reverse-engineering, or local protocol analysis.
- **Layer 3: Ephemeral Connection Context:** Ensures no unique device identifiers (Android ID, IMEI, hardware serials) are ever bundled into BLE payload strings sent over the write characteristic.
- **AES-256 Persistence Layer (`BmsEncryptedPrefs.kt`):** Tapping "Get Started" in the Welcome wizard securely writes the boolean key `is_onboarding_complete = true` using Android `EncryptedSharedPreferences` backed directly by the Android Keystore (`MasterKey.KeyScheme.AES256_GCM`).

---

## Getting Started

### Prerequisites
- Android Studio Iguana / Jellyfish (or newer)
- JDK 17
- Minimum Android SDK: API 26 (Android 8.0)
- Target Android SDK: API 34 (Android 14)

### Building the Project
Open the `BMSQuickLink` directory in Android Studio. Gradle will automatically sync the required AndroidX, Jetpack Compose, Navigation, and Security Crypto dependencies. Build and deploy directly to a BLE-capable Android physical device.
