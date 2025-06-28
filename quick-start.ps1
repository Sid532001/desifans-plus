# Quick Start Script for DesiFans Platform
# Start only essential services to get you going quickly

Write-Host "🚀 Starting DesiFans Platform - Quick Setup" -ForegroundColor Green
Write-Host "=========================================" -ForegroundColor Green

# Check if Docker is running
try {
    docker info | Out-Null
    Write-Host "✅ Docker is running" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker is not running. Please start Docker Desktop first!" -ForegroundColor Red
    exit 1
}

# Create .env file if it doesn't exist
if (-not (Test-Path ".env")) {
    Write-Host "📝 Creating .env file..." -ForegroundColor Yellow
    Copy-Item ".env.example" ".env"
}

Write-Host "🏗️ Starting essential services (this may take 2-3 minutes)..." -ForegroundColor Blue

# Start infrastructure services first
Write-Host "   📊 Starting databases..." -ForegroundColor Cyan
docker-compose up -d mongodb redis postgresql

Write-Host "   🔍 Starting monitoring..." -ForegroundColor Cyan  
docker-compose up -d prometheus grafana

Write-Host "   💌 Starting email service..." -ForegroundColor Cyan
docker-compose up -d mailhog

Write-Host "   📁 Starting file storage..." -ForegroundColor Cyan
docker-compose up -d minio

Write-Host "   🎛️ Starting admin interfaces..." -ForegroundColor Cyan
docker-compose up -d mongo-express redis-commander pgadmin

Write-Host "⏳ Waiting 30 seconds for services to initialize..." -ForegroundColor Yellow
Start-Sleep 30

Write-Host "🌐 Starting Eureka Discovery Server..." -ForegroundColor Blue
docker-compose up -d eureka-server

Write-Host "⏳ Waiting for Eureka to be ready..." -ForegroundColor Yellow
$timeout = 60
while ($timeout -gt 0) {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8761" -TimeoutSec 2 -ErrorAction Stop
        if ($response.StatusCode -eq 200) {
            Write-Host "✅ Eureka is ready!" -ForegroundColor Green
            break
        }
    } catch {
        Write-Host "." -NoNewline -ForegroundColor Yellow
    }
    Start-Sleep 3
    $timeout -= 3
}

if ($timeout -le 0) {
    Write-Host "⚠️ Eureka took longer than expected, but continuing..." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "🎉 SETUP COMPLETE!" -ForegroundColor Green
Write-Host "==================" -ForegroundColor Green
Write-Host ""
Write-Host "🌐 Access your services:" -ForegroundColor Blue
Write-Host "• Eureka Discovery:  http://localhost:8761" 
Write-Host "• MongoDB Admin:     http://localhost:8082  (admin/admin123)"
Write-Host "• Redis Admin:       http://localhost:8083"
Write-Host "• PostgreSQL Admin:  http://localhost:8084  (admin@desifans.com/admin123)" 
Write-Host "• Email Testing:     http://localhost:8086"
Write-Host "• File Storage:      http://localhost:9011  (minioadmin/minioadmin123)"
Write-Host "• Monitoring:        http://localhost:3000  (admin/admin123)"
Write-Host ""
Write-Host "📚 Next Steps:" -ForegroundColor Yellow
Write-Host "1. Check that Eureka is running: http://localhost:8761"
Write-Host "2. Explore MongoDB: http://localhost:8082" 
Write-Host "3. Follow the USER_SERVICE_ROADMAP.md to build your first service"
Write-Host ""
Write-Host "🛠️ Management Commands:" -ForegroundColor Cyan
Write-Host "• Check status:     docker-compose ps"
Write-Host "• View logs:        docker-compose logs -f [service-name]"
Write-Host "• Stop all:         docker-compose down"
Write-Host "• Restart:          docker-compose restart [service-name]"
Write-Host ""
