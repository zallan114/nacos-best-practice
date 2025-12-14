# Nacos Practice
#### Nacos server v3.1.1: 
![sc_diagram.png](./nacos-gateway\src\main\resources\sc_diagram.png)


  http://106.54.39.161:8080
```xml
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <spring-cloud.version>2023.0.1</spring-cloud.version>
        <spring-cloud-alibaba.version>2023.0.1.0</spring-cloud-alibaba.version>
        <nacos-client.version>3.1.1</nacos-client.version>
```

## Module Structure

- **nacos-common**: Common entity and utility classes shared across modules
  - `User.java`: User entity class
  - `Result.java`: Unified API response wrapper

- **nacos-provider**: Service provider module
  - Provides user information and config query services

- **nacos-consumer**: Service consumer module
  - Calls services from nacos-provider and nacos-config-demo
  - Uses Feign for service-to-service communication

- **nacos-config-demo**: Nacos Config Center integration demo
  - Demonstrates dynamic config refresh from Nacos Config Center


## API Documentation

### Nacos Gateway Rules

- **/consumer/**: Forward to nacos-consumer service
- **/provider/**: Forward to nacos-provider service
- **/config-demo/**: Forward to nacos-config-demo service
 
 ### Nacos Gateway API
 #### only gateway exposed 8880 outside, other services are hidden
 - **/test/**: Forward to baidu.com
 - http://106.54.39.161:8880/consumer/user/{id} Forward to nacos-consumer service to get user by ID
 - http://106.54.39.161:8880/config-demo/info Forward to nacos-config-demo service to get config info
 - http://106.54.39.161:8880/provider/user/{id} Forward to nacos-provider service to get user by ID
 - http://106.54.39.161:8880/provider/config Forward to nacos-provider service to get config info (2 instances)

#### final services in nacos after startup.sh
![nacos_services.png](./nacos-gateway\src\main\resources\nacos_services.png)


