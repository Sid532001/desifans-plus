# User Service - Complete Implementation Roadmap

## 🎯 **Service Overview**

### **Purpose & Scope**
The User Service is the **authentication and user management foundation** for your OnlyFans clone. It handles user lifecycle, authentication, authorization, and profile management for both creators and subscribers.

### **Technical Stack Decision**
- **Database**: MongoDB (Content-focused, flexible user profiles)
- **Authentication**: JWT + Spring Security
- **Cache**: Redis (Session management, token blacklisting)
- **Port**: 8081
- **Dependencies**: Discovery Service, Config Service, API Gateway

---

## 📊 **MongoDB Schema Design**

### **1. Users Collection**
```javascript
{
  _id: ObjectId("64f8b123456789abcdef"),
  username: "creator_john",
  email: "john@example.com",
  passwordHash: "$2a$12$hashedpassword...",
  role: "CREATOR", // CREATOR, SUBSCRIBER, ADMIN
  status: "ACTIVE", // ACTIVE, SUSPENDED, DELETED, PENDING_VERIFICATION
  emailVerified: true,
  phoneNumber: "+1234567890",
  dateOfBirth: ISODate("1995-05-15"),
  
  // Basic Profile
  profile: {
    displayName: "John Creator",
    bio: "Content creator specializing in fitness",
    location: "Los Angeles, CA",
    website: "https://johncreator.com",
    profilePicture: "https://storage.example.com/profiles/john_pic.jpg",
    bannerImage: "https://storage.example.com/banners/john_banner.jpg",
    isVerified: true,
    verificationLevel: "ID_VERIFIED", // EMAIL, PHONE, ID_VERIFIED, BLUE_CHECK
    socialLinks: {
      instagram: "@johncreator",
      twitter: "@johncreator",
      tiktok: "@johncreator"
    },
    preferences: {
      language: "en",
      timezone: "America/Los_Angeles",
      emailNotifications: true,
      pushNotifications: true,
      marketingEmails: false,
      theme: "dark"
    }
  },
  
  // Creator-specific data (only for CREATOR role)
  creatorProfile: {
    creatorName: "John Fitness",
    category: "FITNESS", // FITNESS, LIFESTYLE, ADULT, MUSIC, etc.
    subscriptionPrice: 9.99,
    currency: "USD",
    totalEarnings: 5420.50, // Cached from Payment Service
    subscriberCount: 245, // Cached from Subscription Service
    contentCount: 87, // Cached from Content Service
    averageRating: 4.8,
    isAcceptingTips: true,
    tipMenu: [
      { amount: 5, message: "Thanks!" },
      { amount: 10, message: "Buy me coffee" },
      { amount: 25, message: "Special request" }
    ],
    contentWarnings: ["adult_content", "explicit_language"],
    creatorSettings: {
      autoApproveMessages: false,
      allowCustomRequests: true,
      minimumTip: 1.00,
      welcomeMessage: "Thanks for subscribing!",
      ppvPricing: {
        photos: 5.00,
        videos: 15.00,
        customContent: 50.00
      }
    },
    onboardingCompleted: true,
    verificationDocuments: {
      idDocumentUrl: "https://secure.example.com/docs/john_id.pdf",
      idVerificationStatus: "APPROVED",
      taxFormUrl: "https://secure.example.com/tax/john_w9.pdf",
      bankingSetup: true
    }
  },
  
  // Security & Auth
  security: {
    lastLogin: ISODate("2024-01-15T10:30:00Z"),
    lastLoginIP: "192.168.1.100",
    failedLoginAttempts: 0,
    accountLockedUntil: null,
    passwordHistory: [
      "$2a$12$oldpasswordhash1...",
      "$2a$12$oldpasswordhash2..."
    ], // Last 5 passwords
    twoFactorEnabled: false,
    recoveryEmail: "recovery@example.com",
    securityQuestions: [
      {
        question: "What was your first pet's name?",
        answerHash: "$2a$12$answerhash..."
      }
    ]
  },
  
  // Timestamps
  createdAt: ISODate("2024-01-01T00:00:00Z"),
  updatedAt: ISODate("2024-01-15T10:30:00Z"),
  lastActiveAt: ISODate("2024-01-15T10:30:00Z")
}
```

### **2. User Sessions Collection**
```javascript
{
  _id: ObjectId("64f8b123456789abcdef"),
  userId: ObjectId("64f8b123456789abcdef"),
  sessionToken: "jwt_refresh_token_string",
  accessToken: "jwt_access_token_string",
  deviceInfo: {
    userAgent: "Mozilla/5.0 (Windows NT 10.0; Win64; x64)...",
    ipAddress: "192.168.1.100",
    device: "Desktop",
    browser: "Chrome",
    os: "Windows",
    location: {
      country: "US",
      city: "Los Angeles",
      coordinates: [-118.2437, 34.0522]
    }
  },
  isActive: true,
  createdAt: ISODate("2024-01-15T10:00:00Z"),
  expiresAt: ISODate("2024-01-22T10:00:00Z"),
  lastActivity: ISODate("2024-01-15T10:30:00Z")
}
```

