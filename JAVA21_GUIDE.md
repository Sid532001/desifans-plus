# Java 21 Development Guide - DesiFans Platform

## Java 21 Benefits for Our Platform

### Performance Improvements
- **ZGC (Z Garbage Collector)**: Ultra-low latency garbage collection (< 10ms pauses)
- **Virtual Threads**: Lightweight threads for high-concurrency applications
- **String Templates**: Better string manipulation performance
- **Pattern Matching**: More efficient code execution

### Platform-Specific Advantages

#### 1. High-Concurrency Live Streaming
```java
// Virtual Threads for handling multiple concurrent streams
public class LiveStreamingHandler {
    
    @Async("virtualThreadTaskExecutor")
    public CompletableFuture<Void> handleStreamViewer(String streamId, String viewerId) {
        // Each viewer connection runs on a virtual thread
        return CompletableFuture.runAsync(() -> {
            // Stream processing logic
            processViewerConnection(streamId, viewerId);
        }, VirtualThread.ofVirtual().factory());
    }
}
```

#### 2. Low-Latency Payment Processing
```java
// ZGC ensures payment processing has minimal GC pauses
@Service
public class PaymentService {
    
    @Timed("payment.processing.time")
    public PaymentResult processPayment(PaymentRequest request) {
        // Critical payment logic with guaranteed low-latency
        return paymentProcessor.process(request);
    }
}
```

#### 3. Enhanced String Processing
```java
// String Templates for better performance in content processing
public class ContentProcessor {
    
    public String generateContentUrl(String creatorId, String contentId) {
        return STR."https://cdn.desifans.com/\{creatorId}/content/\{contentId}";
    }
}
```

## Development Environment Setup

### 1. Java 21 Installation

#### Windows (Using SDKMAN)
```bash
# Install SDKMAN
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Install Java 21
sdk install java 21.0.1-oracle
sdk use java 21.0.1-oracle

# Verify installation
java --version
```

#### Direct Download
- Download from: https://www.oracle.com/java/technologies/downloads/#java21
- Or use OpenJDK: https://openjdk.org/projects/jdk/21/

### 2. IDE Configuration

#### IntelliJ IDEA
```xml
<!-- Add to pom.xml for IntelliJ -->
<properties>
    <java.version>21</java.version>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
</properties>
```

#### VS Code
```json
// .vscode/settings.json
{
    "java.configuration.runtimes": [
        {
            "name": "JavaSE-21",
            "path": "C:\\Program Files\\Java\\jdk-21"
        }
    ],
    "java.compile.nullAnalysis.mode": "automatic"
}
```

### 3. Maven Configuration

#### Maven Wrapper Update
```bash
# Update Maven wrapper to latest version
./mvnw wrapper:wrapper -Dmaven=3.9.5
```

#### JVM Arguments for Development
```bash
# .mvn/jvm.config (create this file)
-XX:+UseZGC
-XX:+UnlockExperimentalVMOptions
-XX:+UseContainerSupport
-Xmx2g
-Xms512m
--enable-preview
```

## Service-Specific Java 21 Optimizations

### Discovery Service (Eureka)
```yaml
# application.yml - JVM tuning
spring:
  application:
    name: desifans-eureka-server
  main:
    # Enable virtual threads
    web-application-type: servlet
server:
  tomcat:
    threads:
      # Use virtual threads for better concurrency
      max: 200
      min-spare: 10
```

### Future Services Configuration

#### User Service (High Concurrency)
```java
@Configuration
@EnableAsync
public class AsyncConfig {
    
    @Bean("virtualThreadTaskExecutor")
    public TaskExecutor virtualThreadTaskExecutor() {
        return new TaskExecutorAdapter(VirtualThread.ofVirtual()
            .name("user-service-", 0)
            .factory());
    }
}
```

#### Content Service (Memory Intensive)
```bash
# JVM arguments for content processing
-XX:+UseZGC
-XX:+UnlockExperimentalVMOptions
-XX:MaxRAMPercentage=75.0
-XX:+UseStringDeduplication
```

#### Live Streaming Service (Ultra-Low Latency)
```bash
# JVM arguments for streaming
-XX:+UseZGC
-XX:+UnlockExperimentalVMOptions
-XX:MaxGCPauseMillis=1
-XX:+UseLargePages
```

## Docker Optimizations for Java 21

