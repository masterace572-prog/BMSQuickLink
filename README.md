# BMS Quick Link & Control (12.0 Final Premium Pager Edition)

**Version:** 12.0 (Final Premium Welcome Pager Edition)  
**Platform:** Android  
**Language:** Kotlin  
**UI Framework:** Jetpack Compose (Material 3)  
**Design System:** Professional Corporate Palette (Absolute Zero Neon, Zero Pastel Tints, Zero Gradients)  
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

## Senior Android Security Engineer Specs (v12.0 Final)

### 🚀 Premium 2-Screen Welcome Wizard (`OnboardingScreen.kt`)
Designed around a highly elegant, user-friendly `HorizontalPager` flow:
- **Screen 1: Welcome Screen** — Displays a beautiful hero greeting, prominent app branding (`Icons.Default.BluetoothConnected`), and an explicit **"Continue"** button. Tapping "Continue" smoothly slides the pager to the second screen.
- **Screen 2: Features Showcase** — Highlights the core functionalities (Secure BLE Link, Quick Hardware Controls: Charge, Discharge, Auto-Balance, Heating) with crisp monochrome icons and short text descriptions, followed by an explicit **"Get Started"** trigger.
- **AES-256 Persistence Layer (`BmsEncryptedPrefs.kt`):** Tapping "Get Started" securely writes the boolean key `is_onboarding_complete = true` using Android `EncryptedSharedPreferences` backed directly by the Android Keystore (`MasterKey.KeyScheme.AES256_GCM`). The app's main entry point instantly inspects this encrypted key to bypass the Welcome wizard on all subsequent launches.

### 🛡️ Logical Security & User-Device Privacy Layers
To guarantee that the mobile host device remains entirely private, untraceable, and unidentifiable by receiving BMS hardware or external packet sniffers, the application integrates three strict security layers:

#### Layer 1: OS-Level MAC Anonymization (RPA)
- The BLE scanner leverages Android’s native Bluetooth Privacy system by enforcing the use of **Resolvable Private Addresses (RPA)**.
- Configured via low latency scan modes (`SCAN_MODE_LOW_LATENCY`), zero report delay (`setReportDelay(0)`), and strict manifest scanning flags (`neverForLocation`), this mandates that the mobile phone's real physical MAC address is scrambled and dynamically rotated at the OS level during every scan session. External sniffers and BMS hardware cannot log or trace a persistent device signature.

#### Layer 2: Secure Application Sandboxing & Obfuscation (`proguard-rules.pro`)
- Configured with aggressive R8 optimization, repackaging (`-repackageclasses ''`), and advanced code obfuscation.
- All custom BLE connection logic, GATT callback listeners, and manufacturer UUID references are heavily obfuscated to prevent decompilation, reverse-engineering, or local protocol analysis. Production release builds automatically strip all logging calls (`Log.d`, `Log.w`, `Log.e`) to prevent exposing sensitive runtime parameters to logcat.

#### Layer 3: Ephemeral Connection Context
- Ensures no unique device identifiers (such as Android ID, IMEI, or hardware serials) are ever bundled into BLE payload strings sent over the write characteristic.
- All outgoing transmission payloads are limited strictly to standard, generic BMS hardware hex command structures (`0x01`, `0x02`, `0x03`, `0x04`).

---

## Getting Started

### Prerequisites
- Android Studio Iguana / Jellyfish (or newer)
- JDK 17
- Minimum Android SDK: API 26 (Android 8.0)
- Target Android SDK: API 34 (Android 14)

### Building the Project
Open the `BMSQuickLink` directory in Android Studio. Gradle will automatically sync the required AndroidX, Jetpack Compose, Navigation, and Security Crypto dependencies. Build and deploy directly to a BLE-capable Android physical device.