### **3. Email Verifications Collection**
```javascript
{
  _id: ObjectId("64f8b123456789abcdef"),
  userId: ObjectId("64f8b123456789abcdef"),
  email: "john@example.com",
  verificationToken: "random_secure_token_string",
  tokenType: "EMAIL_VERIFICATION", // EMAIL_VERIFICATION, PASSWORD_RESET, EMAIL_CHANGE
  isUsed: false,
  createdAt: ISODate("2024-01-15T10:00:00Z"),
  expiresAt: ISODate("2024-01-15T22:00:00Z") // 12 hours
}
```

### **4. User Activities Collection** (Audit Log)
```javascript
{
  _id: ObjectId("64f8b123456789abcdef"),
  userId: ObjectId("64f8b123456789abcdef"),
  action: "LOGIN", // LOGIN, PROFILE_UPDATE, PASSWORD_CHANGE, CREATOR_UPGRADE
  details: {
    oldValues: {},
    newValues: {},
    additionalInfo: {}
  },
  ipAddress: "192.168.1.100",
  userAgent: "Mozilla/5.0...",
  timestamp: ISODate("2024-01-15T10:30:00Z")
}
```

---

## 🔐 **Authentication & Security Architecture**

### **JWT Token Strategy**

#### **Access Token (15 minutes)**
```json
{
  "sub": "64f8b123456789abcdef",
  "username": "creator_john",
  "email": "john@example.com",
  "role": "CREATOR",
  "permissions": [
    "CREATE_CONTENT",
    "MANAGE_SUBSCRIPTIONS", 
    "VIEW_EARNINGS",
    "LIVE_STREAM"
  ],
  "iat": 1705320600,
  "exp": 1705321500,
  "type": "ACCESS_TOKEN",
  "sessionId": "session_uuid"
}
```

#### **Refresh Token (7 days)**
```json
{
  "sub": "64f8b123456789abcdef",
  "sessionId": "session_uuid",
  "iat": 1705320600,
  "exp": 1705925400,
  "type": "REFRESH_TOKEN"
}
```

### **Security Features**
- **Password**: BCrypt with 12 rounds
- **Rate Limiting**: 5 failed attempts = 15 min lockout
- **Session Management**: Max 5 active sessions per user
- **Token Blacklisting**: Redis with TTL
- **IP Tracking**: Suspicious login detection
- **2FA Support**: TOTP (Google Authenticator) - Optional
- **Device Fingerprinting**: Track and alert on new devices

---

## 📱 **API Endpoints Design**

### **Authentication Endpoints**
```
POST /api/v1/auth/register
POST /api/v1/auth/login
POST /api/v1/auth/logout
POST /api/v1/auth/logout-all                # Logout from all devices
POST /api/v1/auth/refresh-token
POST /api/v1/auth/verify-email
POST /api/v1/auth/resend-verification
POST /api/v1/auth/forgot-password
POST /api/v1/auth/reset-password
POST /api/v1/auth/change-password
POST /api/v1/auth/enable-2fa
POST /api/v1/auth/verify-2fa
POST /api/v1/auth/disable-2fa
```

### **User Profile Endpoints**
```
GET /api/v1/users/profile                   # Own profile
PUT /api/v1/users/profile                   # Update profile
DELETE /api/v1/users/account                # Delete account (soft delete)
GET /api/v1/users/{userId}/public-profile   # Public profile view
PUT /api/v1/users/preferences               # Update preferences
GET /api/v1/users/sessions                  # Active sessions
DELETE /api/v1/users/sessions/{sessionId}   # Logout specific session
POST /api/v1/users/upload-avatar            # Upload profile picture
POST /api/v1/users/upload-banner            # Upload banner image
GET /api/v1/users/activity-log              # User activity history
```

### **Creator Profile Endpoints**
```
POST /api/v1/creators/upgrade               # Become a creator
GET /api/v1/creators/profile                # Creator profile
PUT /api/v1/creators/profile                # Update creator profile
GET /api/v1/creators/{creatorId}/public     # Public creator profile
GET /api/v1/creators/search                 # Search creators
GET /api/v1/creators/categories             # Available categories
PUT /api/v1/creators/subscription-price     # Update subscription price
PUT /api/v1/creators/tip-menu               # Update tip menu
GET /api/v1/creators/verification-status    # Check verification status
POST /api/v1/creators/submit-verification   # Submit verification docs
GET /api/v1/creators/earnings               # Earnings summary
GET /api/v1/creators/subscribers            # Subscriber list
PUT /api/v1/creators/settings               # Creator settings
```

