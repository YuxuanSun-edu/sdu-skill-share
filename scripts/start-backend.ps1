$ErrorActionPreference = "Stop"
$modules = @("gateway", "user-service", "skill-service", "order-service", "message-service")
foreach ($module in $modules) {
  Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PSScriptRoot\..'; mvn -pl $module spring-boot:run"
}
