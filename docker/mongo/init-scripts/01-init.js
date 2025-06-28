// MongoDB Initialization Script for DesiFans Platform
// This script creates databases, users, and initial indexes

// Switch to admin database
db = db.getSiblingDB('admin');

// Create application user for desifans_users database
db.createUser({
  user: 'desifans_user',
  pwd: 'desifans123',
  roles: [
    { role: 'readWrite', db: 'desifans_users' },
    { role: 'readWrite', db: 'desifans_content' },
    { role: 'readWrite', db: 'desifans_subscriptions' }
  ]
});

// Switch to desifans_users database
db = db.getSiblingDB('desifans_users');

// Create collections with validation schemas
db.createCollection('users', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['username', 'email', 'passwordHash', 'role', 'status'],
      properties: {
        username: {
          bsonType: 'string',
          pattern: '^[a-zA-Z0-9_]{3,30}$',
          description: 'Username must be 3-30 characters, alphanumeric and underscore only'
        },
        email: {
          bsonType: 'string',
          pattern: '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$',
          description: 'Must be a valid email address'
        },
        role: {
          bsonType: 'string',
          enum: ['SUBSCRIBER', 'CREATOR', 'ADMIN'],
          description: 'Role must be SUBSCRIBER, CREATOR, or ADMIN'
        },
        status: {
          bsonType: 'string',
          enum: ['ACTIVE', 'SUSPENDED', 'DELETED', 'PENDING_VERIFICATION'],
          description: 'Status must be valid enum value'
        }
      }
    }
  }
});

// Create unique indexes
db.users.createIndex({ 'email': 1 }, { unique: true });
db.users.createIndex({ 'username': 1 }, { unique: true });

// Create compound indexes for common queries
db.users.createIndex({ 'role': 1, 'status': 1 });
db.users.createIndex({ 'status': 1, 'createdAt': 1 });
db.users.createIndex({ 'creatorProfile.category': 1, 'status': 1 });
db.users.createIndex({ 'profile.isVerified': 1, 'role': 1 });

// Create text indexes for search
db.users.createIndex({
  'profile.displayName': 'text',
  'creatorProfile.creatorName': 'text',
  'creatorProfile.category': 'text',
  'profile.bio': 'text'
}, {
  name: 'user_search_index',
  weights: {
    'profile.displayName': 10,
    'creatorProfile.creatorName': 10,
    'creatorProfile.category': 5,
    'profile.bio': 1
  }
});

// User Sessions Collection
db.createCollection('userSessions');
db.userSessions.createIndex({ 'userId': 1 });
db.userSessions.createIndex({ 'sessionToken': 1 }, { unique: true });
db.userSessions.createIndex({ 'isActive': 1, 'userId': 1 });
db.userSessions.createIndex({ 'expiresAt': 1 }, { expireAfterSeconds: 0 });

// Email Verifications Collection
db.createCollection('emailVerifications');
db.emailVerifications.createIndex({ 'userId': 1 });
db.emailVerifications.createIndex({ 'verificationToken': 1 }, { unique: true });
db.emailVerifications.createIndex({ 'email': 1 });
db.emailVerifications.createIndex({ 'expiresAt': 1 }, { expireAfterSeconds: 0 });

// User Activities Collection (Audit Log)
db.createCollection('userActivities');
db.userActivities.createIndex({ 'userId': 1, 'timestamp': -1 });
db.userActivities.createIndex({ 'action': 1, 'timestamp': -1 });
db.userActivities.createIndex({ 'timestamp': -1 });

// Insert sample admin user (password: admin123)
db.users.insertOne({
  username: 'admin',
  email: 'admin@desifans.com',
  passwordHash: '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewfYbU8xNqVMFaeu',
  role: 'ADMIN',
  status: 'ACTIVE',
  emailVerified: true,
  profile: {
    displayName: 'System Administrator',
    bio: 'Platform administrator',
    isVerified: true,
    verificationLevel: 'BLUE_CHECK',
    preferences: {
      language: 'en',
      timezone: 'UTC',
      emailNotifications: true,
      pushNotifications: true,
      marketingEmails: false,
      theme: 'dark'
    }
  },
  security: {
    lastLogin: new Date(),
    lastLoginIP: '127.0.0.1',
    failedLoginAttempts: 0,
    accountLockedUntil: null,
    passwordHistory: [],
    twoFactorEnabled: false
  },
  createdAt: new Date(),
  updatedAt: new Date(),
  lastActiveAt: new Date()
});

print('✅ DesiFans Users database initialized successfully');
print('📊 Created collections: users, userSessions, emailVerifications, userActivities');
print('🔍 Created indexes for optimal query performance');
print('👤 Sample admin user created (admin@desifans.com / admin123)');

// Switch to desifans_content database for future use
db = db.getSiblingDB('desifans_content');
print('📁 Content database ready for future initialization');

// Switch to desifans_subscriptions database for future use
db = db.getSiblingDB('desifans_subscriptions');
print('💳 Subscriptions database ready for future initialization');
