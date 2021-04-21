# Rest-Api :
  This repo is containing two microservices:
    
    1. auth-service 
    2. api-service
    
    Deployment Process for auth-service and api-service:
      1. Build the rpm from jenkin
      2. java 8+
      3. change db configuration on properties file.
      4. For Enabling HTTPS (copy conf and certificate from 10.150.1.5)
          a. copy the cerificate /atom/api-service/current/atomex.p12 location
          b. All the ssl config from /home/staging/api_conf/application.properties file
          ```
          server.ssl.key-store: atomex.p12
          server.ssl.key-store-password: <Password FRom Above Server>
          server.ssl.keyStoreType: <In Prop>
          server.ssl.keyAlias: <In Prop>

          ```