### **Admin Endpoints**
```
GET /api/v1/admin/users                     # User management
GET /api/v1/admin/users/{userId}            # Get specific user
PUT /api/v1/admin/users/{userId}/status     # Update user status
GET /api/v1/admin/creators/pending-verification  # Pending verifications
POST /api/v1/admin/creators/{creatorId}/verify   # Approve creator
POST /api/v1/admin/creators/{creatorId}/reject   # Reject creator
GET /api/v1/admin/reports                   # User reports
GET /api/v1/admin/analytics                 # User analytics
POST /api/v1/admin/users/{userId}/suspend   # Suspend user
POST /api/v1/admin/users/{userId}/unsuspend # Unsuspend user
```

---

## 🔄 **Business Logic & Workflows**

### **1. User Registration Flow**
```
┌─────────────────────────────────────────────────────────┐
│                  User Registration                      │
└─────────────────────────────────────────────────────────┘
                           │
                    ┌─────────────┐
                    │ Input       │
                    │ Validation  │
                    └─────────────┘
                           │
            ┌──────────────┼──────────────┐
            │              │              │
    ┌──────────────┐ ┌────────────┐ ┌──────────────┐
    │ Email        │ │ Username   │ │ Password     │
    │ Format &     │ │ 3-30 chars │ │ 8+ chars     │
    │ Uniqueness   │ │ Available  │ │ Complexity   │
    └──────────────┘ └────────────┘ └──────────────┘
                           │
                    ┌─────────────┐
                    │ Age Check   │
                    │ Must be 18+ │
                    └─────────────┘
                           │
                    ┌─────────────┐
                    │ Create      │
                    │ Account     │
                    └─────────────┘
                           │
            ┌──────────────┼──────────────┐
            │              │              │
    ┌──────────────┐ ┌────────────┐ ┌──────────────┐
    │ Hash         │ │ Generate   │ │ Send         │
    │ Password     │ │ Token      │ │ Verification │
    │ (BCrypt 12)  │ │            │ │ Email        │
    └──────────────┘ └────────────┘ └──────────────┘
                           │
                    ┌─────────────┐
                    │ Return      │
                    │ Success     │
                    └─────────────┘
```

### **2. Login Flow**
```
┌─────────────────────────────────────────────────────────┐
│                    User Login                           │
└─────────────────────────────────────────────────────────┘
                           │
                    ┌─────────────┐
                    │ Security    │
                    │ Checks      │
                    └─────────────┘
                           │
            ┌──────────────┼──────────────┐
            │              │              │
    ┌──────────────┐ ┌────────────┐ ┌──────────────┐
    │ Account      │ │ Email      │ │ Account      │
    │ Exists &     │ │ Verified   │ │ Not Locked   │
    │ Active       │ │            │ │              │
    └──────────────┘ └────────────┘ └──────────────┘
                           │
                    ┌─────────────┐
                    │ Verify      │
                    │ Password    │
                    └─────────────┘
                           │
                    ┌─────────────┐
                    │ Update      │
                    │ Login Info  │
                    └─────────────┘
                           │
            ┌──────────────┼──────────────┐
            │              │              │
    ┌──────────────┐ ┌────────────┐ ┌──────────────┐
    │ Generate     │ │ Create     │ │ Set HTTP     │
    │ Access Token │ │ Session    │ │ Cookies      │
    │ (15 min)     │ │ (7 days)   │ │              │
    └──────────────┘ └────────────┘ └──────────────┘
                           │
                    ┌─────────────┐
                    │ Return      │
                    │ User Data   │
                    └─────────────┘
```

