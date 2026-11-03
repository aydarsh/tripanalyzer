FROM gcr.io/distroless/java21-debian13
LABEL appname='trip'
COPY target/trip.jar /app/
WORKDIR /app
CMD ["trip.jar"]
