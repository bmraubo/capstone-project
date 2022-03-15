FROM gradle:7.4-jdk17-alpine as builder

WORKDIR /code
COPY . /code


RUN gradle build -x test

FROM openjdk:17-alpine
COPY --from=builder /code/build/libs/EchoServer-1.0-SNAPSHOT.jar server.jar

COPY --from=builder /code/src/main/java/site/bmraubo/echo_server_endpoints/kitteh.jpg ./src/main/java/site/bmraubo/echo_server_endpoints/kitteh.jpg 
COPY --from=builder /code/src/main/java/site/bmraubo/echo_server_endpoints/doggo.png ./src/main/java/site/bmraubo/echo_server_endpoints/doggo.png
COPY --from=builder /code/src/main/java/site/bmraubo/echo_server_endpoints/kisses.gif ./src/main/java/site/bmraubo/echo_server_endpoints/kisses.gif

EXPOSE 5000
CMD [ "java", "-jar", "server.jar"]