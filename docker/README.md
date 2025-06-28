# DesiFans Platform - Docker Environment Setup

This directory contains all the Docker configuration files for the DesiFans platform development environment.

## 🚀 Quick Start

1. **Start all services:**
   ```bash
   docker-compose up -d
   ```

2. **View logs:**
   ```bash
   docker-compose logs -f [service_name]
   ```

3. **Stop all services:**
   ```bash
   docker-compose down
   ```

4. **Clean up everything (including volumes):**
   ```bash
   docker-compose down -v
   docker system prune -a
   ```

## 📊 Service URLs

### **Core Infrastructure**
- **Eureka Discovery Server**: http://localhost:8761
- **Nginx Load Balancer**: http://localhost:80

### **Databases & Storage**
- **MongoDB**: mongodb://localhost:27017
  - Username: `admin` / Password: `admin123`
  - App User: `desifans_user` / Password: `desifans123`
- **MongoDB Express**: http://localhost:8082
  - Username: `admin` / Password: `admin123`
- **Redis**: redis://localhost:6379
  - Password: `redis123`
- **Redis Commander**: http://localhost:8083
- **PostgreSQL**: postgresql://localhost:5432
  - Username: `postgres` / Password: `postgres123`
- **pgAdmin**: http://localhost:8084
  - Email: `admin@desifans.com` / Password: `admin123`
- **MinIO S3**: http://localhost:9000
  - Console: http://localhost:9001
  - Username: `minioadmin` / Password: `minioadmin123`

### **Message Broker**
- **Kafka**: localhost:9092
- **Kafka UI**: http://localhost:8085

### **Email Testing**
- **MailHog SMTP**: localhost:1025
- **MailHog Web UI**: http://localhost:8086

### **Monitoring & Observability**
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000
  - Username: `admin` / Password: `admin123`
- **Jaeger Tracing**: http://localhost:16686

### **Development Tools**
- **Portainer**: https://localhost:9443

## 🗂️ File Structure

```
docker/
├── mongo/
│   ├── mongod.conf              # MongoDB configuration
│   └── init-scripts/
│       └── 01-init.js           # Database initialization
├── redis/
│   └── redis.conf               # Redis configuration
├── postgres/
│   └── init-scripts/
│       └── 01-init.sql          # PostgreSQL initialization
├── prometheus/
│   └── prometheus.yml           # Prometheus configuration
├── grafana/
│   └── provisioning/
│       └── datasources/
│           └── datasource.yml   # Grafana data sources
└── nginx/
    └── nginx.conf               # Load balancer configuration
```

## 🔧 Environment Variables

Create a `.env` file in the root directory for sensitive configuration:

```bash
# Database Passwords
MONGODB_ROOT_PASSWORD=admin123
MONGODB_APP_PASSWORD=desifans123
REDIS_PASSWORD=redis123
POSTGRES_PASSWORD=postgres123

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-signing-key-here

# Email Configuration
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password

# MinIO Configuration
MINIO_ROOT_USER=minioadmin
MINIO_ROOT_PASSWORD=minioadmin123
```

## 🛠️ Development Workflow

### **Starting Development**
```bash
# Start infrastructure services only
docker-compose up -d mongodb redis kafka prometheus grafana

# Start your application services manually for development
# or use your IDE
```

### **Full Stack Testing**
```bash
# Start everything
docker-compose up -d

# Check service health
docker-compose ps
```

### **Debugging**
```bash
# View service logs
docker-compose logs -f user-service

# Execute commands in containers
docker-compose exec mongodb mongosh
docker-compose exec redis redis-cli
```

## 🔒 Security Notes

- **Default passwords are for development only**
- **Change all default credentials before production**
- **Use proper SSL certificates in production**
- **Configure proper firewall rules**

## 📚 Service Dependencies

```
Eureka Server (8761)
├── MongoDB (27017)
├── Redis (6379)
├── Kafka (9092)
│   └── Zookeeper (2181)
└── User Service (8081)
    ├── MongoDB
    ├── Redis
    └── Kafka
```

## 🚨 Troubleshooting

### **Common Issues:**

1. **Port conflicts:** Check if ports are already in use
   ```bash
   netstat -tulpn | grep LISTEN
   ```

2. **Memory issues:** Increase Docker memory allocation
   - Docker Desktop: Settings → Resources → Memory

3. **Permission issues on Windows:**
   ```bash
   # Run as administrator
   docker-compose up -d
   ```

4. **MongoDB connection issues:**
   ```bash
   # Check MongoDB logs
   docker-compose logs mongodb
   ```

### **Health Checks:**
```bash
# Check all service health
docker-compose ps

# Individual service health
curl http://localhost:8761/actuator/health  # Eureka
curl http://localhost:9090/-/healthy        # Prometheus
```

## 🔄 Data Persistence

All data is persisted in Docker volumes:
- `mongodb_data`: MongoDB database files
- `redis_data`: Redis persistence
- `kafka_data`: Kafka logs and topics
- `grafana_data`: Grafana dashboards and settings
- `prometheus_data`: Metrics data

To backup data:
```bash
# Create backup
docker run --rm -v mongodb_data:/data -v $(pwd):/backup alpine tar czf /backup/mongodb_backup.tar.gz /data

# Restore backup
docker run --rm -v mongodb_data:/data -v $(pwd):/backup alpine tar xzf /backup/mongodb_backup.tar.gz -C /
```
