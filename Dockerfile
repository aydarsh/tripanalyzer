FROM gcr.io/distroless/java:11
LABEL appname='trip'
COPY target/trip.jar /app
WORKDIR /app
CMD ["trip.jar"]