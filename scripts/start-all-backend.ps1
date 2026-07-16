$ErrorActionPreference = "Stop"

$root = Resolve-Path "$PSScriptRoot\.."
Set-Location $root

Write-Host "Starting infrastructure with docker compose..." -ForegroundColor Cyan
docker compose up -d

$mvn = Get-Command mvn -ErrorAction SilentlyContinue
if (-not $mvn) {
  Write-Host ""
  Write-Host "Maven command was not found in PATH." -ForegroundColor Yellow
  Write-Host "Use IDEA run configuration 'All Backend Services', or install Maven and run this script again."
  exit 1
}

$services = @(
  @{ Module = "gateway"; Name = "gateway" },
  @{ Module = "user-service"; Name = "user-service" },
  @{ Module = "skill-service"; Name = "skill-service" },
  @{ Module = "order-service"; Name = "order-service" },
  @{ Module = "message-service"; Name = "message-service" }
)

foreach ($service in $services) {
  $module = $service.Module
  $name = $service.Name
  Write-Host "Starting $name..." -ForegroundColor Cyan
  Start-Process powershell -WindowStyle Hidden -ArgumentList @(
    "-NoExit",
    "-Command",
    "cd '$root'; mvn -pl $module spring-boot:run"
  )
}

Write-Host ""
Write-Host "All backend services are starting." -ForegroundColor Green
Write-Host "Gateway: http://localhost:8080"
Write-Host "Nacos:   http://localhost:8848/nacos"
