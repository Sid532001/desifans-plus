# DesiFans - OnlyFans Clone Microservices Platform

## Project Overview
A modern content subscription platform built with microservices architecture using Spring Boot, allowing creators to monetize their content through subscriptions, tips, and live streaming.

## Architecture
- **Microservices**: Spring Boot 3.5.x services with Java 21 LTS
- **Service Discovery**: Netflix Eureka with ZGC for low-latency
- **Database**: MongoDB for document storage
- **Cache**: Redis for session management
- **Message Queue**: Apache Kafka for event-driven communication
- **Monitoring**: Prometheus + Grafana with Micrometer metrics
- **Frontend**: Next.js (to be implemented)
- **JVM**: Java 21 with ZGC and Virtual Threads for optimal performance

## Services Overview

### Infrastructure Services
| Service | Port | Status | Purpose |
|---------|------|--------|---------|
| Discovery Service (Eureka) | 8761 | ✅ Ready | Service registration & discovery |
| Config Service | 8888 | 🚧 Next | Centralized configuration |
| API Gateway | 8080 | 📋 Planned | Request routing & authentication |

### Core Business Services
| Service | Port | Status | Purpose |
|---------|------|--------|---------|
| User Service | 8081 | 📋 Planned | Authentication & user management |
| Content Service | 8082 | 📋 Planned | Content creation & management |
| Subscription Service | 8083 | 📋 Planned | Creator subscriptions |
| Payment Service | 8084 | 📋 Planned | Payment processing & earnings |
| Live Streaming Service | 8085 | 📋 Planned | Real-time streaming |
| File Storage Service | 8088 | 📋 Planned | Media file management |

### Supporting Infrastructure
| Service | Port | Purpose |
|---------|------|---------|
| MongoDB | 27017 | Primary database |
| Redis | 6379 | Cache & sessions |
| Kafka | 9092 | Event streaming |
| Prometheus | 9090 | Metrics collection |
| Grafana | 3000 | Monitoring dashboard |

## Quick Start with Docker

### Prerequisites
- Docker Desktop
- Docker Compose
- Java 21 (for local development)
- Maven 3.9+ (for local development)
- 8GB+ RAM recommended

### 1. Start Infrastructure Services
```bash
# Navigate to project root
cd "c:\Users\sumans\Learnings\Fans+"

# Start all infrastructure services
docker-compose -f docker-compose.infrastructure.yml up -d

# View logs
docker-compose -f docker-compose.infrastructure.yml logs -f

# Check service status
docker-compose -f docker-compose.infrastructure.yml ps
```

### 2. Verify Services
```bash
# Discovery Service (Eureka Dashboard)
start http://localhost:8761

# MongoDB (using MongoDB Compass or CLI)
mongosh mongodb://admin:password123@localhost:27017/admin

# Redis (using Redis CLI)
docker exec -it redis redis-cli -a redis123

# Prometheus
start http://localhost:9090

# Grafana (admin/admin123)
start http://localhost:3000

# Kafka (check topics)
docker exec -it kafka kafka-topics --bootstrap-server localhost:9092 --list
```

### 3. Stop All Services
```bash
# Stop infrastructure
docker-compose -f docker-compose.infrastructure.yml down

# Stop and remove volumes (WARNING: This deletes all data)
docker-compose -f docker-compose.infrastructure.yml down -v
```

## Development Workflow

### Individual Service Development

#### 1. Discovery Service (Currently Ready)
```bash
cd desifans-eureka-server

# Local development
./mvnw spring-boot:run

# Docker build and test
docker build -t desifans-eureka-server:latest .
docker run -p 8761:8761 desifans-eureka-server:latest

# Integration with infrastructure
docker-compose -f docker-compose.infrastructure.yml up eureka-server
```

#### 2. Next Service: Config Service
```bash
# Create config service
mkdir desifans-config-server
cd desifans-config-server

# Initialize Spring Boot project with dependencies:
# - Spring Cloud Config Server
# - Eureka Discovery Client
# - Spring Boot Actuator
```

### Environment Management

#### Local Development Environment
```bash
# Start only required infrastructure for development
docker-compose -f docker-compose.infrastructure.yml up mongodb redis kafka zookeeper -d

# Run services locally on host machine
./mvnw spring-boot:run
```

#### Full Docker Environment
```bash
# Start everything in Docker
docker-compose -f docker-compose.infrastructure.yml up -d

# Scale specific services
docker-compose -f docker-compose.infrastructure.yml up --scale eureka-server=2 -d
```

