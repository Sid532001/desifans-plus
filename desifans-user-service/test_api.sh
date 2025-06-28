#!/bin/bash

echo "=== API End-to-End Testing ==="

echo "1. Testing Health Check..."
curl -s http://localhost:9091/api/v1/health | jq .

echo -e "\n2. Testing User Registration..."
curl -s -X POST http://localhost:9091/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser'$(date +%s)'",
    "email": "test'$(date +%s)'@example.com",
    "password": "SecurePass123!",
    "dateOfBirth": "1990-01-01"
  }' | jq .

echo -e "\n3. Testing Login..."
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:9091/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "loginIdentifier": "testuser'$(date +%s)'",
    "password": "SecurePass123!"
  }')

echo $LOGIN_RESPONSE | jq .

# Extract tokens (you'll need to update this based on actual response structure)
ACCESS_TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.data.accessToken')
echo "Access Token: $ACCESS_TOKEN"

echo -e "\n4. Testing Profile Access..."
curl -s -X GET http://localhost:9091/api/v1/users/profile \
  -H "Authorization: Bearer $ACCESS_TOKEN" | jq .

echo -e "\nAll tests completed!"