plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.mcal.mcpelauncher"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    ndkVersion = "26.1.10909125"

    defaultConfig {
        applicationId = "com.mcal.mcpelauncher"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"

        ndk {
            abiFilters.addAll(
                setOf(
                    "arm64-v8a",
                )
            )
        }
    }
    externalNativeBuild {
        ndkBuild {
            path = File("src/main/cpp/Android.mk")
        }
    }
    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    packaging {
        resources {
            excludes += listOf(
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/license.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/notice.txt",
                "META-INF/ASL2.0",
                "META-INF/INDEX.LIST",
                "META-INF/*.kotlin_module"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
    buildFeatures {
        compose = true
        viewBinding = false
        buildConfig = true
    }
    composeOptions {
    }
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
    configurations {
        all {
            exclude(group = "xpp3", module = "xpp3")
        }
    }
}

dependencies {
    implementation(project(":minecraft"))
    implementation(project(":substrate"))
    implementation(project(":xhook"))
    implementation(project(":httpclient"))
    implementation(project(":microsoft:xal"))
    implementation(project(":microsoft:xbox"))
    implementation(project(":fmod"))

    implementation(libs.androidx.games.activity)
    implementation(libs.androidx.legacy.support.v4)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.material.icons.extended)

    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.activity.compose)
    implementation(libs.material3)
    implementation(libs.androidx.tv.material)
    debugImplementation(libs.ui.tooling)
    implementation(libs.ui.tooling.preview)

    implementation(libs.androidx.annotation)
    implementation(libs.material)

    implementation(libs.gson)
    implementation(libs.annotations)
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.commons.io)
    implementation(libs.conscrypt.android)
}

configurations.all {
    exclude(group = "net.sf.kxml", module = "kxml2")
    exclude(group = "xpp3", module = "xpp3")
}
