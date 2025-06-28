# Live Streaming Service - Technical Specifications

## Overview
The Live Streaming Service enables creators to broadcast real-time video content to their subscribers with monetization features like tips, paid access, and chat interaction.

## Core Features

### 1. Stream Management
- **Stream Creation**: Creators can schedule and start live streams
- **Stream Settings**: Quality, recording, chat, tips configuration
- **Access Control**: Public, subscriber-only, or ticketed streams
- **Stream Recording**: Auto-save streams for later viewing

### 2. Real-time Features
- **Video Streaming**: High-quality video broadcast using WebRTC/Agora.io
- **Live Chat**: Real-time messaging during streams
- **Viewer Count**: Live viewer statistics and analytics
- **Interactive Elements**: Reactions, polls, Q&A sessions

### 3. Monetization
- **Live Tips**: Viewers can tip creators during streams
- **Paid Access**: Ticket-based entry for premium streams
- **Subscriber Benefits**: Exclusive access to subscriber-only streams
- **Virtual Gifts**: Animated gifts that creators can receive

### 4. Analytics & Insights
- **Stream Performance**: Views, duration, engagement metrics
- **Revenue Tracking**: Tips, ticket sales, subscriber growth
- **Audience Analytics**: Viewer demographics and behavior
- **Peak Performance**: Best streaming times and content types

## Technical Architecture

### Service Components
```
Live Streaming Service (Port 8085)
├── Stream Management Controller
├── Real-time Communication Handler
├── Chat Service Integration
├── Payment Service Integration
├── Analytics Collection
└── Agora.io SDK Integration
```

### External Integrations

#### Agora.io Video Calling SDK
- **Purpose**: Real-time video streaming infrastructure
- **Features**: 
  - Ultra-low latency streaming
  - Global content delivery network
  - Scalable infrastructure
  - Recording capabilities
- **Pricing**: Pay-per-minute usage model

#### WebSocket Connections
- **Purpose**: Real-time chat and interactive features
- **Implementation**: Spring WebSocket with STOMP protocol
- **Features**:
  - Live chat messages
  - Real-time viewer count
  - Tip notifications
  - Stream status updates

## API Endpoints

### Stream Management APIs

#### Create Stream
```http
POST /api/v1/streams
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "title": "My Live Stream",
  "description": "Stream description",
  "isSubscriberOnly": true,
  "ticketPrice": 5.00,
  "scheduledStartTime": "2025-06-27T20:00:00Z",
  "settings": {
    "quality": "HD",
    "recordingEnabled": true,
    "chatEnabled": true,
    "tipsEnabled": true
  }
}
```

#### Start Stream
```http
POST /api/v1/streams/{streamId}/start
Authorization: Bearer {jwt_token}

Response:
{
  "streamId": "stream_123",
  "agoraChannelName": "channel_abc",
  "agoraToken": "temp_token_xyz",
  "streamUrl": "rtmp://stream.example.com/live/stream_123"
}
```

#### Join Stream (Viewer)
```http
POST /api/v1/streams/{streamId}/join
Authorization: Bearer {jwt_token}

Response:
{
  "accessGranted": true,
  "agoraChannelName": "channel_abc",
  "agoraToken": "viewer_token_xyz",
  "chatEnabled": true,
  "canTip": true
}
```

#### End Stream
```http
POST /api/v1/streams/{streamId}/end
Authorization: Bearer {jwt_token}

Response:
{
  "streamId": "stream_123",
  "duration": 3600,
  "viewerCount": 45,
  "totalTips": 125.50,
  "recordingUrl": "https://recordings.example.com/stream_123.mp4"
}
```

### Chat APIs

#### Send Message
```http
POST /api/v1/streams/{streamId}/chat
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "message": "Great stream!",
  "type": "MESSAGE"
}
```

#### Send Tip
```http
POST /api/v1/streams/{streamId}/tip
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "amount": 10.00,
  "message": "Keep up the great work!",
  "currency": "USD"
}
```

### Analytics APIs

#### Get Stream Analytics
```http
GET /api/v1/streams/{streamId}/analytics
Authorization: Bearer {jwt_token}

Response:
{
  "streamId": "stream_123",
  "totalViews": 150,
  "peakViewers": 45,
  "duration": 3600,
  "totalTips": 125.50,
  "messagesCount": 234,
  "averageViewTime": 1800,
  "viewerRetention": 0.75
}
```

## Database Schema

