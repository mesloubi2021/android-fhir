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

package com.google.android.fhir.knowledge.npm

import com.google.android.fhir.knowledge.FhirNpmPackage

/** Downloads Npm package from the provided package server. */
fun interface PackageDownloader {

  /** Downloads the [fhirNpmPackage] from the [packageServerUrl]. */
  suspend fun downloadPackage(
    fhirNpmPackage: FhirNpmPackage,
    packageServerUrl: String,
  ): LocalFhirNpmPackageMetadata
}
