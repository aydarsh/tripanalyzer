# Vehicle Trip Analyzer  

TripAnalyzer is a Java 21 service built with Spring Boot 4.1.2. Send vehicle telemetry to `POST /v1/trip` to get the departure and destination cities, average fuel consumption, breaks and refueling stops. See the [API specification](task-swagger.yml) for the request and response fields.

## Build and run locally

From the repository root, use JDK 21 and the included Maven wrapper to run the tests and build the executable JAR:

```sh
./mvnw clean verify
```

The tests use a local HTTP server and a stub geocoder. To run the application, set these environment variables in your shell:

| Variable | Purpose |
| --- | --- |
| `APIKEY` | Google Maps API key with the Geocoding API enabled |
| `BASICAUTHKEY` | HTTP Basic password for the user `admin` |

Start the service on port 8080:

```sh
java -jar target/trip.jar
```

From a second terminal in the repository root, submit a sample trip. When prompted, enter the password configured in `BASICAUTHKEY`.

```sh
curl --user admin --header 'Content-Type: application/json' \
  --data-binary @examples/example1.json http://localhost:8080/v1/trip
```

To run the same JAR in Docker, stop the local process first, then run the following commands from the shell with the two variables exported:

```sh
docker build -t tripanalyzer .
docker run --rm -p 127.0.0.1:8080:8080 -e APIKEY -e BASICAUTHKEY tripanalyzer
```

## Cloud deployment

The following sections describe the original Terraform and Google Cloud setup. For deployment from your own GitHub account, fork this repository.

