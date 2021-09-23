# Reviewia Backend

## API Documentation
[Postman documentation](https://documenter.getpostman.com/view/13371633/TzzGGDLF)

## Localhost deployment guide
### Pre-requisites
1.  JDK 1.8 or later version
2.  MySQL
3.  NPM
4.  AWS S3 Bucket

### Guide
Create MySQL database:
    
    Database name: reviewia
    Username: root
    Password: root

Install mail server:

    npm install -g maildev

Add AWS S3 configurations to application.properties file:
(located in /src/main/resources/application.properties)

    Endpoint url
    Access key
    Secret key
    Bucket name

Run mail server (run on the terminal):

    maildev

Run the application (run command from inside the project directory):

    mvnw spring-boot:run
