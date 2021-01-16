@file:Suppress("MayBeConstant")

object Config {
    val compileSdk = 29
    val minSdk = 21
    val targetSdk = 29
}

object Versions {
    val appCompat = "1.1.0"
    val constraintLayout = "1.1.3"
    val core = "1.2.0"
    val coroutines = "1.3.3"
    val flipper = "0.36.0"
    val fragment = "1.2.4"
    val googleAuth = "17.0.0"
    val googleClient = "1.30.8"
    val googleDrive = "v3-rev110-1.23.0"
    val googleSheets = "v4-rev581-1.25.0"
    val gradleAndroid = "4.1.0"
    val gradleVersions = "0.28.0"
    val kotlin = "1.4.21"
    val material = "1.1.0"
    val navigation = "2.3.0-alpha04"
    val recyclerView = "1.1.0"
    val soLoader = "0.8.2"
    val sqlDelight = "1.4.4"
    val threeTen = "1.2.2"
}

object Plugins {
    val gradleAndroid = "com.android.tools.build:gradle:${Versions.gradleAndroid}"
    val gradleVersions = "com.github.ben-manes:gradle-versions-plugin:${Versions.gradleVersions}"
    val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    val navigationSafeArgs =
        "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.navigation}"
    val sqlDelight = "com.squareup.sqldelight:gradle-plugin:${Versions.sqlDelight}"
}

object Kotlin {
    val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
}

object Android {
    val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
    val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    val core = "androidx.core:core-ktx:${Versions.core}"
    val fragment = "androidx.fragment:fragment-ktx:${Versions.fragment}"
    val navigationFragment = "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
    val navigationUi = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"
    val recyclerView = "androidx.recyclerview:recyclerview:${Versions.recyclerView}"
}

object Libs {
    val flipper = "com.facebook.flipper:flipper:${Versions.flipper}"
    val flipperNoOp = "com.facebook.flipper:flipper-noop:${Versions.flipper}"
    val googleAuth = "com.google.android.gms:play-services-auth:${Versions.googleAuth}"
    val googleClient = "com.google.api-client:google-api-client-android:${Versions.googleClient}"
    val googleDrive = "com.google.apis:google-api-services-drive:${Versions.googleDrive}"
    val googleSheets = "com.google.apis:google-api-services-sheets:${Versions.googleSheets}"
    val material = "com.google.android.material:material:${Versions.material}"
    val soLoader = "com.facebook.soloader:soloader:${Versions.soLoader}"
    val sqlDelightAndroidDriver = "com.squareup.sqldelight:android-driver:${Versions.sqlDelight}"
    val sqlDelightCoroutines =
        "com.squareup.sqldelight:coroutines-extensions-jvm:${Versions.sqlDelight}"
    val sqlDelightSqliteDriver = "com.squareup.sqldelight:sqlite-driver:${Versions.sqlDelight}"
    val threeTen = "com.jakewharton.threetenabp:threetenabp:${Versions.threeTen}"
}

object Tests {
    val assertK = "com.willowtreeapps.assertk:assertk-jvm:0.22"
    val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}"
    val junit = "junit:junit:4.13"
    val mockitoInline = "org.mockito:mockito-inline:2.13.0"
    val mockitoKotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0"
}
