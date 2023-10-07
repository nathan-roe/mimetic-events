FROM openjdk:11
COPY ./build/kotlin/dist/MimeticEvents.jar /opt
WORKDIR /opt
RUN apt-get update && apt-get install -y build-essential
RUN java -jar -Djava.library.path=. MimeticEvents.jar