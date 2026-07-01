# Layer 2: Secure Application Sandboxing & Advanced R8 Code Obfuscation
-repackageclasses ''
-allowaccessmodification
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*

-keepattributes SourceFile,LineNumberTable
-keep class com.bms.quicklink.MainActivity { *; }
-keep class com.bms.quicklink.MainApplication { *; }

# Heavily obfuscate custom BLE connection logic, callback listeners, and manufacturer UUID references
-keep,allowobfuscation class com.bms.quicklink.ble.** { *; }
-keep,allowobfuscation class com.bms.quicklink.data.SwitchType { *; }
-keep,allowobfuscation class com.bms.quicklink.data.SwitchState { *; }
-keep,allowobfuscation class com.bms.quicklink.data.BmsRepository { *; }

# Prevent logging of sensitive connection details or hardware signatures in production releases
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

-dontwarn com.bms.quicklink.**
-dontwarn androidx.security.crypto.**
