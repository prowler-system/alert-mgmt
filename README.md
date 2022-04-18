# alert-mgmt
Log and alert management system

# Pre-requisites
Running this project locally requires one to install Docker and Docker-compose.

# Steps to run on the server side:
1. Clone the repo to your machine, and update the from-email credentials specified [here](https://github.com/prowler-system/alert-mgmt/blob/main/src/main/resources/application.properties#L32-L33).
2. Goto [resources folder](https://github.com/prowler-system/alert-mgmt/blob/main/src/main/resources/)
3. Run `docker-compose -f docker-compose-server-side.yml up -d` in order to bring up the dependencies viz., Kafka & MySQL for the prowler alert-management service.
4. Start the alert-mgmt spring boot application by running the [AlertMgmtApplication](https://github.com/prowler-system/alert-mgmt/blob/main/src/main/java/com/prowler/alertmgmt/AlertMgmtApplication.java) class. Make sure that there are no errors in the console while connecting to MySQL or Kafka.

# Steps to run on the client side:
1. A bare-bones client application which talks to the Prowler system, and which can be used to demonstrate the functionality can be downloaded frmo [here](https://github.com/prowler-system/trivial-client-app)
2. Copy [docker-compose-client-side.yml](https://github.com/prowler-system/alert-mgmt/blob/main/src/main/resources/docker-compose-client-side.yml) to the host running your client application.
3. Run `docker-compose -f docker-compose-client-side.yml up -d` in order to bring up the dependency viz., filebeat. It's assumed that you are running the client and the server on the same host as filebeat assumes that the kafka broker is running on localhost. 
5. Run the client application to mimic emitting a few logs with violations.