### Live Streams Collection
```javascript
{
  _id: ObjectId,
  creatorId: ObjectId,
  title: String,
  description: String,
  status: ["SCHEDULED", "LIVE", "ENDED", "CANCELLED"],
  isSubscriberOnly: Boolean,
  ticketPrice: Number,
  streamSettings: {
    quality: ["SD", "HD", "FHD", "4K"],
    recordingEnabled: Boolean,
    chatEnabled: Boolean,
    tipsEnabled: Boolean,
    maxViewers: Number
  },
  streamStats: {
    viewerCount: Number,
    peakViewers: Number,
    totalTips: Number,
    duration: Number, // in seconds
    messagesCount: Number
  },
  agoraConfig: {
    channelName: String,
    appId: String,
    uid: Number
  },
  scheduledStartTime: Date,
  actualStartTime: Date,
  endTime: Date,
  recordingUrl: String,
  thumbnailUrl: String,
  createdAt: Date,
  updatedAt: Date
}
```

### Stream Viewers Collection
```javascript
{
  _id: ObjectId,
  streamId: ObjectId,
  viewerId: ObjectId,
  joinTime: Date,
  leaveTime: Date,
  totalTipped: Number,
  messagesCount: Number,
  watchTime: Number, // in seconds
  deviceType: ["MOBILE", "DESKTOP", "TABLET"],
  location: {
    country: String,
    city: String
  }
}
```

### Stream Messages Collection
```javascript
{
  _id: ObjectId,
  streamId: ObjectId,
  userId: ObjectId,
  message: String,
  type: ["MESSAGE", "TIP", "SYSTEM", "REACTION"],
  tipAmount: Number,
  reactionType: String,
  timestamp: Date,
  isModerated: Boolean,
  moderatedBy: ObjectId
}
```

## WebSocket Events

### Client → Server Events

#### Join Stream
```javascript
{
  "type": "JOIN_STREAM",
  "data": {
    "streamId": "stream_123",
    "userId": "user_456"
  }
}
```

#### Send Message
```javascript
{
  "type": "SEND_MESSAGE",
  "data": {
    "streamId": "stream_123",
    "message": "Hello everyone!",
    "userId": "user_456"
  }
}
```

#### Send Tip
```javascript
{
  "type": "SEND_TIP",
  "data": {
    "streamId": "stream_123",
    "amount": 5.00,
    "message": "Great content!",
    "userId": "user_456"
  }
}
```

### Server → Client Events

#### New Message
```javascript
{
  "type": "NEW_MESSAGE",
  "data": {
    "messageId": "msg_789",
    "userId": "user_456",
    "username": "john_doe",
    "message": "Hello everyone!",
    "timestamp": "2025-06-27T20:30:00Z",
    "isSubscriber": true
  }
}
```

#### New Tip
```javascript
{
  "type": "NEW_TIP",
  "data": {
    "tipId": "tip_123",
    "userId": "user_456",
    "username": "john_doe",
    "amount": 5.00,
    "message": "Great content!",
    "timestamp": "2025-06-27T20:30:00Z"
  }
}
```

#### Viewer Count Update
```javascript
{
  "type": "VIEWER_COUNT_UPDATE",
  "data": {
    "streamId": "stream_123",
    "viewerCount": 47,
    "isIncreasing": true
  }
}
```

#### Stream Status Update
```javascript
{
  "type": "STREAM_STATUS_UPDATE",
  "data": {
    "streamId": "stream_123",
    "status": "LIVE",
    "timestamp": "2025-06-27T20:00:00Z"
  }
}
```

## Frontend Implementation

### Creator Interface

