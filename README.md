# weather-service
Consumes Yahoo weather-channel service using spray, akka actors and slick 3.

### build image
sbt docker:publishLocal

### run container
docker run --rm --net host --name weather-service weather-service:1.0 
