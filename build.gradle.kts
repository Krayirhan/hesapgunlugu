import org.gradle.api.GradleException
import org.gradle.api.artifacts.ProjectDependency

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.hilt.android) apply false

    // Baseline Profile - Enabled for startup optimization
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.baselineprofile) apply false

    // Static Code Analysis - using version catalog
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.ktlint) apply false

    // Screenshot Testing
    id("app.cash.paparazzi") version "1.3.4" apply false

    
}

// Detekt configuration for all modules
subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        config.setFrom(files("${rootProject.projectDir}/config/detekt/detekt.yml"))
        buildUponDefaultConfig = true
        allRules = false
        parallel = true
    }

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        android.set(true)
        ignoreFailures.set(false)
        outputToConsole.set(true)
    }
}

tasks.register("projectDependencyReport") {
    group = "verification"
    description = "Generate project dependency graph and fail on cycles."

    val outputFile = layout.buildDirectory.file("reports/dependency-graph/project-dependencies.txt")

    doLast {
        val graph = mutableMapOf<String, Set<String>>()

        rootProject.subprojects.forEach { project ->
            val deps = mutableSetOf<String>()
            listOf("api", "implementation", "compileOnly").forEach { configName ->
                project.configurations.findByName(configName)
                    ?.dependencies
                    ?.withType(ProjectDependency::class.java)
                    ?.forEach { deps.add(it.dependencyProject.path) }
            }
            graph[project.path] = deps
        }

        val report = buildString {
            graph.toSortedMap().forEach { (project, deps) ->
                append(project)
                append(" -> ")
                append(deps.sorted().joinToString(", "))
                append("\n")
            }
        }

        val file = outputFile.get().asFile
        file.parentFile.mkdirs()
        file.writeText(report)

        val visiting = mutableSetOf<String>()
        val visited = mutableSetOf<String>()
        val cycles = mutableListOf<List<String>>()

        fun dfs(node: String, stack: MutableList<String>) {
            if (node in visiting) {
                cycles.add(stack + node)
                return
            }
            if (node in visited) return

            visiting.add(node)
            stack.add(node)
            graph[node].orEmpty().forEach { dfs(it, stack) }
            stack.removeAt(stack.size - 1)
            visiting.remove(node)
            visited.add(node)
        }

        graph.keys.forEach { if (it !in visited) dfs(it, mutableListOf()) }

        if (cycles.isNotEmpty()) {
            val message = cycles.joinToString("\n") { it.joinToString(" -> ") }
            throw GradleException("Project dependency cycles detected:\n$message")
        }
    }
}
