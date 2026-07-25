# iTextG missing classes (optional dependencies)
-dontwarn org.spongycastle.**
-dontwarn com.itextpdf.**

# Keep Room generated code
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.Dao
-keep class * extends androidx.room.Entity

# Keep Compose UI
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.material3.** { *; }

# Keep ML Kit
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.** { *; }
