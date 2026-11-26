import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}
dependencies {
    implementation(project(":shared"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.koin.android)
    implementation(libs.koin.core)
    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

fun getSigningProp(gradleProp: String, propKey: String, envVar: String): String? {
    return keystoreProperties.getProperty(propKey)
        ?: providers.gradleProperty(gradleProp).orElse(providers.environmentVariable(envVar)).orNull
}

val storeFileStr = getSigningProp("unibook.signing.storeFile", "storeFile", "ANDROID_KEYSTORE_PATH")
val storePasswordStr = getSigningProp("unibook.signing.storeFile", "storePassword", "ANDROID_KEYSTORE_PASSWORD") // wait, storePassword key
// let's correct storePassword key in keystoreProperties:
val storePasswordStrCorrect = getSigningProp("unibook.signing.storePassword", "storePassword", "ANDROID_KEYSTORE_PASSWORD")
val keyAliasStr = getSigningProp("unibook.signing.keyAlias", "keyAlias", "ANDROID_KEY_ALIAS")
val keyPasswordStr = getSigningProp("unibook.signing.keyPassword", "keyPassword", "ANDROID_KEY_PASSWORD")

val releaseSigningValues = listOf(storeFileStr, storePasswordStrCorrect, keyAliasStr, keyPasswordStr)

android {
    namespace = "com.leandro.unibook"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.leandro.unibook"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    signingConfigs {
        if (releaseSigningValues.all { !it.isNullOrBlank() }) {
            create("release") {
                storeFile = file(storeFileStr!!)
                storePassword = storePasswordStrCorrect
                keyAlias = keyAliasStr
                keyPassword = keyPasswordStr
            }
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfigs.findByName("release")?.let { signingConfig = it }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}