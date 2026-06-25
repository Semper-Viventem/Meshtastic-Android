/*
 * Copyright (c) 2026 Meshtastic LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
plugins {
    alias(libs.plugins.meshtastic.kmp.feature)
    alias(libs.plugins.meshtastic.kotlinx.serialization)
}

kotlin {
    android { withHostTest { isIncludeAndroidResources = true } }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.collections.immutable)
            implementation(projects.core.data)
            implementation(projects.core.database)
            implementation(projects.core.datastore)
            implementation(projects.core.model)
            implementation(projects.core.navigation)
            implementation(projects.core.prefs)
            implementation(libs.meshtastic.protobufs)
            implementation(projects.core.service)
            implementation(projects.core.resources)
            implementation(projects.core.ui)
            implementation(projects.core.di)
        }
        commonTest.dependencies {
            implementation(libs.compose.multiplatform.ui.test)
        }
        // Skiko's platform native runtime for the JVM/desktop Compose UI test (runComposeUiTest uses
        // Skiko to render). compose.ui:ui-test brings only the common API, not the per-OS native, so
        // the test fails with "Cannot find libskiko-linux-x64.so". 0.144.6 == the Skiko version for
        // compose-multiplatform 1.11.1 (see root build.gradle.kts). JVM-only (not in commonTest, which
        // would leak it onto the iOS test classpath).
        jvmTest.dependencies {
            implementation("org.jetbrains.skiko:skiko-awt-runtime-linux-x64:0.144.6")
        }
    }
}