## Monitoring and Debugging

### Service Health Checks
```bash
# Check all container health
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

# Individual service health
curl http://localhost:8761/actuator/health
curl http://localhost:9090/-/healthy
```

### Logs and Debugging
```bash
# View logs for all services
docker-compose -f docker-compose.infrastructure.yml logs

# View logs for specific service
docker-compose -f docker-compose.infrastructure.yml logs -f eureka-server

# Enter container for debugging
docker exec -it eureka-server bash
```

### Database Operations
```bash
# MongoDB operations
docker exec -it mongodb mongosh -u admin -p password123

# Redis operations
docker exec -it redis redis-cli -a redis123

# Check Redis keys
docker exec -it redis redis-cli -a redis123 KEYS "*"
```

### Kafka Operations
```bash
# List topics
docker exec -it kafka kafka-topics --bootstrap-server localhost:9092 --list

# Create topic
docker exec -it kafka kafka-topics --bootstrap-server localhost:9092 --create --topic user-events --partitions 3 --replication-factor 1

# Consume messages
docker exec -it kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic user-events --from-beginning
```

## Network and Connectivity

### Internal Service Communication
Services communicate through Docker network `desifans-network`:
- eureka-server:8761
- mongodb:27017
- redis:6379
- kafka:9092

### External Access Points
- **Eureka Dashboard**: http://localhost:8761
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000
- **MongoDB**: localhost:27017
- **Redis**: localhost:6379
- **Kafka**: localhost:9092

## Troubleshooting

### Common Issues

#### 1. Port Conflicts
```bash
# Check what's using a port
netstat -ano | findstr :8761

# Kill process using port
taskkill /PID <process_id> /F
```

#### 2. Docker Issues
```bash
# Clean up Docker
docker system prune -f

# Remove all containers and images
docker-compose -f docker-compose.infrastructure.yml down
docker system prune -a -f

# Restart Docker Desktop
```

#### 3. Memory Issues
```bash
# Check Docker memory usage
docker stats

# Increase Docker Desktop memory allocation
# Settings > Resources > Memory > 8GB+
```

#### 4. Service Discovery Issues
```bash
# Check Eureka registration
curl http://localhost:8761/eureka/apps

# Check network connectivity
docker exec -it eureka-server ping mongodb
docker exec -it eureka-server nslookup mongodb
```

### Debug Commands
```bash
# Check Docker Compose configuration
docker-compose -f docker-compose.infrastructure.yml config

# Validate Docker Compose file
docker-compose -f docker-compose.infrastructure.yml config --quiet

# Check service dependencies
docker-compose -f docker-compose.infrastructure.yml ps --services
```

## Performance Optimization

### Resource Allocation
```yaml
# Add to docker-compose.yml for production
deploy:
  resources:
    limits:
      cpus: '0.5'
      memory: 512M
    reservations:
      cpus: '0.25'
      memory: 256M
```

### Database Optimization
- MongoDB indexes are automatically created via init script
- Redis persistence is enabled
- Kafka configured for single-node development

## Security Considerations

### Development Security
- Default passwords are used (change for production)
- No SSL/TLS in development
- Network is isolated within Docker

### Production Checklist
- [ ] Change all default passwords
- [ ] Enable SSL/TLS
- [ ] Configure proper authentication
- [ ] Set up firewall rules
- [ ] Enable audit logging

## Next Steps

### Week 1 Completion
- [x] Discovery Service (Eureka) - ✅ Complete
- [x] Docker infrastructure setup - ✅ Complete
- [x] Monitoring setup - ✅ Complete

### Week 2 Goals
- [ ] Config Service implementation
- [ ] API Gateway setup
- [ ] Service-to-service communication testing

### Week 3+ Goals
- [ ] User Service
- [ ] Content Service
- [ ] Payment integration
- [ ] Live streaming features

## Resources and Documentation

- **Discovery Service README**: `./desifans-eureka-server/README.md`
- **Spring Cloud Documentation**: https://spring.io/projects/spring-cloud
- **Docker Compose Reference**: https://docs.docker.com/compose/
- **MongoDB Documentation**: https://docs.mongodb.com/
- **Kafka Documentation**: https://kafka.apache.org/documentation/

---

**Current Status**: ✅ Infrastructure Foundation Complete  
**Next Milestone**: Config Service Implementation  
**Team**: Ready for parallel development of business services