## Configuring Terraform  
I used [Terraform Cloud](https://app.terraform.io) to automate infrastructure management. In fact, the infrastructure can be deployed by issuing terraform commands from the [terraform/](terraform/) directory. However, I would like to give instructions on how to configure Terraform Cloud.  

Firstly, sign up and create a Terraform Cloud account. Create an organization named `trip-analyzer` and two workspaces: `trip-analyzer-dev` and `trip-analyzer-prod`. These workspaces are for dev and prod environment infrastructure resources:    
![Terraform organization and workspaces](images/workspaces.png)

Then, connect workspaces to your GitHub repository. Choose option **Only trigger runs when files in specified paths change** and enter `terraform` in the input field. For **VCS branch** choose `dev` in the dev workspace and `master` in the prod workspace:
![Version control settings](images/workspace_settings.png)  

After that, configure variables for your workspaces. Set `env` to `dev` in the dev workspace and `prod` in the prod workspace. I used Google Cloud Platform for the infrastructure, that is why I need Google credentials. In the next step I will explain how to add `google_credentials` variable. Do not add it now:
![Variables configuration](images/terraform_variables.png)

## Configuring Google credentials  

Go to **Google Cloud [web console](https://console.cloud.google.com/)** -> **IAM & Admin** -> **Service Accounts** -> **Create Service Account**. Name it as `trip-analyzer-dev` and press **Create**. Then **Select Role** -> **Editor** -> **Continue** -> **Create Key** -> **Create**. Download the json file. Open this file in Notepad++, select all text and go to **Plugins** -> **MIME Tools** -> **Base64 Encode**. We will add this to Terraform now. Open `trip-analyzer-dev` workspace, go to **Variables** tab and press **Add variable**. For the **Key** field enter `google_credentials`, for the **Value** paste the Base64 encoded content from Notepad++, the **Sensitive** checkbox and press **Save variable**.  

Create `trip-analyzer-prod` service account, select `Editor` role and create a key for that account. In the variables section of the `trip-analyzer-prod` workspace create `env` variable with value `prod` and `google_credentials` sensitive variable with the Base64 encoded key file contents.   

With this configuration when you change files in the `terraform/` directory and push it to the `dev` branch `trip-analyzer-dev` infrastructure will be changed. Similarly, when files in the `terraform/` directory change for the `master` branch `trip-analyzer-prod` infrastructure will be changed.  

Now go to the **Google Cloud web console** -> **APIs & Services** -> **Enable APIs & Services**, enter `Kubernetes` in the search field, press `Kubernetes Engine API` and then **Enable**. Also enable the following APIs:  
* `Cloud Resource Manager API`
* `Geocoding API`
* `Cloud Build API`

## Configuring Kubernetes secrets

So far, so good. Now we will configure `Geocoding API` and `Basic Auth` credentials to use in our Kubernetes clusters. We will get Google Geocoding API key, then start Terraform pipelines to create `trip-analyzer-dev` and `trip-analyzer-prod` kubernetes clusters, use `kubectl` command to create corresponding secrets.  
  
Now go to the **Google Cloud web console** -> **APIs & Services** -> **Credentials**. In the **API Keys** section find your key, it looks like `AIzaSyCEAq..`, and press a button next to it to copy. Store this value in Notepad++, we will use it in the next step.  

Go to the Terraform Cloud console, then to the `trip-analyzer` organisation and click `trip-analyzer-dev` workspace. Press **Queue plan** dropdown box and then **Queue plan** button. This will start infrastructure build pipeline. Have a look at planned changes and then press **Confirm** to complete infrastructure build.  

Switch to the **Google Cloud web console** -> **Kubernetes Engine**. On this page you can see the `trip-analyzer-dev` and `trip-analyzer-prod` clusters. Once they are ready press **Connect** button, then **Run in Cloud Shell**. Substitute API key value for `<APIkey>` and your password for `<password>` in the following commands and run them:  
```commandline
kubectl create secret generic geocodingkey --from-literal=APIKEY=<APIkey>
kubectl create secret generic basicauthkey --from-literal=BASICAUTHKEY=<password>
``` 
Do the same steps for the `trip-analyzer-prod` cluster with different values for `<APIkey>` and `<password>`.  

## Configuring CI/CD
I configured CI/CD by means of Google Cloud Build in [GitOps style](https://cloud.google.com/kubernetes-engine/docs/tutorials/gitops-cloud-build).  

To setup CI/CD pipelines go to the **Google Cloud web console** -> **Cloud Build** -> **Triggers**. Press **Connect repository**, select `GitHub (Cloud Build GitHub App)` and then click **Continue**. Add your GitHub account, select your `tripanalyzer` repository, check the `I understand that GitHub content...` checkbox and **Connect repository** button. Now go to the **Triggers** page and click **Create trigger**. Use the following values to configure the trigger:  

| **Parameter** | **Value** |
| --- | --- |
| Name | push-trigger-app |
| Event | Push to a branch |
| Branch | ^dev$ |
| Included files filter (glob) | Dockerfile, pom.xml, src/\*\*, examples/\*\*, cloudbuild.yaml |
| Ignored files filter (glob) | terraform/\*\*, env/\*\* |
| File type | Cloud Build configuration file (yaml or json) |
| Cloud Build configuration file location | cloudbuild.yaml |  

Press **Save** button. Add new trigger with the following values:  

| **Parameter** | **Value** |
| --- | --- |
| Name | push-trigger-env |
| Event | Push to a branch |
| Branch | ^dev$ |
| Included files filter (glob) | env/\*\* |
| Ignored files filter (glob) | terraform/\*\*, src/\*\* |
| File type | Cloud Build configuration file (yaml or json) |
| Cloud Build configuration file location | env/cloud-delivery.yaml |

**Add variable**

| **Variable** | **Value** |
| --- | --- |
| _ENV | dev |

Press **Save** button.

Now go to the **Settings** page and enable `Kubernetes Engine Developer` role for the Cloud Build service account:  
![Service account permissions](images/cloudbuild_permissions.png)  
After that go to the **Google Cloud web console** -> **IAM & Admin**. Find member email that ends with `@cloudbuild.gserviceaccount.com` and click pen button to edit its permissions. Press **Add another role**, type `secret` and select `Secret Manager Secret Accessor`, press **Save**.  

Go to the **Google Cloud web console** -> **Secret Manager** -> **Create secret**. Name new secret as `github_key`, press **Browse** and select your GitHub deploy key.  

Both triggers clone the triggering GitHub repository and use the build service account email for Git commits.

## Test the deployed service

Use the same sample request as above with the deployed service URL in place of `http://localhost:8080`. Use the `admin` password from the target cluster's `basicauthkey` secret.
