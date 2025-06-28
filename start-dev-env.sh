#!/bin/bash

# DesiFans Platform - Development Environment Startup Script
# This script helps you start the entire development environment

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Functions
print_header() {
    echo -e "${BLUE}"
    echo "=========================================="
    echo "  DesiFans Platform - Docker Setup"
    echo "=========================================="
    echo -e "${NC}"
}

print_step() {
    echo -e "${GREEN}[STEP]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

check_docker() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed. Please install Docker first."
        exit 1
    fi

    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose is not installed. Please install Docker Compose first."
        exit 1
    fi

    if ! docker info &> /dev/null; then
        print_error "Docker daemon is not running. Please start Docker first."
        exit 1
    fi
}

check_ports() {
    local ports=(80 3000 5432 6379 8761 8082 8083 8084 8085 8086 9000 9001 9090 9092 9443 16686 27017)
    local occupied_ports=()

    for port in "${ports[@]}"; do
        if netstat -tuln 2>/dev/null | grep -q ":$port "; then
            occupied_ports+=($port)
        fi
    done

    if [ ${#occupied_ports[@]} -ne 0 ]; then
        print_warning "The following ports are already in use: ${occupied_ports[*]}"
        print_warning "Please stop the services using these ports or modify docker-compose.yaml"
        read -p "Do you want to continue anyway? (y/n): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            exit 1
        fi
    fi
}

create_env_file() {
    if [ ! -f ".env" ]; then
        print_step "Creating .env file from template..."
        cp .env.example .env
        print_warning "Please review and update the .env file with your actual configuration"
    else
        print_step ".env file already exists"
    fi
}

start_infrastructure() {
    print_step "Starting infrastructure services..."
    docker-compose up -d mongodb redis postgresql kafka zookeeper prometheus grafana jaeger mailhog minio
    
    print_step "Waiting for services to be ready..."
    sleep 30
    
    print_step "Checking service health..."
    docker-compose ps
}

start_platform_services() {
    print_step "Building and starting platform services..."
    docker-compose up -d eureka-server
    
    print_step "Waiting for Eureka server to be ready..."
    timeout=60
    while [ $timeout -gt 0 ]; do
        if curl -f http://localhost:8761/actuator/health &> /dev/null; then
            print_step "Eureka server is ready!"
            break
        fi
        echo -n "."
        sleep 2
        timeout=$((timeout-2))
    done
    
    if [ $timeout -le 0 ]; then
        print_error "Eureka server failed to start within 60 seconds"
        exit 1
    fi
}

start_monitoring() {
    print_step "Starting monitoring and admin services..."
    docker-compose up -d mongo-express redis-commander pgadmin kafka-ui portainer nginx
}

show_urls() {
    echo
    echo -e "${GREEN}=========================================="
    echo "  🚀 All services are running!"
    echo -e "==========================================${NC}"
    echo
    echo -e "${BLUE}📊 Service URLs:${NC}"
    echo "• Eureka Discovery:     http://localhost:8761"
    echo "• MongoDB Express:      http://localhost:8082"
    echo "• Redis Commander:      http://localhost:8083"
    echo "• pgAdmin:              http://localhost:8084"
    echo "• Kafka UI:             http://localhost:8085"
    echo "• MailHog:              http://localhost:8086"
    echo "• MinIO Console:        http://localhost:9001"
    echo "• Prometheus:           http://localhost:9090"
    echo "• Grafana:              http://localhost:3000"
    echo "• Jaeger:               http://localhost:16686"
    echo "• Portainer:            https://localhost:9443"
    echo
    echo -e "${BLUE}🔑 Default Credentials:${NC}"
    echo "• MongoDB:              admin / admin123"
    echo "• Redis:                (password: redis123)"
    echo "• PostgreSQL:           postgres / postgres123"
    echo "• Grafana:              admin / admin123"
    echo "• MinIO:                minioadmin / minioadmin123"
    echo
    echo -e "${YELLOW}📚 Next Steps:${NC}"
    echo "1. Create your User Service following the roadmap"
    echo "2. Update service configurations in docker-compose.yaml"
    echo "3. Use 'docker-compose logs -f [service]' to view logs"
    echo "4. Use 'docker-compose down' to stop all services"
    echo
}

cleanup() {
    print_step "Cleaning up stopped containers..."
    docker-compose down --remove-orphans
    docker system prune -f
}

# Main execution
print_header

case "${1:-start}" in
    "start")
        check_docker
        check_ports
        create_env_file
        start_infrastructure
        start_platform_services
        start_monitoring
        show_urls
        ;;
    "stop")
        print_step "Stopping all services..."
        docker-compose down
        print_step "All services stopped"
        ;;
    "restart")
        print_step "Restarting all services..."
        docker-compose down
        docker-compose up -d
        print_step "All services restarted"
        ;;
    "clean")
        print_step "Stopping and removing all containers, networks, and volumes..."
        docker-compose down -v
        cleanup
        print_step "Cleanup completed"
        ;;
    "logs")
        if [ -n "$2" ]; then
            docker-compose logs -f "$2"
        else
            docker-compose logs -f
        fi
        ;;
    "status")
        docker-compose ps
        ;;
    "help")
        echo "Usage: $0 [command]"
        echo
        echo "Commands:"
        echo "  start     - Start all services (default)"
        echo "  stop      - Stop all services"
        echo "  restart   - Restart all services"
        echo "  clean     - Stop and remove all containers, networks, and volumes"
        echo "  logs      - Show logs for all services"
        echo "  logs <service> - Show logs for specific service"
        echo "  status    - Show status of all services"
        echo "  help      - Show this help message"
        ;;
    *)
        print_error "Unknown command: $1"
        echo "Use '$0 help' for available commands"
        exit 1
        ;;
esac