### **3. Creator Upgrade Flow**
```
┌─────────────────────────────────────────────────────────┐
│                Creator Onboarding                       │
└─────────────────────────────────────────────────────────┘
                           │
                    ┌─────────────┐
                    │ Eligibility │
                    │ Check       │
                    └─────────────┘
                           │
            ┌──────────────┼──────────────┐
            │              │              │
    ┌──────────────┐ ┌────────────┐ ┌──────────────┐
    │ Account      │ │ Age 18+    │ │ Good         │
    │ Verified     │ │ Verified   │ │ Standing     │
    └──────────────┘ └────────────┘ └──────────────┘
                           │
                    ┌─────────────┐
                    │ Profile     │
                    │ Setup       │
                    └─────────────┘
                           │
    ┌──────────────┬────────────┬──────────────┬──────────────┐
    │              │            │              │              │
┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────────┐
│ Basic    │ │ Category │ │ Pricing  │ │ Content      │
│ Info     │ │ Select   │ │ Setup    │ │ Warnings     │
└──────────┘ └──────────┘ └──────────┘ └──────────────┘
                           │
                    ┌─────────────┐
                    │ Document    │
                    │ Upload      │
                    └─────────────┘
                           │
            ┌──────────────┼──────────────┐
            │              │              │
    ┌──────────────┐ ┌────────────┐ ┌──────────────┐
    │ ID Document  │ │ Tax Form   │ │ Banking      │
    │ Verification │ │ (W9/W8)    │ │ Setup        │
    └──────────────┘ └────────────┘ └──────────────┘
                           │
                    ┌─────────────┐
                    │ Admin       │
                    │ Review      │
                    └─────────────┘
                           │
                ┌─────────┴─────────┐
                │                   │
        ┌──────────────┐    ┌──────────────┐
        │ Approved     │    │ Rejected     │
        │ - Role Update│    │ - Send       │
        │ - Features   │    │   Reason     │
        │ - Notification│    │ - Allow      │
        └──────────────┘    │   Resubmit   │
                            └──────────────┘
```

---

## ⚡ **Performance & Optimization**

### **Redis Caching Strategy**

#### **Cache Keys & TTL**
```
user:profile:{userId}               → 1 hour TTL
user:sessions:{userId}              → 7 days TTL
user:permissions:{userId}           → 30 minutes TTL
creator:public:{creatorId}          → 30 minutes TTL
auth:blacklist:{tokenId}            → Until token expiry
rate_limit:login:{ip}               → 15 minutes TTL
rate_limit:api:{userId}             → 1 hour TTL
email:verification:{email}          → 12 hours TTL
password:reset:{email}              → 1 hour TTL
```

#### **Cache Patterns**
```java
// Cache-aside pattern for user profiles
@Cacheable(value = "user_profiles", key = "#userId")
public UserProfile getUserProfile(String userId);

// Write-through pattern for user updates
@CachePut(value = "user_profiles", key = "#userId")
public UserProfile updateUserProfile(String userId, UserProfile profile);

// Cache eviction on sensitive operations
@CacheEvict(value = "user_profiles", key = "#userId")
public void deleteUser(String userId);
```

### **Database Optimization**

#### **MongoDB Indexes**
```javascript
// Unique indexes
db.users.createIndex({ "email": 1 }, { unique: true })
db.users.createIndex({ "username": 1 }, { unique: true })

// Compound indexes for queries
db.users.createIndex({ "role": 1, "status": 1 })
db.users.createIndex({ "creatorProfile.category": 1, "status": 1 })
db.users.createIndex({ "profile.isVerified": 1, "role": 1 })

// TTL indexes for auto-cleanup
db.userSessions.createIndex({ "expiresAt": 1 }, { expireAfterSeconds: 0 })
db.emailVerifications.createIndex({ "expiresAt": 1 }, { expireAfterSeconds: 0 })

// Search indexes
db.users.createIndex({ 
  "profile.displayName": "text", 
  "creatorProfile.creatorName": "text",
  "creatorProfile.category": "text"
})
```

#### **Query Optimization**
```javascript
// Efficient creator search with pagination
db.users.find({
  "role": "CREATOR",
  "status": "ACTIVE",
  "creatorProfile.category": { $in: ["FITNESS", "LIFESTYLE"] }
})
.project({
  "username": 1,
  "profile.displayName": 1,
  "profile.profilePicture": 1,
  "creatorProfile.subscriptionPrice": 1,
  "creatorProfile.subscriberCount": 1
})
.sort({ "creatorProfile.subscriberCount": -1 })
.limit(20)
.skip(offset)
```

---

## 🛡️ **Error Handling & Validation**

### **Validation Rules**

#### **Registration Validation**
```java
public class UserRegistrationRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be 3-30 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]",
             message = "Password must contain uppercase, lowercase, number, and special character")
    private String password;
    
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    
    @AssertTrue(message = "Must be 18 or older")
    private boolean isAdult() {
        return dateOfBirth != null && 
               Period.between(dateOfBirth, LocalDate.now()).getYears() >= 18;
    }
}
```

### **Error Response Format**
```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Validation failed",
    "details": [
      {
        "field": "email",
        "code": "EMAIL_ALREADY_EXISTS",
        "message": "This email is already registered"
      },
      {
        "field": "password",
        "code": "WEAK_PASSWORD",
        "message": "Password must contain uppercase, lowercase, number, and special character"
      }
    ],
    "timestamp": "2024-01-15T10:30:00Z",
    "path": "/api/v1/auth/register",
    "requestId": "req-123456"
  }
}
```

