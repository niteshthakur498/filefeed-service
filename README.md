# Filefeed Service POC

## Overview
`filefeed-service` is a Spring-based app for file uploads (PDF, XLSX, JSON, TXT, XLS) using R2DBC with H2, Liquibase for migrations, and health/metrics endpoints.

## Key Features
- **File Uploads**: Supports PDF, XLSX, JSON, TXT, XLS.
- **Database**: R2DBC with embedded H2.
- **Migration**: Liquibase for DB migrations.
- **Monitoring**: Health and Prometheus metrics endpoints.

## Configuration

### File Upload
```yaml
spring:
  servlet:
    webflux:
      multipart:
        enabled: true
        max-file-size: 10MB
```

### Database (R2DBC + H2)
```yaml
spring:
  r2dbc:
    url: r2dbc:h2:file:///./data/h2db/testdb
```

### Liquibase
```yaml
spring:
  liquibase:
    enabled: true
    change-log: classpath:/db/changelog/db.changelog-master.yaml
```

### Supported Formats
```yaml
file-feed:
  supported-formats:
    - pdf
    - xlsx
    - json
    - txt
    - xls
```

### Management Endpoints
```yaml
management:
  endpoints:
    web:
      exposure:
        include: "*"
  metrics:
    export:
      prometheus:
        enabled: true
```

## Running
1. **Build** (Maven).
2. **Run**:
   - Maven: `mvn spring-boot:run`
4. **Health**: [localhost:8080/actuator/health](http://localhost:8080/actuator/health)  
5. **Metrics**: [localhost:8080/actuator/prometheus](http://localhost:8080/actuator/prometheus)

## License
MIT License. See [LICENSE](LICENSE).