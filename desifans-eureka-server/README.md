# OnlyFans Clone - Discovery Service (Eureka Server)

## Overview
This is the Discovery Service for the OnlyFans Clone microservices platform. It uses Netflix Eureka Server for service registration and discovery, allowing all microservices to find and communicate with each other.

## Architecture Role
- **Purpose**: Service registration and discovery
- **Port**: 8761
- **Dependencies**: None (Foundation service)
- **Technology**: Spring Boot 3.5.x + Netflix Eureka Server + Java 21
- **JVM**: ZGC (Z Garbage Collector) for low-latency performance

## Prerequisites
- Java 21 or higher
- Maven 3.9+
- Docker (for containerization)
- Docker Compose (for multi-service setup)

## Local Development Setup

### 1. Clone and Navigate
```bash
cd c:\Users\sumans\Learnings\Fans+\desifans-eureka-server
```

### 2. Build the Application
```bash
# Using Maven Wrapper (Recommended)
./mvnw clean compile

# Or using system Maven
mvn clean compile
```

### 3. Run the Application Locally
```bash
# Using Maven Wrapper with Java 21 optimizations
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-XX:+UseZGC -XX:+UnlockExperimentalVMOptions"

# Or using system Maven
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-XX:+UseZGC -XX:+UnlockExperimentalVMOptions"

# Or run the JAR directly with Java 21 optimizations
./mvnw clean package
java -XX:+UseZGC -XX:+UnlockExperimentalVMOptions -jar target/desifans-eureka-server-0.0.1-SNAPSHOT.jar
```

### 4. Verify the Service
- **Eureka Dashboard**: http://localhost:8761
- **Health Check**: http://localhost:8761/actuator/health
- **Service Info**: http://localhost:8761/actuator/info
- **Prometheus Metrics**: http://localhost:8761/actuator/prometheus
- **JVM Metrics**: http://localhost:8761/actuator/metrics

## Docker Setup

### 1. Build Docker Image
```bash
# Build the Docker image
docker build -t desifans-eureka-server:latest .

# Build with specific tag
docker build -t desifans-eureka-server:1.0.0 .
```

### 2. Run Docker Container
```bash
# Run the container
docker run -d \
  --name eureka-server \
  -p 8761:8761 \
  desifans-eureka-server:latest

# Run with environment variables
docker run -d \
  --name eureka-server \
  -p 8761:8761 \
  -e SPRING_PROFILES_ACTIVE=docker \
  desifans-eureka-server:latest
```

### 3. Docker Container Management
```bash
# Check container status
docker ps

# View container logs
docker logs eureka-server

# Follow logs in real-time
docker logs -f eureka-server

# Stop the container
docker stop eureka-server

# Remove the container
docker rm eureka-server

# Remove the image
docker rmi desifans-eureka-server:latest
```

## Docker Compose Setup

### 1. Create Infrastructure Docker Compose
```yaml
# File: docker-compose.infrastructure.yml
version: '3.8'

services:
  eureka-server:
    build: ./desifans-eureka-server
    container_name: eureka-server
    ports:
      - "8761:8761"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8761/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 30s
    networks:
      - desifans-network

networks:
  desifans-network:
    driver: bridge
```

### 2. Run with Docker Compose
```bash
# Start the service
docker-compose -f docker-compose.infrastructure.yml up -d

# View logs
docker-compose -f docker-compose.infrastructure.yml logs -f

# Stop the service
docker-compose -f docker-compose.infrastructure.yml down

# Rebuild and start
docker-compose -f docker-compose.infrastructure.yml up --build -d
```

## Configuration

### Application Properties
```yaml
# application.yaml
spring:
  application:
    name: desifans-eureka-server

server:
  port: 8761

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
  server:
    enable-self-preservation: false
    eviction-interval-timer-in-ms: 3000
  instance:
    hostname: localhost
```

### Docker Profile Configuration
```yaml
# application-docker.yaml (for Docker environment)
eureka:
  instance:
    hostname: eureka-server
    prefer-ip-address: true
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka/
```

## Service Registration

### How Other Services Register
Other microservices will register with this Discovery Service using:

```yaml
# Other services' application.yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
```