### **Custom Exception Types**
```java
// User-related exceptions
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String userId) {
        super("User not found with ID: " + userId);
    }
}

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Invalid username or password");
    }
}

@ResponseStatus(HttpStatus.LOCKED)
public class AccountLockedException extends RuntimeException {
    private final LocalDateTime lockExpiry;
    
    public AccountLockedException(LocalDateTime lockExpiry) {
        super("Account is locked until: " + lockExpiry);
        this.lockExpiry = lockExpiry;
    }
}

@ResponseStatus(HttpStatus.FORBIDDEN)
public class EmailNotVerifiedException extends RuntimeException {
    public EmailNotVerifiedException() {
        super("Email address must be verified before login");
    }
}

// Token-related exceptions
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException() {
        super("Token has expired");
    }
}

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super("Invalid token: " + message);
    }
}

// Creator-related exceptions
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CreatorUpgradeException extends RuntimeException {
    public CreatorUpgradeException(String reason) {
        super("Cannot upgrade to creator: " + reason);
    }
}
```

---

## 📊 **Event-Driven Architecture**

### **Events Published by User Service**

#### **User Lifecycle Events**
```javascript
// User Registration Event
{
  eventType: "USER_REGISTERED",
  eventId: "evt_12345",
  userId: "64f8b123456789abcdef",
  username: "john_doe",
  email: "john@example.com",
  role: "SUBSCRIBER",
  timestamp: "2024-01-15T10:00:00Z",
  metadata: {
    source: "WEB_APP",
    userAgent: "Mozilla/5.0...",
    ipAddress: "192.168.1.100"
  }
}

// Creator Profile Created Event
{
  eventType: "CREATOR_PROFILE_CREATED",
  eventId: "evt_12346",
  userId: "64f8b123456789abcdef",
  creatorId: "64f8b123456789abcdef",
  category: "FITNESS",
  subscriptionPrice: 9.99,
  currency: "USD",
  timestamp: "2024-01-15T10:30:00Z"
}

// Profile Updated Event
{
  eventType: "USER_PROFILE_UPDATED",
  eventId: "evt_12347",
  userId: "64f8b123456789abcdef",
  changes: {
    displayName: {
      from: "John Doe",
      to: "John Creator"
    },
    bio: {
      from: null,
      to: "Fitness enthusiast..."
    }
  },
  timestamp: "2024-01-15T11:00:00Z"
}

// User Deleted Event
{
  eventType: "USER_DELETED",
  eventId: "evt_12348",
  userId: "64f8b123456789abcdef",
  email: "john@example.com",
  deletionReason: "USER_REQUEST",
  retentionPolicy: "SOFT_DELETE_30_DAYS",
  timestamp: "2024-01-15T12:00:00Z"
}
```

### **Events Consumed by User Service**

#### **From Payment Service**
```javascript
// Payment Completed - Update Creator Earnings
{
  eventType: "PAYMENT_COMPLETED",
  eventId: "evt_pay_123",
  creatorId: "64f8b123456789abcdef",
  amount: 9.99,
  currency: "USD",
  type: "SUBSCRIPTION",
  timestamp: "2024-01-15T10:00:00Z"
}

// Tip Received - Update Creator Earnings
{
  eventType: "TIP_RECEIVED",
  eventId: "evt_tip_123",
  creatorId: "64f8b123456789abcdef",
  amount: 25.00,
  currency: "USD",
  fromUserId: "64f8b123456789abcdff",
  timestamp: "2024-01-15T11:00:00Z"
}
```

#### **From Subscription Service**
```javascript
// Subscription Created - Update Subscriber Count
{
  eventType: "SUBSCRIPTION_CREATED",
  eventId: "evt_sub_123",
  creatorId: "64f8b123456789abcdef",
  subscriberId: "64f8b123456789abcdff",
  amount: 9.99,
  timestamp: "2024-01-15T10:00:00Z"
}

// Subscription Cancelled - Update Subscriber Count
{
  eventType: "SUBSCRIPTION_CANCELLED",
  eventId: "evt_sub_124",
  creatorId: "64f8b123456789abcdef",
  subscriberId: "64f8b123456789abcdff",
  reason: "USER_REQUEST",
  timestamp: "2024-01-15T12:00:00Z"
}
```

#### **From Content Service**
```javascript
// Content Published - Update Content Count
{
  eventType: "CONTENT_PUBLISHED",
  eventId: "evt_content_123",
  creatorId: "64f8b123456789abcdef",
  contentId: "64f8b123456789abcdee",
  contentType: "IMAGE",
  timestamp: "2024-01-15T10:00:00Z"
}
```

---

## 🧪 **Testing Strategy**

### **Test Pyramid Structure**

