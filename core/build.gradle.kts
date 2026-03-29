// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    id("org.sonarqube") version "5.0.0.4638"
}

sonar {
    properties {
        property("sonar.projectKey", "Kotlin")
        property("sonar.qualitygate.wait", "true")
        property("sonar.coverage.jacoco.xmlReportPaths", "${project.projectDir}/app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
        property("sonar.junit.reportPaths", "${project.projectDir}/app/build/test-results/testDebugUnitTest")
        property("sonar.exclusions", "**/presentation/**/*.kt,**/*Activity.kt,**/*Application.kt,**/components/**/*.kt")
        property("sonar.coverage.exclusions", "**/presentation/**/*.kt,**/*Activity.kt,**/*Application.kt,**/components/**/*.kt,**/*Screen.kt")
    }
}