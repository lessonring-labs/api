#!/bin/bash

set -e

BASE_PACKAGE_DIR="src/main/java/com/lessonring/api"
RESOURCE_DIR="src/main/resources"
GRADLE_FILE="build.gradle"
YML_FILE="$RESOURCE_DIR/application.yml"

mkdir -p "$BASE_PACKAGE_DIR/common/config"
mkdir -p "$BASE_PACKAGE_DIR/common/entity"
mkdir -p "$BASE_PACKAGE_DIR/studio/domain"
mkdir -p "$BASE_PACKAGE_DIR/studio/infrastructure/persistence"
mkdir -p "$RESOURCE_DIR/db/migration"

cat > "$GRADLE_FILE" <<'EOG'
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.5.0'
    id 'io.spring.dependency-management' version '1.1.7'
}

group = 'com.lessonring'
version = '0.0.1-SNAPSHOT'

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

repositories {
    mavenCentral()
}

ext {
    querydslVersion = "5.1.0"
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'

    runtimeOnly 'org.postgresql:postgresql:42.7.5'

    implementation 'org.flywaydb:flyway-core'
    implementation 'org.flywaydb:flyway-database-postgresql'

    implementation "com.querydsl:querydsl-jpa:${querydslVersion}:jakarta"
    annotationProcessor "com.querydsl:querydsl-apt:${querydslVersion}:jakarta"
    annotationProcessor "jakarta.annotation:jakarta.annotation-api"
    annotationProcessor "jakarta.persistence:jakarta.persistence-api"

    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'

    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

tasks.named('test') {
    useJUnitPlatform()
}
EOG

cat > "$YML_FILE" <<'EOG'
spring:
  application:
    name: lessonring-api

  datasource:
    url: jdbc:postgresql://localhost:5432/lessonring
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false
    properties:
      hibernate:
        format_sql: true
        jdbc:
          time_zone: Asia/Seoul
    show-sql: true

  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration

logging:
  level:
    org.hibernate.SQL: debug
    org.hibernate.orm.jdbc.bind: trace
EOG

cat > "$BASE_PACKAGE_DIR/common/entity/BaseEntity.java" <<'EOG'
package com.lessonring.api.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(updatable = false)
    private Long createdBy;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @LastModifiedBy
    private Long updatedBy;
}
EOG

cat > "$BASE_PACKAGE_DIR/common/config/JpaAuditingConfig.java" <<'EOG'
package com.lessonring.api.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
EOG

cat > "$BASE_PACKAGE_DIR/common/config/AuditorAwareConfig.java" <<'EOG'
package com.lessonring.api.common.config;

import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class AuditorAwareConfig {

    @Bean
    public AuditorAware<Long> auditorProvider() {
        return () -> Optional.of(0L);
    }
}
EOG

cat > "$BASE_PACKAGE_DIR/common/config/QueryDslConfig.java" <<'EOG'
package com.lessonring.api.common.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueryDslConfig {

    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}
EOG

cat > "$BASE_PACKAGE_DIR/studio/domain/StudioStatus.java" <<'EOG'
package com.lessonring.api.studio.domain;

public enum StudioStatus {
    ACTIVE,
    INACTIVE,
    CLOSED
}
EOG

cat > "$BASE_PACKAGE_DIR/studio/domain/Studio.java" <<'EOG'
package com.lessonring.api.studio.domain;

import com.lessonring.api.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "studio")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Studio extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String phone;

    private String address;

    private String detailAddress;

    private String timezone;

    private String businessNumber;

    @Enumerated(EnumType.STRING)
    private StudioStatus status;
}
EOG

cat > "$BASE_PACKAGE_DIR/studio/infrastructure/persistence/StudioJpaRepository.java" <<'EOG'
package com.lessonring.api.studio.infrastructure.persistence;

import com.lessonring.api.studio.domain.Studio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudioJpaRepository extends JpaRepository<Studio, Long> {
}
EOG

cat > "$RESOURCE_DIR/db/migration/V1__init.sql" <<'EOG'
CREATE TABLE studio (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    address VARCHAR(255) NOT NULL,
    detail_address VARCHAR(255),
    timezone VARCHAR(50) NOT NULL,
    business_number VARCHAR(30),
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    created_by BIGINT,
    updated_at TIMESTAMP NOT NULL,
    updated_by BIGINT
);
EOG

echo "생성 완료"
