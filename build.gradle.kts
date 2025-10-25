plugins {
    id("java")
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.5"
}

group = "com.overtheinfinite.splittodo"
version = "1.0-SNAPSHOT"

java {
    // 17 버전 이상 권장
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    // 💡 JPA와 DB 접근을 위한 핵심 의존성 (유지)
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // 💡 REST API 개발을 위한 Spring Boot Web Starter 의존성 추가
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    // 💡 MariaDB JDBC 드라이버 추가 (SQLite 드라이버 대신 이것을 사용)
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
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