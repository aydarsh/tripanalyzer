apiVersion: apps/v1
kind: Deployment
metadata:
  name: trip
  labels:
    app: trip
spec:
  replicas: 1
  selector:
    matchLabels:
      app: trip
  template:
    metadata:
      labels:
        app: trip
    spec:
      containers:
        - name: trip
          image: gcr.io/GOOGLE_CLOUD_PROJECT/trip:COMMIT_SHA
          env:
          - name: APIKEY
            valueFrom:
              secretKeyRef:
                name: geocodingkey
                key: APIKEY
          - name: BASICAUTHKEY
            valueFrom:
              secretKeyRef:
                name: basicauthkey
                key: BASICAUTHKEY
          ports:
            - containerPort: 8080
---
kind: Service
apiVersion: v1
metadata:
  name: trip
spec:
  selector:
    app: trip
  ports:
    - protocol: TCP
      port: 80
      targetPort: 8080
  type: LoadBalancer