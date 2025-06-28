# 🚀 DesiFans Platform - Complete Docker Development Environment

This comprehensive Docker Compose setup provides a complete development environment for the DesiFans platform, including all necessary infrastructure services, databases, monitoring tools, and development utilities.

## 📋 What's Included

### **🏗️ Core Infrastructure**
- **Eureka Discovery Server** (Port 8761) - Service discovery and registration
- **Nginx Load Balancer** (Port 80) - Reverse proxy and load balancing
- **Apache Kafka + Zookeeper** - Event streaming and message broker

### **🗄️ Databases & Storage**
- **MongoDB 7.0** (Port 27017) - Primary database for user/content services
- **PostgreSQL 16** (Port 5432) - ACID database for financial services
- **Redis 7.2** (Port 6379) - Caching and session management
- **MinIO** (Ports 9000/9001) - S3-compatible object storage

### **🔍 Database Management UIs**
- **MongoDB Express** (Port 8082) - MongoDB web interface
- **Redis Commander** (Port 8083) - Redis web interface
- **pgAdmin** (Port 8084) - PostgreSQL web interface

### **📊 Monitoring & Observability**
- **Prometheus** (Port 9090) - Metrics collection
- **Grafana** (Port 3000) - Metrics visualization and dashboards
- **Jaeger** (Port 16686) - Distributed tracing
- **Kafka UI** (Port 8085) - Kafka management interface

### **🛠️ Development Tools**
- **MailHog** (Ports 1025/8086) - Email testing service
- **Portainer** (Port 9443) - Docker container management

## 🚀 Quick Start

### **1. Prerequisites**
- Docker Desktop 4.0+ with at least 8GB RAM allocated
- Docker Compose v2.0+
- 20GB free disk space

### **2. Clone and Setup**
```bash
# Navigate to your project directory
cd "c:\Users\sumans\Learnings\Fans+"

# Copy environment file
copy .env.example .env

# Edit .env file with your actual configuration
notepad .env
```

### **3. Start Everything (Windows)**
```powershell
# Using PowerShell script (recommended)
.\start-dev-env.ps1 start

# Or manually with docker-compose
docker-compose up -d
```

### **4. Start Everything (Linux/Mac)**
```bash
# Using bash script (recommended)
./start-dev-env.sh start

# Or manually with docker-compose
docker-compose up -d
```

## 🌐 Service Access URLs

| Service | URL | Credentials |
|---------|-----|-------------|
| **Eureka Discovery** | http://localhost:8761 | None |
| **MongoDB Express** | http://localhost:8082 | admin / admin123 |
| **Redis Commander** | http://localhost:8083 | None |
| **pgAdmin** | http://localhost:8084 | admin@desifans.com / admin123 |
| **Kafka UI** | http://localhost:8085 | None |
| **MailHog** | http://localhost:8086 | None |
| **MinIO Console** | http://localhost:9001 | minioadmin / minioadmin123 |
| **Prometheus** | http://localhost:9090 | None |
| **Grafana** | http://localhost:3000 | admin / admin123 |
| **Jaeger** | http://localhost:16686 | None |
| **Portainer** | https://localhost:9443 | Create on first access |

## 🔗 Database Connections

### **MongoDB**
```yaml
Host: localhost
Port: 27017
Username: admin
Password: admin123
Database: desifans_users

# Application user
Username: desifans_user
Password: desifans123
```

### **Redis**
```yaml
Host: localhost
Port: 6379
Password: redis123
```

### **PostgreSQL**
```yaml
Host: localhost
Port: 5432
Username: postgres
Password: postgres123
Database: desifans_payments
```

## 🏗️ Architecture Overview

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Nginx         │    │   API Gateway   │    │   User Service  │
│   Load Balancer │────│   (Port 8080)   │────│   (Port 8081)   │
│   (Port 80)     │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │              ┌─────────────────┐              │
         │              │   Eureka Server │              │
         └──────────────│   (Port 8761)   │──────────────┘
                        │                 │
                        └─────────────────┘
                                 │
    ┌─────────────────────────────┼─────────────────────────────┐
    │                             │                             │
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│    MongoDB      │    │     Redis       │    │   PostgreSQL    │
│  (Port 27017)   │    │  (Port 6379)    │    │  (Port 5432)    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │              ┌─────────────────┐              │
         │              │     Kafka       │              │
         └──────────────│  (Port 9092)    │──────────────┘
                        └─────────────────┘
```

## 📁 Project Structure

```
Fans+/
├── docker-compose.yaml              # Main Docker Compose configuration
├── .env.example                     # Environment variables template
├── start-dev-env.ps1               # Windows startup script
├── start-dev-env.sh                # Linux/Mac startup script
├── docker/                         # Docker configuration files
│   ├── README.md                   # Docker setup documentation
│   ├── mongo/
│   │   ├── mongod.conf            # MongoDB configuration
│   │   └── init-scripts/
│   │       └── 01-init.js         # Database initialization
│   ├── redis/
│   │   └── redis.conf             # Redis configuration
│   ├── postgres/
│   │   └── init-scripts/
│   │       └── 01-init.sql        # PostgreSQL initialization
│   ├── prometheus/
│   │   └── prometheus.yml         # Prometheus configuration
│   ├── grafana/
│   │   └── provisioning/
│   │       └── datasources/
│   │           └── datasource.yml # Grafana data sources
│   └── nginx/
│       └── nginx.conf             # Load balancer configuration
├── desifans-eureka-server/         # Eureka server implementation
│   ├── Dockerfile                 # Eureka server container
│   └── src/main/resources/
│       └── application-docker.yaml # Docker-specific config
└── USER_SERVICE_ROADMAP.md        # Detailed implementation guide
```

## 🛠️ Development Workflow

### **Starting Development**
```bash
# Start infrastructure only (for local service development)
docker-compose up -d mongodb redis kafka prometheus grafana

