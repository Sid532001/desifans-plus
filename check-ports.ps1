# Check if commonly used ports are available for DesiFans platform
Write-Host "🔍 Checking for port conflicts..." -ForegroundColor Yellow

$ports = @(
    @{Port=8761; Service="Eureka Discovery Server"},
    @{Port=8080; Service="API Gateway"},
    @{Port=27017; Service="MongoDB"},
    @{Port=6379; Service="Redis"},
    @{Port=5432; Service="PostgreSQL"},
    @{Port=9010; Service="MinIO API"},
    @{Port=9011; Service="MinIO Console"},
    @{Port=8082; Service="MongoDB Express"},
    @{Port=8083; Service="Redis Commander"},
    @{Port=3000; Service="Grafana"},
    @{Port=9090; Service="Prometheus"}
)

$conflicts = @()

foreach ($portInfo in $ports) {
    $port = $portInfo.Port
    $service = $portInfo.Service
    
    $connection = Test-NetConnection -ComputerName localhost -Port $port -WarningAction SilentlyContinue
    
    if ($connection.TcpTestSucceeded) {
        Write-Host "❌ Port $port is already in use (needed for: $service)" -ForegroundColor Red
        $conflicts += $port
    } else {
        Write-Host "✅ Port $port is available (for: $service)" -ForegroundColor Green
    }
}

if ($conflicts.Count -gt 0) {
    Write-Host "`n🚨 Port conflicts detected!" -ForegroundColor Red
    Write-Host "The following ports are in use: $($conflicts -join ', ')" -ForegroundColor Yellow
    Write-Host "`nTo find what's using these ports, run:" -ForegroundColor Yellow
    foreach ($port in $conflicts) {
        Write-Host "netstat -ano | findstr :$port" -ForegroundColor Cyan
    }
    Write-Host "`nTo kill a process, use: taskkill /PID <PID> /F" -ForegroundColor Yellow
} else {
    Write-Host "`n🎉 No port conflicts found! You're ready to start." -ForegroundColor Green
}

Write-Host "`nPress any key to continue..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")