#### **Unit Tests (70%)**
```java
// Service Layer Tests
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Test
    void shouldCreateUserSuccessfully() {
        // Given
        UserRegistrationRequest request = createValidRequest();
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        
        // When
        UserResponse response = userService.registerUser(request);
        
        // Then
        assertThat(response.getUsername()).isEqualTo(request.getUsername());
        verify(emailService).sendVerificationEmail(any());
    }
    
    @Test
    void shouldThrowExceptionWhenEmailExists() {
        // Given
        UserRegistrationRequest request = createValidRequest();
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);
        
        // When & Then
        assertThrows(EmailAlreadyExistsException.class, 
                    () -> userService.registerUser(request));
    }
}

// Security Tests
@ExtendWith(SpringExtension.class)
class JwtTokenServiceTest {
    
    @Test
    void shouldGenerateValidAccessToken() {
        // Given
        User user = createTestUser();
        
        // When
        String token = jwtTokenService.generateAccessToken(user);
        
        // Then
        assertThat(jwtTokenService.validateToken(token)).isTrue();
        assertThat(jwtTokenService.getUserIdFromToken(token)).isEqualTo(user.getId());
    }
}
```

#### **Integration Tests (20%)**
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
class UserControllerIntegrationTest {
    
    @Test
    @Order(1)
    void shouldRegisterNewUser() throws Exception {
        // Given
        UserRegistrationRequest request = createValidRequest();
        
        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(request.getUsername()))
                .andExpect(jsonPath("$.email").value(request.getEmail()));
    }
    
    @Test
    @Order(2)
    void shouldLoginSuccessfully() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("testuser", "password");
        
        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(cookie().exists("refreshToken"));
    }
}
```

#### **E2E Tests (10%)**
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestContainers
class UserJourneyE2ETest {
    
    @Container
    static MongoDBContainer mongoContainer = new MongoDBContainer("mongo:5.0");
    
    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);
    
    @Test
    void completeUserJourney() {
        // 1. Register new user
        // 2. Verify email
        // 3. Login
        // 4. Update profile
        // 5. Upgrade to creator
        // 6. Submit verification
        // 7. Admin approval
        // 8. Creator profile active
    }
}
```

### **Performance Tests**
```java
@Test
void shouldHandleConcurrentLogins() {
    // Test 1000 concurrent login requests
    // Verify response times < 500ms
    // Verify no data corruption
}

@Test
void shouldCacheUserProfilesEffectively() {
    // Verify cache hit ratio > 80%
    // Verify cache invalidation works correctly
}
```

---

## 🚀 **Implementation Roadmap**

### **Phase 1: Core Authentication (Week 1)**

#### **Day 1-2: Project Setup & Foundation**
**Tasks:**
- [ ] Create Spring Boot project with User Service
- [ ] Setup MongoDB connection and configuration
- [ ] Configure Redis for caching and sessions
- [ ] Setup basic project structure (controllers, services, repositories)
- [ ] Configure Spring Security basics
- [ ] Setup health check endpoints

