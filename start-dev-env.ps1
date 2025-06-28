# DesiFans Platform - Development Environment Startup Script (PowerShell)
# This script helps you start the entire development environment on Windows

param(
    [Parameter(Position=0)]
    [ValidateSet("start", "stop", "restart", "clean", "logs", "status", "help")]
    [string]$Command = "start",
    
    [Parameter(Position=1)]
    [string]$Service
)

# Functions
function Write-Header {
    Write-Host "==========================================" -ForegroundColor Blue
    Write-Host "  DesiFans Platform - Docker Setup" -ForegroundColor Blue
    Write-Host "==========================================" -ForegroundColor Blue
}

function Write-Step {
    param([string]$Message)
    Write-Host "[STEP] $Message" -ForegroundColor Green
}

function Write-Warning {
    param([string]$Message)
    Write-Host "[WARNING] $Message" -ForegroundColor Yellow
}

function Write-Error {
    param([string]$Message)
    Write-Host "[ERROR] $Message" -ForegroundColor Red
}

function Test-Docker {
    try {
        $null = Get-Command docker -ErrorAction Stop
        $null = Get-Command docker-compose -ErrorAction Stop
        
        $dockerInfo = docker info 2>$null
        if ($LASTEXITCODE -ne 0) {
            Write-Error "Docker daemon is not running. Please start Docker Desktop first."
            exit 1
        }
    }
    catch {
        Write-Error "Docker or Docker Compose is not installed. Please install Docker Desktop first."
        exit 1
    }
}

function Test-Ports {
    $ports = @(80, 3000, 5432, 6379, 8761, 8082, 8083, 8084, 8085, 8086, 9000, 9001, 9090, 9092, 9443, 16686, 27017)
    $occupiedPorts = @()
    
    foreach ($port in $ports) {
        $connection = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
        if ($connection) {
            $occupiedPorts += $port
        }
    }
    
    if ($occupiedPorts.Count -gt 0) {
        Write-Warning "The following ports are already in use: $($occupiedPorts -join ', ')"
        Write-Warning "Please stop the services using these ports or modify docker-compose.yaml"
        $response = Read-Host "Do you want to continue anyway? (y/n)"
        if ($response -notmatch "^[Yy]$") {
            exit 1
        }
    }
}

function New-EnvFile {
    if (-not (Test-Path ".env")) {
        Write-Step "Creating .env file from template..."
        Copy-Item ".env.example" ".env"
        Write-Warning "Please review and update the .env file with your actual configuration"
    } else {
        Write-Step ".env file already exists"
    }
}

function Start-Infrastructure {
    Write-Step "Starting infrastructure services..."
    docker-compose up -d mongodb redis postgresql kafka zookeeper prometheus grafana jaeger mailhog minio
    
    Write-Step "Waiting for services to be ready..."
    Start-Sleep 30
    
    Write-Step "Checking service health..."
    docker-compose ps
}

function Start-PlatformServices {
    Write-Step "Building and starting platform services..."
    docker-compose up -d eureka-server
    
    Write-Step "Waiting for Eureka server to be ready..."
    $timeout = 60
    while ($timeout -gt 0) {
        try {
            $response = Invoke-WebRequest -Uri "http://localhost:8761/actuator/health" -TimeoutSec 2 -ErrorAction Stop
            if ($response.StatusCode -eq 200) {
                Write-Step "Eureka server is ready!"
                break
            }
        }
        catch {
            # Continue waiting
        }
        Write-Host "." -NoNewline
        Start-Sleep 2
        $timeout -= 2
    }
    
    if ($timeout -le 0) {
        Write-Error "Eureka server failed to start within 60 seconds"
        exit 1
    }
}

function Start-Monitoring {
    Write-Step "Starting monitoring and admin services..."
    docker-compose up -d mongo-express redis-commander pgadmin kafka-ui portainer nginx
}

function Show-URLs {
    Write-Host ""
    Write-Host "==========================================" -ForegroundColor Green
    Write-Host "  🚀 All services are running!" -ForegroundColor Green
    Write-Host "==========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "📊 Service URLs:" -ForegroundColor Blue
    Write-Host "• Eureka Discovery:     http://localhost:8761"
    Write-Host "• MongoDB Express:      http://localhost:8082"
    Write-Host "• Redis Commander:      http://localhost:8083"
    Write-Host "• pgAdmin:              http://localhost:8084"
    Write-Host "• Kafka UI:             http://localhost:8085"
    Write-Host "• MailHog:              http://localhost:8086"
    Write-Host "• MinIO Console:        http://localhost:9001"
    Write-Host "• Prometheus:           http://localhost:9090"
    Write-Host "• Grafana:              http://localhost:3000"
    Write-Host "• Jaeger:               http://localhost:16686"
    Write-Host "• Portainer:            https://localhost:9443"
    Write-Host ""
    Write-Host "🔑 Default Credentials:" -ForegroundColor Blue
    Write-Host "• MongoDB:              admin / admin123"
    Write-Host "• Redis:                (password: redis123)"
    Write-Host "• PostgreSQL:           postgres / postgres123"
    Write-Host "• Grafana:              admin / admin123"
    Write-Host "• MinIO:                minioadmin / minioadmin123"
    Write-Host ""
    Write-Host "📚 Next Steps:" -ForegroundColor Yellow
    Write-Host "1. Create your User Service following the roadmap"
    Write-Host "2. Update service configurations in docker-compose.yaml"
    Write-Host "3. Use 'docker-compose logs -f [service]' to view logs"
    Write-Host "4. Use 'docker-compose down' to stop all services"
    Write-Host ""
}

function Invoke-Cleanup {
    Write-Step "Cleaning up stopped containers..."
    docker-compose down --remove-orphans
    docker system prune -f
}

# Main execution
Write-Header

switch ($Command) {
    "start" {
        Test-Docker
        Test-Ports
        New-EnvFile
        Start-Infrastructure
        Start-PlatformServices
        Start-Monitoring
        Show-URLs
    }
    "stop" {
        Write-Step "Stopping all services..."
        docker-compose down
        Write-Step "All services stopped"
    }
    "restart" {
        Write-Step "Restarting all services..."
        docker-compose down
        docker-compose up -d
        Write-Step "All services restarted"
    }
    "clean" {
        Write-Step "Stopping and removing all containers, networks, and volumes..."
        docker-compose down -v
        Invoke-Cleanup
        Write-Step "Cleanup completed"
    }
    "logs" {
        if ($Service) {
            docker-compose logs -f $Service
        } else {
            docker-compose logs -f
        }
    }
    "status" {
        docker-compose ps
    }
    "help" {
        Write-Host "Usage: .\start-dev-env.ps1 [command] [service]"
        Write-Host ""
        Write-Host "Commands:"
        Write-Host "  start     - Start all services (default)"
        Write-Host "  stop      - Stop all services"
        Write-Host "  restart   - Restart all services"
        Write-Host "  clean     - Stop and remove all containers, networks, and volumes"
        Write-Host "  logs      - Show logs for all services"
        Write-Host "  logs <service> - Show logs for specific service"
        Write-Host "  status    - Show status of all services"
        Write-Host "  help      - Show this help message"
        Write-Host ""
        Write-Host "Examples:"
        Write-Host "  .\start-dev-env.ps1 start"
        Write-Host "  .\start-dev-env.ps1 logs mongodb"
        Write-Host "  .\start-dev-env.ps1 stop"
    }
    default {
        Write-Error "Unknown command: $Command"
        Write-Host "Use '.\start-dev-env.ps1 help' for available commands"
        exit 1
    }
}