#### Stream Creation Form
```jsx
const StreamCreationForm = () => {
  const [streamData, setStreamData] = useState({
    title: '',
    description: '',
    isSubscriberOnly: false,
    ticketPrice: 0,
    scheduledStartTime: '',
    settings: {
      quality: 'HD',
      recordingEnabled: true,
      chatEnabled: true,
      tipsEnabled: true
    }
  });

  const handleCreateStream = async () => {
    const response = await fetch('/api/v1/streams', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${authToken}`
      },
      body: JSON.stringify(streamData)
    });
    // Handle response
  };

  return (
    <form onSubmit={handleCreateStream}>
      {/* Form fields */}
    </form>
  );
};
```

#### Live Streaming Component
```jsx
const LiveStreamComponent = ({ streamId }) => {
  const [agoraClient, setAgoraClient] = useState(null);
  const [localVideoTrack, setLocalVideoTrack] = useState(null);
  const [localAudioTrack, setLocalAudioTrack] = useState(null);

  useEffect(() => {
    const initializeAgora = async () => {
      const client = AgoraRTC.createClient({ mode: "live", codec: "vp8" });
      await client.setClientRole("host");
      
      const videoTrack = await AgoraRTC.createCameraVideoTrack();
      const audioTrack = await AgoraRTC.createMicrophoneAudioTrack();
      
      setAgoraClient(client);
      setLocalVideoTrack(videoTrack);
      setLocalAudioTrack(audioTrack);
    };

    initializeAgora();
  }, []);

  const startStream = async () => {
    const response = await fetch(`/api/v1/streams/${streamId}/start`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${authToken}` }
    });
    
    const { agoraChannelName, agoraToken } = await response.json();
    
    await agoraClient.join(APP_ID, agoraChannelName, agoraToken, null);
    await agoraClient.publish([localVideoTrack, localAudioTrack]);
  };

  return (
    <div className="live-stream-container">
      <div id="local-video" className="video-container">
        {/* Local video preview */}
      </div>
      <button onClick={startStream}>Start Stream</button>
    </div>
  );
};
```

### Viewer Interface

#### Stream Viewer Component
```jsx
const StreamViewer = ({ streamId }) => {
  const [agoraClient, setAgoraClient] = useState(null);
  const [remoteUsers, setRemoteUsers] = useState([]);
  const [messages, setMessages] = useState([]);
  const [socket, setSocket] = useState(null);

  useEffect(() => {
    const initializeViewer = async () => {
      // Initialize Agora client
      const client = AgoraRTC.createClient({ mode: "live", codec: "vp8" });
      await client.setClientRole("audience");
      
      // Join stream
      const response = await fetch(`/api/v1/streams/${streamId}/join`, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${authToken}` }
      });
      
      const { agoraChannelName, agoraToken } = await response.json();
      await client.join(APP_ID, agoraChannelName, agoraToken, null);
      
      // Setup WebSocket for chat
      const ws = new WebSocket(`ws://localhost:8085/stream/${streamId}/chat`);
      setSocket(ws);
      
      setAgoraClient(client);
    };

    initializeViewer();
  }, [streamId]);

  const sendTip = async (amount, message) => {
    const response = await fetch(`/api/v1/streams/${streamId}/tip`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${authToken}`
      },
      body: JSON.stringify({ amount, message })
    });
    // Handle tip response
  };

  return (
    <div className="stream-viewer">
      <div id="remote-video" className="video-container">
        {/* Remote video streams */}
      </div>
      <StreamChat 
        messages={messages} 
        onSendMessage={sendMessage}
        onSendTip={sendTip}
      />
    </div>
  );
};
```

## Security Considerations

### Stream Access Control
- **JWT Validation**: All stream operations require valid JWT tokens
- **Subscription Verification**: Check active subscription for subscriber-only streams
- **Payment Verification**: Validate payment for ticketed streams
- **Rate Limiting**: Prevent spam in chat and tip abuse

### Content Moderation
- **Automated Filtering**: Real-time chat message filtering
- **Manual Moderation**: Creator and admin moderation tools
- **Report System**: Users can report inappropriate content
- **Stream Recording**: Evidence for moderation decisions

### Privacy Protection
- **Token Management**: Short-lived Agora tokens for security
- **Data Encryption**: End-to-end encryption for sensitive data
- **User Consent**: Clear privacy policies for recording and data usage
- **GDPR Compliance**: Right to deletion and data portability

## Performance Optimization

### Streaming Quality
- **Adaptive Bitrate**: Automatic quality adjustment based on connection
- **CDN Integration**: Global content delivery for low latency
- **Load Balancing**: Distribute streaming load across servers
- **Caching**: Cache stream metadata and user permissions

### Scalability
- **Horizontal Scaling**: Multiple instances of streaming service
- **Database Optimization**: Efficient queries for real-time data
- **Connection Pooling**: Optimize WebSocket connections
- **Resource Management**: Auto-scaling based on concurrent streams

## Monitoring & Analytics

### Real-time Metrics
- **Stream Health**: Video/audio quality, connection stability
- **Viewer Engagement**: Watch time, chat activity, tip frequency
- **Performance Metrics**: Latency, buffering, error rates
- **Revenue Tracking**: Tips, ticket sales, subscription conversions

### Business Intelligence
- **Creator Analytics**: Stream performance, audience growth
- **Platform Analytics**: Popular content, peak usage times
- **Revenue Analytics**: Monetization effectiveness
- **User Behavior**: Viewing patterns, engagement trends

This comprehensive live streaming service will provide creators with professional-grade broadcasting capabilities while offering viewers an engaging, interactive experience with multiple monetization opportunities.