**Dependencies:**
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-mongodb</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
</dependencies>
```

#### **Day 3-4: Basic Authentication**
**Tasks:**
- [ ] Create User entity with MongoDB annotations
- [ ] Implement UserRepository with custom queries
- [ ] Setup JWT token generation and validation
- [ ] Implement password hashing with BCrypt (12 rounds)
- [ ] Create registration endpoint with basic validation
- [ ] Create login endpoint with credential verification
- [ ] Implement basic error handling

**Deliverables:**
- User registration working
- User login with JWT token generation
- Password hashing implemented
- Basic input validation

#### **Day 5-7: Security & Session Management**
**Tasks:**
- [ ] Implement JWT filter for request authentication
- [ ] Setup session management in MongoDB
- [ ] Implement rate limiting for login attempts
- [ ] Add account lockout mechanism (5 attempts = 15 min lockout)
- [ ] Create logout endpoint with token blacklisting
- [ ] Setup comprehensive input validation
- [ ] Implement security headers and CORS configuration

**Deliverables:**
- JWT authentication filter working
- Rate limiting implemented
- Account lockout mechanism
- Session management
- Security headers configured

### **Phase 2: User Profiles & Email Verification (Week 2)**

#### **Day 8-10: Profile Management**
**Tasks:**
- [ ] Implement user profile CRUD operations
- [ ] Create profile update endpoint with validation
- [ ] Add profile picture upload functionality
- [ ] Implement user preferences management
- [ ] Create public profile view endpoint
- [ ] Add profile validation rules
- [ ] Implement caching for frequently accessed profiles

**Deliverables:**
- Complete profile management
- Profile picture upload
- User preferences system
- Profile caching implemented

#### **Day 11-14: Email Verification & Security**
**Tasks:**
- [ ] Implement email verification system
- [ ] Create email verification token generation
- [ ] Setup email templates and sending service
- [ ] Implement password reset functionality
- [ ] Add device tracking and notifications
- [ ] Create session management UI endpoints
- [ ] Implement account recovery options

**Deliverables:**
- Email verification working
- Password reset functionality
- Device tracking implemented
- Session management endpoints

### **Phase 3: Creator Features (Week 3)**

#### **Day 15-17: Creator Profile System**
**Tasks:**
- [ ] Implement creator profile creation
- [ ] Setup creator categories and validation
- [ ] Add subscription pricing management
- [ ] Implement tip menu functionality
- [ ] Create creator settings management
- [ ] Add creator profile validation rules
- [ ] Implement creator search functionality

**Deliverables:**
- Creator profile creation
- Subscription pricing system
- Tip menu management
- Creator search API

#### **Day 18-21: Creator Verification System**
**Tasks:**
- [ ] Implement ID document upload system
- [ ] Create verification workflow and status tracking
- [ ] Setup admin verification approval process
- [ ] Add tax document handling (W9/W8 forms)
- [ ] Implement banking integration preparation
- [ ] Create verification status endpoints
- [ ] Add verification notification system

**Deliverables:**
- Document upload system
- Verification workflow
- Admin approval process
- Verification status tracking

### **Phase 4: Advanced Features & Production Readiness (Week 4)**

#### **Day 22-24: Enhanced Security & Monitoring**
**Tasks:**
- [ ] Implement 2FA (TOTP) support (optional)
- [ ] Add advanced audit logging
- [ ] Setup suspicious activity detection
- [ ] Implement IP-based security measures
- [ ] Add comprehensive monitoring and metrics
- [ ] Setup application performance monitoring
- [ ] Implement health check endpoints

**Deliverables:**
- 2FA implementation
- Audit logging system
- Activity monitoring
- Performance metrics

#### **Day 25-28: Admin Features & Final Integration**
**Tasks:**
- [ ] Implement admin user management interface
- [ ] Add creator verification admin panel
- [ ] Setup user status management (suspend/unsuspend)
- [ ] Implement user analytics and reporting
- [ ] Add bulk operations for admin
- [ ] Setup comprehensive API documentation
- [ ] Implement event publishing for microservices integration

**Deliverables:**
- Admin management system
- User analytics
- API documentation
- Event system integration
- Production-ready service

---

## 📋 **Dependencies & Configuration**

### **Complete Maven Dependencies**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>
    
    <groupId>com.desifans</groupId>
    <artifactId>user-service</artifactId>
    <version>1.0.0</version>
    <name>user-service</name>
    <description>User Service for DesiFans Platform</description>
    
    <properties>
        <java.version>17</java.version>
        <spring-cloud.version>2023.0.0</spring-cloud.version>
        <jwt.version>0.11.5</jwt.version>
    </properties>
    
    <dependencies>
        <!-- Core Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-mail</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        
        <!-- Spring Cloud -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-bootstrap</artifactId>
        </dependency>
        
        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jwt.version}</version>
        </dependency>
        
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>${jwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>${jwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        
        <!-- Password Encryption -->
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-crypto</artifactId>
        </dependency>
        
        <!-- Documentation -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>2.2.0</version>
        </dependency>
        
        <!-- Event Handling -->
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
        </dependency>
        
        <!-- File Upload -->
        <dependency>
            <groupId>commons-fileupload</groupId>
            <artifactId>commons-fileupload</artifactId>
            <version>1.5</version>
        </dependency>
        
        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>mongodb</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

### **Application Configuration**
```yaml
# application.yml
spring:
  application:
    name: user-service
  
  data:
    mongodb:
      uri: mongodb://localhost:27017/desifans_users
      auto-index-creation: true
  
  redis:
    host: localhost
    port: 6379
    password: 
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
  
  security:
    jwt:
      secret: ${JWT_SECRET:your-secret-key-here}
      access-token-expiration: 900000  # 15 minutes
      refresh-token-expiration: 604800000  # 7 days
  
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${EMAIL_USERNAME:your-email@gmail.com}
    password: ${EMAIL_PASSWORD:your-app-password}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

server:
  port: 8081

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always

logging:
  level:
    com.desifans.userservice: DEBUG
    org.springframework.security: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/user-service.log

# Custom application properties
app:
  security:
    max-login-attempts: 5
    lockout-duration: 900000  # 15 minutes
    max-sessions-per-user: 5
  
  email:
    verification-expiry: 43200000  # 12 hours
    reset-expiry: 3600000  # 1 hour
  
  creator:
    verification-required: true
    minimum-age: 18
