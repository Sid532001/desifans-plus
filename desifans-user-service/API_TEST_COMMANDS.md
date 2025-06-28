# DesiFans User Service - Complete API Testing Commands

## Base URL
```
http://localhost:9091/api/v1
```

## 1. Health Check

### Check Service Health
```bash
curl -X GET http://localhost:9091/api/v1/health \
  -H "Content-Type: application/json"
```

## 2. Authentication Endpoints

### 2.1 User Registration
```bash
curl -X POST http://localhost:9091/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser123",
    "email": "newuser123@example.com",
    "password": "SecurePass123!",
    "confirmPassword": "SecurePass123!",
    "fullName": "John Doe",
    "displayName": "Johnny",
    "dateOfBirth": "1990-01-01"
  }'
```

### 2.2 User Login
```bash
curl -X POST http://localhost:9091/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "loginIdentifier": "newuser123",
    "password": "SecurePass123!"
  }'
```

**Save the access token and refresh token from the response for subsequent requests!**

### 2.3 Token Refresh
```bash
curl -X POST http://localhost:9091/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN_HERE"
  }'
```

### 2.4 Logout
```bash
curl -X POST http://localhost:9091/api/v1/auth/logout \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE"
```

## 3. User Profile Management

### 3.1 Get Current User Profile
```bash
curl -X GET http://localhost:9091/api/v1/users/profile \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE"
```

### 3.2 Update User Profile
```bash
curl -X PUT http://localhost:9091/api/v1/users/profile \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE" \
  -d '{
    "displayName": "Updated Display Name",
    "bio": "This is my updated bio - I am a content creator!",
    "location": "New York, USA",
    "website": "https://mywebsite.com",
    "twitter": "mytwitterhandle",
    "instagram": "myinstahandle",
    "profileVisibility": true,
    "allowMessages": true,
    "showOnlineStatus": true
  }'
```

### 3.3 Get User Profile by ID
```bash
curl -X GET http://localhost:9091/api/v1/users/USER_ID_HERE \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE"
```

## 4. Account Management

### 4.1 Change Password
```bash
curl -X PUT http://localhost:9091/api/v1/users/password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE" \
  -d '{
    "currentPassword": "SecurePass123!",
    "newPassword": "NewSecurePass456!",
    "confirmPassword": "NewSecurePass456!"
  }'
```

### 4.2 Deactivate Account
```bash
curl -X DELETE http://localhost:9091/api/v1/users/account \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE"
```

### 4.3 Reactivate Account
```bash
curl -X POST http://localhost:9091/api/v1/users/reactivate \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE"
```

## 5. Error Testing

### 5.1 Test Invalid Login
```bash
curl -X POST http://localhost:9091/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "loginIdentifier": "nonexistent",
    "password": "wrongpassword"
  }'
```

### 5.2 Test Unauthorized Access
```bash
curl -X GET http://localhost:9091/api/v1/users/profile \
  -H "Content-Type: application/json"
```

### 5.3 Test Invalid Token
```bash
curl -X GET http://localhost:9091/api/v1/users/profile \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer invalid_token_here"
```

## 6. Complete Test Workflow

### Step 1: Register a new user
```bash
curl -X POST http://localhost:9091/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser001",
    "email": "testuser001@example.com",
    "password": "SecurePass123!",
    "confirmPassword": "SecurePass123!",
    "fullName": "Test User",
    "displayName": "Test User",
    "dateOfBirth": "1995-06-15"
  }'
```

### Step 2: Login with the new user
```bash
curl -X POST http://localhost:9091/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "loginIdentifier": "testuser001",
    "password": "SecurePass123!"
  }'
```

### Step 3: Get profile (use token from step 2)
```bash
curl -X GET http://localhost:9091/api/v1/users/profile \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_FROM_STEP_2"
```

### Step 4: Update profile
```bash
curl -X PUT http://localhost:9091/api/v1/users/profile \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_FROM_STEP_2" \
  -d '{
    "displayName": "Content Creator",
    "bio": "Professional content creator specializing in lifestyle content",
    "location": "Los Angeles, CA",
    "website": "https://mycontentsite.com"
  }'
```

### Step 5: Refresh token
```bash
curl -X POST http://localhost:9091/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN_FROM_STEP_2"
  }'
```

### Step 6: Logout
```bash
curl -X POST http://localhost:9091/api/v1/auth/logout \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## 7. PowerShell Commands (Windows)

### 7.1 Registration (PowerShell)
```powershell
$registerBody = @{
    username = "powershelluser"
    email = "powershelluser@example.com"
    password = "SecurePass123!"
    confirmPassword = "SecurePass123!"
    fullName = "PowerShell User"
    displayName = "PS User"
    dateOfBirth = "1990-01-01"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:9091/api/v1/auth/register" -Method POST -ContentType "application/json" -Body $registerBody
```

### 7.2 Login (PowerShell)
```powershell
$loginBody = @{
    loginIdentifier = "powershelluser"
    password = "SecurePass123!"
} | ConvertTo-Json

$loginResponse = Invoke-RestMethod -Uri "http://localhost:9091/api/v1/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
$accessToken = $loginResponse.data.accessToken
```

### 7.3 Get Profile (PowerShell)
```powershell
Invoke-RestMethod -Uri "http://localhost:9091/api/v1/users/profile" -Method GET -ContentType "application/json" -Headers @{Authorization = "Bearer $accessToken"}
```

## 8. Expected Response Format

All API responses follow this format:
```json
{
    "success": true|false,
    "data": { /* response data */ },
    "message": "Success/Error message",
    "error": { 
        "code": "ERROR_CODE",
        "message": "Detailed error message",
        "details": "Additional error details"
    },
    "timestamp": "2025-06-28T16:30:00.123456789"
}
```

## 9. Authentication Flow

1. **Register** → Get user created confirmation
2. **Login** → Get access_token and refresh_token
3. **Use access_token** for all protected endpoints
4. **Refresh token** when access_token expires
5. **Logout** to invalidate tokens

## 10. Security Features

- ✅ JWT-based authentication
- ✅ Token blacklisting (Redis-based when available)
- ✅ Session management
- ✅ Password encryption (BCrypt)
- ✅ Role-based permissions
- ✅ Input validation
- ✅ CORS enabled
- ✅ Graceful Redis fallback

## 11. Service Status

- **Port**: 9091
- **Context Path**: /api/v1
- **MongoDB**: Connected (localhost:27017)
- **Redis**: Optional (localhost:6379) - graceful fallback
- **Eureka**: Registered
- **Health Check**: Available at /health

---

**Note**: Replace `YOUR_ACCESS_TOKEN_HERE`, `YOUR_REFRESH_TOKEN_HERE`, and `USER_ID_HERE` with actual values from your API responses.
