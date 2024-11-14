plugins {
    id("com.android.library")
    id("kotlin-android")
    id("maven-publish")
}

android {
    namespace = "com.customwebviewtab.custom_tab_webview_library"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
        targetSdk = 35
        version = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.sudarshan7270"
                artifactId = "customTabWebview"
                version = "1.0.0"
            }
        }
 repositories {
            maven {
                url = uri("https://jitpack.io")
            }
        }
    }
}
