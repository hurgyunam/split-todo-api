plugins {
    id("java")
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.5"
}

group = "org.example"
version = "1.0-SNAPSHOT"

java {
    // 17 버전 이상 권장
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    // 💡 REST API 개발을 위한 Spring Boot Web Starter 의존성 추가
    implementation("org.springframework.boot:spring-boot-starter-web")
    // 💡 개발 편의를 위한 Lombok (선택 사항)
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    // 💡 Spring Boot 테스트 의존성 추가
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}