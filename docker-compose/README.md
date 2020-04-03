# JHipster generated Docker-Compose configuration

## Configuration

Copy the file `socials.env.skeleton` to `socials.env` and fill the missing secrets

## Usage

Run the stack with the script `docker-compose-up.sh` to launch services in the correct order.

## Configured Docker services

### Service registry and configuration server:
- [JHipster Registry](http://localhost:8761)

### Applications and dependencies:
- analysis (microservice application)
- analysis's mongodb database
- apigateway (gateway application)
- apigateway's mongodb database
- cronscheduler (microservice application)
- cronscheduler's no database
- geo (microservice application)
- geo's no database
- jobsupervisor (microservice application)
- jobsupervisor's mongodb database
- linkresolver (microservice application)
- linkresolver's no database
- nel (microservice application)
- nel's no database
- ner (microservice application)
- ner's no database
- socials (microservice application)
- socials's mongodb database

### Additional Services:

- Kafka
- Zookeeper
- [JHipster Console](http://localhost:5601)
- [Zipkin](http://localhost:9411)