# Start your Spring Boot services locally using your IDE
# They will automatically register with Eureka
```

### **Full Stack Development**
```bash
# Start everything including platform services
docker-compose up -d

# Check all services are running
docker-compose ps

# View logs for specific service
docker-compose logs -f user-service
```

### **Debugging & Troubleshooting**
```bash
# Check service health
curl http://localhost:8761/actuator/health

# Access service containers
docker-compose exec mongodb mongosh
docker-compose exec redis redis-cli
docker-compose exec postgresql psql -U postgres

# Restart specific service
docker-compose restart mongodb

# View detailed logs
docker-compose logs --tail=100 -f mongodb
```

## 🔧 Common Management Tasks

### **Database Operations**
```bash
# MongoDB
docker-compose exec mongodb mongosh -u admin -p admin123
use desifans_users
db.users.find()

# Redis
docker-compose exec redis redis-cli -a redis123
keys *
get user:profile:123

# PostgreSQL
docker-compose exec postgresql psql -U postgres -d desifans_payments
\dt
SELECT * FROM payments.transactions LIMIT 10;
```

### **Kafka Operations**
```bash
# List topics
docker-compose exec kafka kafka-topics --bootstrap-server localhost:9092 --list

# Create topic
docker-compose exec kafka kafka-topics --bootstrap-server localhost:9092 --create --topic user-events --partitions 3 --replication-factor 1

# Consume messages
docker-compose exec kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic user-events --from-beginning
```

### **MinIO Operations**
```bash
# Create bucket for user avatars
# Access MinIO console at http://localhost:9001
# Login with minioadmin / minioadmin123
# Create buckets: user-avatars, content-media, documents
```

## 🔒 Security Considerations

### **Development Environment**
- **Default passwords are for development only**
- **Services are accessible without authentication**
- **No SSL/TLS encryption configured**

### **Production Preparation**
- Change all default passwords in `.env`
- Configure SSL certificates
- Enable authentication for all services
- Set up proper firewall rules
- Use secrets management
- Enable audit logging

## 📊 Performance Tuning

### **Memory Allocation**
```yaml
# Adjust in docker-compose.yaml
services:
  mongodb:
    deploy:
      resources:
        limits:
          memory: 2G
        reservations:
          memory: 1G
```

### **Database Optimization**
- MongoDB: Proper indexing (configured in init script)
- Redis: Memory policy and persistence settings
- PostgreSQL: Connection pooling and query optimization

## 🚨 Troubleshooting

### **Common Issues**

#### **Port Conflicts**
```bash
# Check what's using a port
netstat -tulpn | grep 8761
# Or on Windows
netstat -ano | findstr 8761

# Kill process using port
# Linux/Mac
sudo kill -9 <PID>
# Windows
taskkill /PID <PID> /F
```

#### **Memory Issues**
- Increase Docker Desktop memory allocation (8GB+)
- Check available memory: `docker system df`
- Clean up unused resources: `docker system prune -a`

#### **Service Won't Start**
```bash
# Check logs
docker-compose logs service-name

# Rebuild container
docker-compose build --no-cache service-name
docker-compose up -d service-name
```

#### **Data Persistence Issues**
```bash
# List volumes
docker volume ls

# Backup volume
docker run --rm -v fans_mongodb_data:/data -v $(pwd):/backup alpine tar czf /backup/mongodb_backup.tar.gz /data

# Restore volume
docker run --rm -v fans_mongodb_data:/data -v $(pwd):/backup alpine tar xzf /backup/mongodb_backup.tar.gz -C /
```

### **Health Checks**
```bash
# Check all services
docker-compose ps

# Individual service health
curl http://localhost:8761/actuator/health  # Eureka
curl http://localhost:9090/-/healthy        # Prometheus
curl http://localhost:3000/api/health       # Grafana
```

## 🔄 Data Management

### **Backup Strategy**
```bash
# MongoDB backup
docker-compose exec mongodb mongodump --uri="mongodb://admin:admin123@localhost:27017/desifans_users" --out=/backup/

# PostgreSQL backup
docker-compose exec postgresql pg_dump -U postgres desifans_payments > backup.sql

# Redis backup
docker-compose exec redis redis-cli -a redis123 --rdb backup.rdb
```

### **Data Migration**
```bash
# Export from development to staging
# Use the backup commands above
# Import to target environment using restore procedures
```

## 📈 Monitoring & Observability

### **Key Metrics to Monitor**
- **Application**: Response times, error rates, throughput
- **Infrastructure**: CPU, memory, disk usage
- **Database**: Connection pools, query performance
- **Business**: User registrations, logins, creator signups

### **Grafana Dashboards**
Access Grafana at http://localhost:3000 and import dashboards for:
- Spring Boot applications
- MongoDB monitoring
- Redis monitoring
- Kafka monitoring
- System metrics

## 🚀 Next Steps

1. **Follow the User Service Roadmap** in `USER_SERVICE_ROADMAP.md`
2. **Create your User Service** using the provided architecture
3. **Implement authentication and user management**
4. **Add monitoring and metrics**
5. **Scale to additional services** (Content, Payment, etc.)

---

## 🤝 Support

If you encounter issues:
1. Check the troubleshooting section above
2. Review service logs: `docker-compose logs -f <service>`
3. Ensure all prerequisites are met
4. Verify `.env` configuration

This Docker environment provides everything you need to develop the DesiFans platform efficiently. Happy coding! 🎉
