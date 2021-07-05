System Requirements:
- Spring Boot 2.5.2 requires Java 8 and is compatible up to and including Java 16. Spring Framework 5.3.8 or above is also required.
- Java SDK v1.8

Explicit build support:
- Maven 3.5+

Steps:
1. Clone the project.
2. Open console in project path.
3. Runs the command: mvn clean package.
4. Now you can run the "project path"/target/demo-0.0.1-SNAPSHOT.jar
5. Open the console in the same dir where demo-0.0.1-SNAPSHOT.jar is and now you can use the next command:
   - java -jar  demo-0.0.1-SNAPSHOT.jar
   
If all was OK then your application backend is running. For default on port: 8080, url: http://localhost:8080/. 

For interact with the services you can use the postman tool. For that you should import the file Gateway-Demo.postman_collection.json into postman  
this file contains a set of request for interact with de service.
 



