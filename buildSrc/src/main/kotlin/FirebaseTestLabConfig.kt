/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.android.build.api.dsl.LibraryExtension
import com.osacky.flank.gradle.FlankGradleExtension
import java.util.UUID
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

fun Project.configureFirebaseTestLabForLibraries() {
  apply(plugin = Plugins.BuildPlugins.fladle)
  configure<FlankGradleExtension> {
    commonConfigurationForFirebaseTestLab(this@configureFirebaseTestLabForLibraries)
    instrumentationApk.set(project.provider { "$buildDir/outputs/apk/androidTest/debug/*.apk" })
    environmentVariables.set(
      mapOf(
        "coverage" to "true",
        "coverageFilePath" to "/sdcard/Download/",
        "clearPackageData" to "true",
      ),
    )
    flakyTestAttempts.set(3)
    devices.set(
      listOf(
        mapOf(
          "model" to "Nexus6P",
          "version" to
            "${project.extensions.getByType(LibraryExtension::class.java).defaultConfig.minSdk}",
          "locale" to "en_US",
        ),
        mapOf(
          "model" to "panther",
          "version" to "${project.extensions.getByType(LibraryExtension::class.java).compileSdk}",
          "locale" to "en_US",
        ),
      ),
    )
  }
}

fun Project.configureFirebaseTestLabForMicroBenchmark() {
  apply(plugin = Plugins.BuildPlugins.fladle)
  configure<FlankGradleExtension> {
    commonConfigurationForFirebaseTestLab(this@configureFirebaseTestLabForMicroBenchmark)
    instrumentationApk.set(project.provider { "$buildDir/outputs/apk/androidTest/release/*.apk" })
    environmentVariables.set(
      mapOf(
        "additionalTestOutputDir" to "/sdcard/Download",
        "no-isolated-storage" to "true",
        "clearPackageData" to "true",
      ),
    )
    // some of the benchmark tests get timed-out in the default 15m
    testTimeout.set("45m")
    devices.set(
      listOf(
        mapOf(
          "model" to "panther",
          "version" to "${project.extensions.getByType(LibraryExtension::class.java).compileSdk}",
          "locale" to "en_US",
        ),
      ),
    )
  }
}

private fun FlankGradleExtension.commonConfigurationForFirebaseTestLab(project: Project) {
  projectId.set("android-fhir-instrumeted-tests")
  debugApk.set(
    project.provider {
      "${project.rootDir}/demo/build/outputs/apk/androidTest/debug/demo-debug-androidTest.apk"
    },
  )
  useOrchestrator.set(true)
  maxTestShards.set(20)
  directoriesToPull.set(listOf("/sdcard/Download"))
  resultsBucket.set("android-fhir-build-artifacts")
  resultsDir.set(
    if (project.providers.environmentVariable("KOKORO_BUILD_ARTIFACTS_SUBDIR").isPresent) {
      "${System.getenv("KOKORO_BUILD_ARTIFACTS_SUBDIR")}/firebase/${project.name}"
    } else {
      "${project.name}-${UUID.randomUUID()}"
    },
  )
}