```

---

## 📊 **Monitoring & Analytics**

### **Key Metrics to Track**

#### **Business Metrics**
```
- User registration rate (daily/weekly)
- Email verification completion rate
- Login success/failure rates
- Creator conversion rate (subscriber → creator)
- Profile completion rates
- User retention rates (7-day, 30-day)
- Average session duration
- Password reset frequency
```

#### **Technical Metrics**
```
- API response times (p50, p95, p99)
- Database query performance
- Cache hit rates (Redis)
- JWT token generation/validation times
- Error rates by endpoint
- Concurrent user sessions
- Memory and CPU usage
- Database connection pool utilization
```

#### **Security Metrics**
```
- Failed login attempts by IP
- Account lockout events
- Suspicious activity detections
- Password strength distribution
- 2FA adoption rate
- Token blacklist operations
- Session hijacking attempts
```

### **Monitoring Implementation**
```java
@Component
public class UserMetrics {
    
    private final Counter registrationCounter;
    private final Counter loginAttempts;
    private final Timer loginDuration;
    private final Gauge activeUserSessions;
    
    public UserMetrics(MeterRegistry meterRegistry) {
        this.registrationCounter = Counter.builder("user.registrations.total")
                .description("Total user registrations")
                .tag("service", "user-service")
                .register(meterRegistry);
                
        this.loginAttempts = Counter.builder("user.login.attempts")
                .description("Login attempts")
                .register(meterRegistry);
                
        this.loginDuration = Timer.builder("user.login.duration")
                .description("Login request duration")
                .register(meterRegistry);
    }
    
    public void incrementRegistrations() {
        registrationCounter.increment();
    }
    
    public void recordLoginAttempt(boolean successful) {
        loginAttempts.increment(
            Tags.of("result", successful ? "success" : "failure")
        );
    }
}
```

---

## 🔒 **Security Considerations**

### **Data Protection**
```java
// Sensitive data encryption at rest
@Encrypted
private String phoneNumber;

@Encrypted 
private String socialSecurityNumber;

// PII handling with audit trail
@AuditableField
private String email;

@AuditableField
private String firstName;
```

### **API Security**
```java
// Rate limiting configuration
@RateLimited(permits = 5, period = 1, unit = TimeUnit.MINUTES)
@PostMapping("/auth/login")
public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request);

// Input sanitization
@PostMapping("/profile")
public ResponseEntity<UserProfile> updateProfile(
    @Valid @Sanitized @RequestBody UserProfileUpdateRequest request);
```

### **GDPR Compliance**
```java
// Data export capability
@GetMapping("/users/{userId}/export")
public ResponseEntity<UserDataExport> exportUserData(@PathVariable String userId);

// Data deletion with retention policy
@DeleteMapping("/users/{userId}")
public ResponseEntity<Void> deleteUser(
    @PathVariable String userId,
    @RequestParam DeletionReason reason);
```

---

## 🚀 **Production Deployment Checklist**

### **Pre-Deployment**
- [ ] All tests passing (Unit, Integration, E2E)
- [ ] Security audit completed
- [ ] Performance testing completed
- [ ] Database indexes optimized
- [ ] Caching strategy implemented
- [ ] Monitoring and alerting configured
- [ ] Documentation completed
- [ ] API rate limiting configured
- [ ] SSL/TLS certificates installed
- [ ] Environment-specific configurations set

### **Deployment**
- [ ] Blue-green deployment strategy
- [ ] Database migration scripts tested
- [ ] Rollback plan prepared
- [ ] Health checks configured
- [ ] Load balancer configuration
- [ ] CDN setup for static assets
- [ ] Backup and recovery procedures tested

### **Post-Deployment**
- [ ] Smoke tests executed
- [ ] Monitoring dashboards verified
- [ ] Performance metrics baseline established
- [ ] Security scanning completed
- [ ] User acceptance testing
- [ ] Documentation updated
- [ ] Team training completed

---

## 📚 **Additional Resources**

### **Documentation Links**
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [MongoDB Java Driver](https://www.mongodb.com/docs/drivers/java/sync/current/)
- [JWT Best Practices](https://datatracker.ietf.org/doc/html/draft-ietf-oauth-jwt-bcp)
- [Redis Spring Data](https://docs.spring.io/spring-data/redis/docs/current/reference/html/)

### **Tools & Libraries**
- **Testing**: JUnit 5, Mockito, TestContainers
- **Documentation**: OpenAPI 3, Swagger UI
- **Monitoring**: Micrometer, Prometheus, Grafana
- **Security**: OWASP dependency check, SonarQube
- **Performance**: JMeter, Gatling

---

This comprehensive roadmap provides a complete implementation guide for the User Service, covering all aspects from database design to production deployment. Follow this roadmap phase by phase to build a robust, secure, and scalable user management system for your OnlyFans clone.