### Expected Registered Services
Once implemented, you should see these services in the Eureka dashboard:
- `CONFIG-SERVICE` (Port: 8888)
- `API-GATEWAY` (Port: 8080)
- `USER-SERVICE` (Port: 8081)
- `CONTENT-SERVICE` (Port: 8082)
- `SUBSCRIPTION-SERVICE` (Port: 8083)
- `PAYMENT-SERVICE` (Port: 8084)
- `LIVE-STREAMING-SERVICE` (Port: 8085)
- `FILE-STORAGE-SERVICE` (Port: 8088)

## Monitoring and Health Checks

### Health Check Endpoints
```bash
# Application health
curl http://localhost:8761/actuator/health

# Detailed health information
curl http://localhost:8761/actuator/health/diskSpace

# Service discovery info
curl http://localhost:8761/eureka/apps
```

### Monitoring Commands
```bash
# Check service registration
curl -H "Accept: application/json" http://localhost:8761/eureka/apps

# Monitor specific service
curl -H "Accept: application/json" http://localhost:8761/eureka/apps/USER-SERVICE

# Get instance information
curl http://localhost:8761/actuator/info
```

## Troubleshooting

### Common Issues

#### 1. Port Already in Use
```bash
# Check what's using port 8761
netstat -ano | findstr :8761

# Kill the process (Windows)
taskkill /PID <process_id> /F

# Kill the process (Linux/Mac)
kill -9 <process_id>
```

#### 2. Services Not Registering
- Check network connectivity
- Verify Eureka server URL in client configuration
- Check firewall/security group settings
- Verify DNS resolution

#### 3. Docker Connection Issues
```bash
# Check Docker network
docker network ls

# Inspect the network
docker network inspect desifans-network

# Check container connectivity
docker exec -it eureka-server ping config-service
```

### Debug Commands
```bash
# Enable debug logging
java -jar target/desifans-eureka-server-0.0.1-SNAPSHOT.jar --logging.level.com.netflix.eureka=DEBUG

# Check application properties
docker exec -it eureka-server env | grep SPRING

# View detailed container information
docker inspect eureka-server
```

## Development Workflow

### 1. Code Changes
```bash
# After making code changes
./mvnw clean compile

# Run tests
./mvnw test

# Package the application
./mvnw clean package
```

### 2. Docker Development
```bash
# Rebuild and restart
docker-compose -f docker-compose.infrastructure.yml down
docker-compose -f docker-compose.infrastructure.yml up --build -d

# Quick restart without rebuild
docker-compose -f docker-compose.infrastructure.yml restart eureka-server
```

### 3. Testing Service Discovery
```bash
# Register a test service (using curl)
curl -X POST http://localhost:8761/eureka/apps/TEST-SERVICE \
  -H "Content-Type: application/json" \
  -d '{
    "instance": {
      "instanceId": "test-service-1",
      "hostName": "localhost",
      "app": "TEST-SERVICE",
      "ipAddr": "127.0.0.1",
      "status": "UP",
      "port": {"$": 8999, "@enabled": true},
      "dataCenterInfo": {"@class": "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo", "name": "MyOwn"}
    }
  }'
```

## Production Considerations

### 1. Security
- Enable Spring Security for production
- Configure authentication for Eureka dashboard
- Use HTTPS in production
- Implement proper network security

### 2. High Availability
```yaml
# Multiple Eureka servers for HA
eureka:
  client:
    service-url:
      defaultZone: http://eureka1:8761/eureka/,http://eureka2:8761/eureka/
```

### 3. Resource Limits
```yaml
# docker-compose.yml resource limits
services:
  eureka-server:
    deploy:
      resources:
        limits:
          cpus: '0.5'
          memory: 512M
        reservations:
          cpus: '0.25'
          memory: 256M
```

## Next Steps

1. **Config Service**: Implement Spring Cloud Config Server
2. **API Gateway**: Create Spring Cloud Gateway
3. **Business Services**: Start with User Service
4. **Integration Testing**: Test service discovery between services

## Links and References

- **Eureka Dashboard**: http://localhost:8761
- **Spring Cloud Netflix**: https://spring.io/projects/spring-cloud-netflix
- **Docker Documentation**: https://docs.docker.com/
- **Project Repository**: [Add your Git repository URL here]

---

**Status**: ✅ Discovery Service Ready  
**Next Service**: Config Service (Port 8888)  
**Dependencies**: None  
**Health Check**: http://localhost:8761/actuator/health
