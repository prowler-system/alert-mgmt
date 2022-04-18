# alert-mgmt
Log and alert management system

# Steps to setup the Server side:
Pre-requisites: Docker and Docker-compose

1. Clone the repo to your machine
2. Goto [resources folder](https://github.com/prowler-system/alert-mgmt/blob/main/src/main/resources/)
3. Run `docker-compose -f docker-compose-server-side.yml up -d`

# Steps to setup the client side:
2. Copy [docker-compose-client-side.yml](https://github.com/prowler-system/alert-mgmt/blob/main/src/main/resources/docker-compose-client-side.yml) to the host running your client application
3. Run `docker-compose -f docker-compose-client-side.yml up -d`