### Multi-Stage Dockerfile Template
```dockerfile
# Build stage with Java 21
FROM maven:3.9.5-openjdk-21-slim as builder

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage with optimizations
FROM openjdk:21-jdk-slim

# Install required packages
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create non-root user
RUN groupadd -r spring && useradd -r -g spring spring

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
RUN chown spring:spring app.jar

USER spring

# Java 21 optimized JVM settings
ENV JAVA_OPTS="-XX:+UseZGC \
               -XX:+UnlockExperimentalVMOptions \
               -XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+UseStringDeduplication \
               -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080

CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

## Performance Monitoring

### JVM Metrics with Micrometer
```java
@Component
public class JvmMetricsConfig {
    
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> {
            registry.config()
                .commonTags("application", "desifans-platform")
                .commonTags("java.version", "21")
                .commonTags("gc.type", "ZGC");
        };
    }
}
```

### Prometheus Metrics
```yaml
# Additional metrics for Java 21
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
    distribution:
      percentiles-histogram:
        http.server.requests: true
      percentiles:
        http.server.requests: 0.5, 0.95, 0.99
```

## Development Commands

### Build and Test
```bash
# Clean build with Java 21 features
./mvnw clean compile -Djava.version=21

# Run tests with virtual threads
./mvnw test -Dspring.threads.virtual.enabled=true

# Package with optimizations
./mvnw clean package -Dspring.aot.enabled=true
```

### Local Development
```bash
# Run with Java 21 optimizations
./mvnw spring-boot:run \
  -Dspring-boot.run.jvmArguments="-XX:+UseZGC -XX:+UnlockExperimentalVMOptions --enable-preview"

# Debug mode with virtual threads
./mvnw spring-boot:run \
  -Dspring-boot.run.jvmArguments="-XX:+UseZGC -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

### Docker Development
```bash
# Build with Java 21 optimizations
docker build \
  --build-arg JAVA_VERSION=21 \
  --build-arg JVM_OPTS="-XX:+UseZGC -XX:+UnlockExperimentalVMOptions" \
  -t desifans-service:java21 .

# Run with memory limits
docker run -m 1g \
  -e JAVA_OPTS="-XX:+UseZGC -XX:MaxRAMPercentage=75.0" \
  -p 8080:8080 \
  desifans-service:java21
```

## Performance Benchmarks

### Expected Improvements with Java 21

#### Latency Improvements
- **GC Pause Time**: < 10ms (vs 50-200ms with G1GC)
- **Startup Time**: 20-30% faster with CDS and AOT
- **Memory Usage**: 10-15% reduction with String deduplication

#### Throughput Improvements
- **HTTP Requests**: 2-3x higher with virtual threads
- **Database Connections**: 5-10x more concurrent connections
- **File Processing**: 15-25% faster with enhanced APIs

### Monitoring Targets
```bash
# Key metrics to monitor
curl http://localhost:8761/actuator/metrics/jvm.gc.pause
curl http://localhost:8761/actuator/metrics/jvm.memory.used
curl http://localhost:8761/actuator/metrics/http.server.requests
```

## Troubleshooting Java 21

### Common Issues

#### 1. Preview Features
```bash
# If using preview features, add to all commands
--enable-preview
```

#### 2. Module System Issues
```bash
# Add if needed for reflection
--add-opens java.base/java.lang=ALL-UNNAMED
```

#### 3. Docker Memory Issues
```bash
# Ensure container has enough memory for ZGC
docker run -m 2g your-service
```

### Debug Commands
```bash
# Check Java version in container
docker exec -it your-container java --version

# Monitor GC behavior
docker exec -it your-container jstat -gc 1

# Check virtual threads (when available)
docker exec -it your-container jcmd 1 Thread.dump_to_file /tmp/threads.dump
```

## Migration Checklist

### From Java 17 to Java 21
- [ ] Update pom.xml java.version to 21
- [ ] Update Dockerfile base image to openjdk:21
- [ ] Add ZGC JVM arguments
- [ ] Enable virtual threads in configuration
- [ ] Update CI/CD pipelines
- [ ] Test all services thoroughly
- [ ] Monitor performance improvements

### Verification Steps
1. **Local Development**: All services start and communicate
2. **Docker Build**: Images build successfully with Java 21
3. **Performance**: Metrics show expected improvements
4. **Integration**: All endpoints respond correctly
5. **Load Testing**: System handles expected traffic

---

**Java 21 Status**: ✅ Ready for Development  
**Key Benefits**: ZGC, Virtual Threads, Enhanced Performance  
**Migration Effort**: Low (mostly configuration changes)
