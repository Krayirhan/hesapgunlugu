// JaCoCo Configuration for Test Coverage
// Apply this to root build.gradle.kts or create as config/jacoco/jacoco.gradle.kts

import org.gradle.testing.jacoco.plugins.JacocoPluginExtension

apply(plugin = "jacoco")

configure<JacocoPluginExtension> {
    toolVersion = "0.8.12"
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")
    
    group = "Reporting"
    description = "Generate Jacoco coverage reports"

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
        
        xml.outputLocation.set(file("${layout.buildDirectory}/reports/jacoco/jacocoTestReport.xml"))
        html.outputLocation.set(file("${layout.buildDirectory}/reports/jacoco/html"))
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R\$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        "**/di/**",
        "**/*_HiltModules*",
        "**/*_Factory*",
        "**/*_MembersInjector*",
        "**/Hilt_*",
        "**/*Dao*", // Exclude Room DAOs (generated code)
        "**/*Database*", // Exclude Room databases
        "**/*_Impl*" // Exclude generated implementations
    )

    val debugTree = fileTree("${layout.buildDirectory}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }

    val mainSrc = "${project.projectDir}/src/main/java"

    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree(layout.buildDirectory) {
        include("jacoco/testDebugUnitTest.exec")
    })
}

tasks.register<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    dependsOn("jacocoTestReport")
    
    group = "Verification"
    description = "Verify code coverage meets minimum thresholds"
    
    val lineMinimum = (project.findProperty("coverageMinimum") as? String)?.toBigDecimal()
        ?: "0.70".toBigDecimal()
    val branchMinimum = (project.findProperty("branchCoverageMinimum") as? String)?.toBigDecimal()
        ?: "0.60".toBigDecimal()

    violationRules {
        rule {
            limit {
                minimum = lineMinimum
            }
        }

        rule {
            element = "CLASS"
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = branchMinimum
            }
        }
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R\$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        "**/di/**",
        "**/*_HiltModules*",
        "**/*_Factory*",
        "**/*_MembersInjector*",
        "**/Hilt_*",
        "**/*Dao*",
        "**/*Database*",
        "**/*_Impl*"
    )

    val debugTree = fileTree("${layout.buildDirectory}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }

    classDirectories.setFrom(files(debugTree))
}

// Optional: Create aggregated report for all modules
tasks.register<JacocoReport>("jacocoAggregatedReport") {
    group = "Reporting"
    description = "Generate aggregated Jacoco coverage report for all modules"
    
    dependsOn(subprojects.mapNotNull { 
        it.tasks.findByName("testDebugUnitTest") 
    })

    reports {
        xml.required.set(true)
        html.required.set(true)
        
        xml.outputLocation.set(file("${layout.buildDirectory}/reports/jacoco/aggregated/jacocoAggregatedReport.xml"))
        html.outputLocation.set(file("${layout.buildDirectory}/reports/jacoco/aggregated/html"))
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R\$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
        "**/di/**",
        "**/*_HiltModules*",
        "**/*_Factory*",
        "**/*_MembersInjector*",
        "**/Hilt_*",
        "**/*Dao*",
        "**/*Database*",
        "**/*_Impl*"
    )

    val sourceDirs = subprojects.flatMap { project ->
        listOf("${project.projectDir}/src/main/java")
    }

    val classDirs = subprojects.mapNotNull { project ->
        val buildDir = project.layout.buildDirectory.get().asFile
        fileTree("$buildDir/tmp/kotlin-classes/debug") {
            exclude(fileFilter)
        }
    }

    val executionDataPaths = subprojects.flatMap { project ->
        val buildDir = project.layout.buildDirectory.get().asFile
        fileTree(buildDir) {
            include("jacoco/testDebugUnitTest.exec")
        }.files
    }

    sourceDirectories.setFrom(sourceDirs)
    classDirectories.setFrom(classDirs)
    executionData.setFrom(executionDataPaths)
}
