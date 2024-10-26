plugins {
	id("org.springframework.boot") version "3.3.3"
	id("io.spring.dependency-management") version "1.1.5"
	kotlin("jvm") version "1.9.24"
	kotlin("plugin.spring") version "1.9.24"
	kotlin("kapt") version "1.9.24"
	kotlin("plugin.jpa") version "1.9.24"
}

group = "com.petcaresuite"
version = "0.1"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
	// Shared Library
	maven {
		url = uri("file://${rootProject.projectDir}/../library-project/build/repos")
	}
}

extra["springCloudVersion"] = "2023.0.3"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
	implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.1.3")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.0")
	implementation("org.mapstruct:mapstruct:1.6.0.Beta2")
	implementation("jakarta.validation:jakarta.validation-api:3.1.0")
	implementation("com.google.guava:guava:33.2.1-jre")
	implementation("org.springframework.kafka:spring-kafka:3.2.4")

	kapt ("org.mapstruct:mapstruct-processor:1.4.2.Final")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
	compileOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("org.springframework.boot:spring-boot-devtools")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

// Pass system properties to the bootRun task
tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
	systemProperties = System.getProperties().mapKeys { it.key.toString() }
}

tasks.withType<JavaCompile> {
	options.forkOptions.memoryMaximumSize = "4g"
}