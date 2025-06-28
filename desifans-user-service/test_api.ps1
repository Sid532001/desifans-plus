#!/usr/bin/env pwsh

Write-Host "=== API End-to-End Testing ===" -ForegroundColor Green

Write-Host "`n1. Testing Health Check..." -ForegroundColor Yellow
try {
    $healthResponse = Invoke-RestMethod -Uri "http://localhost:9091/api/v1/health" -Method GET
    Write-Host "✅ Health Check Response:" -ForegroundColor Green
    $healthResponse | ConvertTo-Json -Depth 3
} catch {
    Write-Host "❌ Health Check Failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n2. Testing User Registration..." -ForegroundColor Yellow
$timestamp = [DateTimeOffset]::UtcNow.ToUnixTimeSeconds()
$registrationBody = @{
    username = "testuser$timestamp"
    email = "test$timestamp@example.com"
    password = "SecurePass123!"
    confirmPassword = "SecurePass123!"
    fullName = "Test User $timestamp"
    displayName = "Test User $timestamp"
    dateOfBirth = "1990-01-01"
} | ConvertTo-Json

try {
    $registerResponse = Invoke-RestMethod -Uri "http://localhost:9091/api/v1/auth/register" -Method POST -ContentType "application/json" -Body $registrationBody
    Write-Host "✅ Registration Response:" -ForegroundColor Green
    $registerResponse | ConvertTo-Json -Depth 3
    $testUsername = "testuser$timestamp"
} catch {
    Write-Host "❌ Registration Failed: $($_.Exception.Message)" -ForegroundColor Red
    # Try with existing user if registration fails
    $testUsername = "newuser456"
}

Write-Host "`n3. Testing Login..." -ForegroundColor Yellow
$loginBody = @{
    loginIdentifier = $testUsername
    password = "SecurePass123!"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:9091/api/v1/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
    Write-Host "✅ Login Response:" -ForegroundColor Green
    $loginResponse | ConvertTo-Json -Depth 3
    
    $accessToken = $loginResponse.data.accessToken
    $refreshToken = $loginResponse.data.refreshToken
    Write-Host "Access Token: $accessToken" -ForegroundColor Cyan
    Write-Host "Refresh Token: $refreshToken" -ForegroundColor Cyan
} catch {
    Write-Host "❌ Login Failed: $($_.Exception.Message)" -ForegroundColor Red
    # Exit if login fails as subsequent tests need tokens
    Write-Host "Cannot continue without valid tokens" -ForegroundColor Red
    exit 1
}

Write-Host "`n4. Testing Profile Access..." -ForegroundColor Yellow
try {
    $profileResponse = Invoke-RestMethod -Uri "http://localhost:9091/api/v1/users/profile" -Method GET -Headers @{Authorization = "Bearer $accessToken"}
    Write-Host "✅ Profile Response:" -ForegroundColor Green
    $profileResponse | ConvertTo-Json -Depth 3
} catch {
    Write-Host "❌ Profile Access Failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n5. Testing Profile Update..." -ForegroundColor Yellow
$profileUpdateBody = @{
    displayName = "Test User Updated"
    bio = "This is my updated bio"
    location = "New York"
} | ConvertTo-Json

try {
    $updateResponse = Invoke-RestMethod -Uri "http://localhost:9091/api/v1/users/profile" -Method PUT -ContentType "application/json" -Headers @{Authorization = "Bearer $accessToken"} -Body $profileUpdateBody
    Write-Host "✅ Profile Update Response:" -ForegroundColor Green
    $updateResponse | ConvertTo-Json -Depth 3
} catch {
    Write-Host "❌ Profile Update Failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n6. Testing Token Refresh..." -ForegroundColor Yellow
$refreshBody = @{
    refreshToken = $refreshToken
} | ConvertTo-Json

try {
    $refreshResponse = Invoke-RestMethod -Uri "http://localhost:9091/api/v1/auth/refresh" -Method POST -ContentType "application/json" -Body $refreshBody
    Write-Host "✅ Token Refresh Response:" -ForegroundColor Green
    $refreshResponse | ConvertTo-Json -Depth 3
} catch {
    Write-Host "❌ Token Refresh Failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n7. Testing Error Scenarios..." -ForegroundColor Yellow

# Test invalid login
Write-Host "  7a. Testing Invalid Login..." -ForegroundColor Magenta
$invalidLoginBody = @{
    loginIdentifier = "nonexistent"
    password = "wrongpass"
} | ConvertTo-Json

try {
    $invalidResponse = Invoke-RestMethod -Uri "http://localhost:9091/api/v1/auth/login" -Method POST -ContentType "application/json" -Body $invalidLoginBody
    Write-Host "❌ Expected this to fail!" -ForegroundColor Red
} catch {
    Write-Host "✅ Invalid login correctly rejected: $($_.Exception.Response.StatusCode)" -ForegroundColor Green
}

# Test accessing protected endpoint without token
Write-Host "  7b. Testing Unauthorized Access..." -ForegroundColor Magenta
try {
    $unauthorizedResponse = Invoke-RestMethod -Uri "http://localhost:9091/api/v1/users/profile" -Method GET
    Write-Host "❌ Expected this to fail!" -ForegroundColor Red
} catch {
    Write-Host "✅ Unauthorized access correctly rejected: $($_.Exception.Response.StatusCode)" -ForegroundColor Green
}

Write-Host "`n8. Testing Logout..." -ForegroundColor Yellow
try {
    $logoutResponse = Invoke-RestMethod -Uri "http://localhost:9091/api/v1/auth/logout" -Method POST -Headers @{Authorization = "Bearer $accessToken"}
    Write-Host "✅ Logout Response:" -ForegroundColor Green
    $logoutResponse | ConvertTo-Json -Depth 3
} catch {
    Write-Host "❌ Logout Failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== All Tests Completed! ===" -ForegroundColor Green
