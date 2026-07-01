# BMS Quick Link & Control (13.0 Ultimate Floating Masterpiece Edition)

**Version:** 13.0 (Ultimate Floating Masterpiece Edition)  
**Platform:** Android  
**Language:** Kotlin  
**UI Framework:** Jetpack Compose (Material 3)  
**Design System:** Masterpiece Floating Dock + Real-Time Customization Engine (Themes, Palettes, Card/Corner Styles)  
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

## Ultimate Masterpiece Specs (v13.0 Final)

### 🌟 Jaw-Dropping UI/UX Design System
To deliver a world-class, ultra-premium experience that feels like a flagship tech/corporate dashboard, the entire application has been transformed with cutting-edge visual design:
- **Spectacular Floating Navigation Dock (`MainScreen.kt`):** Transformed the standard bottom navigation bar into a spectacularly modern floating dock (modeled after the latest high-end dynamic interfaces). It hovers elegantly above the background with `RoundedCornerShape(36.dp)`, custom pill indicator highlights, and soft natural drop shadows.
- **Jaw-Dropping Connection Dashboard (`ConnectionHeader.kt`):** A masterpiece header card featuring a large 64dp dedicated square icon container, prominent MAC address presentation, and an explicit live RSSI signal meter bar (representing physical connection strength via 4 clean vertical pill bars).
- **Left Accent Indicator Bars (`ControlPanel.kt`):** Hardware switches now feature an elegant 6dp left accent indicator bar, explicit status tags (`ACTIVE` / `OFF`), and custom animated content expansion.
- **View-Only Dimmed Controls (`ControlsTab.kt`):** When disconnected, the app keeps the actual control switches fully visible in a beautifully dimmed, unclickable view-only mode accompanied by an elegant professional info banner.
- **High-End 2-Screen Welcome Wizard (`OnboardingScreen.kt`):** An intuitive sliding `HorizontalPager` flow featuring a 110dp branding container (`Icons.Default.BluetoothConnected`), animated indicator dots, and dynamic action buttons ("Continue" on Page 1 sliding to Page 2, "Get Started" on Page 2).

### 🎨 Fully Functional Real-Time Appearance Console (`AppearanceScreen.kt`, `Theme.kt`)
Accessible instantly in the Settings screen, the Appearance Console provides full-featured, highly intuitive customization that instantly re-themes the entire application in real-time:
- **3-Way Theme Mode Selection:** Easily toggle between **Dark Mode**, **Light Mode**, and **System Default**.
- **Dynamic Accent Swatch Palette:** Instantly change the app's solid accent color across active switches, primary buttons, floating navigation indicators, and badge pills. Choose from an elite palette of 6 striking pairings: `Corporate Blue`, `Corporate Green`, `Corporate Orange`, `Corporate Red`, `Corporate Teal`, and `Corporate Purple`.
- **3-Way Card Style Engine (`LocalCardStyle`):** Change the architectural appearance of cards across the entire application instantly (`Solid Clean`, `Border Outlined`, `Translucent`).
- **3-Way Card Corner Style Engine (`LocalCornerStyle`):** Adjust the physical corner rounding of cards across the entire app instantly (`Classic`, `Sharp`, `Soft`).
- **Corporate Footer (`SettingsTab.kt`):** Features a clean, professional app footer text centered beautifully above the floating navigation dock:
  ```text
  BMS Quick Link & Control v12.0
  Developed by Anoy
  ```

---

## Getting Started

### Prerequisites
- Android Studio Iguana / Jellyfish (or newer)
- JDK 17
- Minimum Android SDK: API 26 (Android 8.0)
- Target Android SDK: API 34 (Android 14)

### Building the Project
Open the `BMSQuickLink` directory in Android Studio. Gradle will automatically sync the required AndroidX, Jetpack Compose, Navigation, and Security Crypto dependencies. Build and deploy directly to a BLE-capable Android physical device.
