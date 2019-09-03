# weather-service

### build image
sbt docker:publishLocal

### run container
docker run --rm --net host --name weather-service weather-service:1.0 
