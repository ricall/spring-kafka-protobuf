import com.google.protobuf.gradle.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	idea
	id("org.springframework.boot") version "2.3.2.RELEASE"
	id("io.spring.dependency-management") version "1.0.9.RELEASE"
	id("com.google.protobuf") version "0.8.12"
	kotlin("jvm") version "1.3.72"
	kotlin("plugin.spring") version "1.3.72"
	kotlin("plugin.noarg") version "1.3.72"
	kotlin("plugin.allopen") version "1.3.72"
}

group = "au.com.rma"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

sourceSets{
	create("protobuf") {
		proto {
			srcDir("src/main/proto")
		}
	}
}

configurations {
	compile.get().extendsFrom(configurations.protobuf)
}

allOpen {
	annotation("au.com.rma.test.annotation.ModelObject")
}

noArg {
	annotation("au.com.rma.test.annotation.ModelObject")
}

dependencies {
	implementation("com.google.protobuf:protobuf-java:3.6.1")
	implementation("io.grpc:grpc-stub:1.15.1")
	implementation("io.grpc:grpc-protobuf:1.15.1")

	implementation("io.projectreactor.kafka:reactor-kafka:1.2.2.RELEASE")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.apache.kafka:kafka-streams")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("org.springframework.kafka:spring-kafka")

	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.springframework.kafka:spring-kafka-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}

protobuf {
	protoc {
		// The artifact spec for the Protobuf Compiler
		artifact = "com.google.protobuf:protoc:3.6.1"
	}
	plugins {
		// Optional: an artifact spec for a protoc plugin, with "grpc" as
		// the identifier, which can be referred to in the "plugins"
		// container of the "generateProtoTasks" closure.
		id("grpc") {
			artifact = "io.grpc:protoc-gen-grpc-java:1.15.1"
		}
	}
	generateProtoTasks {
		ofSourceSet("main").forEach {
			it.plugins {
				// Apply the "grpc" plugin whose spec is defined above, without options.
				id("grpc") { }
			}
		}
	}
}