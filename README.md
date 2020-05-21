# Vehicle Trip Analyzer  

## Task definition
* Implement the [specified](task-swagger.yml) REST Endpoint
* Protect the API with BasicAuth
* Use Docker to run your application
* Use one of the following languages: Go, Java, Python, C++
* Automate the infrastructure rollout
* Use an external service to determine the city name for depature and destination
* Upload your solution to a private GitHub repository
* Provide a link to the secured hosted instance of your solution
* Provide the following files together with your code:
  - Dockerfile
  - Build-Script
  - Deployment-Script
  - Kubernetes deployment YAML (if Kubernetes is used)
  - Infrastructure automation scripts
  - README.md with documentation how to deploy the infrastructure and the application

---

Create a fork of the [tripanalyzer](https://github.com/aydarsh/tripanalyzer) repository to your account.  

## Configuring Terraform  
I used [Terraform Cloud](https://app.terraform.io) to automate infrastructure management. In fact, the infrastructure can be deployed by issuing terraform commands from the [terraform/](terraform/) directory. However, I would like to give instructions on how to configure Terraform Cloud.  

Firstly, sign up and create a Terraform Cloud account. Create an organization named `trip-analyzer` and two workspaces: `trip-analyzer-dev` and `trip-analyzer-prod`. These workspaces are for dev and prod environment infrastructure resources:    
![Terraform organization and workspaces](images/workspaces.png)

Then, connect workspaces to your GitHub repository. Choose option **Only trigger runs when files in specified paths change** and enter `terraform` in the input field. For **VCS branch** enter `dev`:
![Version control settings](images/workspace_settings.png)  

After that, configure variables for your workspaces. Add variable `env` with value `dev`. I used Google Cloud Platform for the infrastructure, that is why I need Google credentials. In the next step I will explain how to add `google_credentials` variable. Do not add it now:
![Variables configuration](images/terraform_variables.png)

## Configuring Google credentials  

Go to **Google Cloud [web console](https://console.cloud.google.com/)** -> **IAM & Admin** -> **Service Accounts** -> **Create Service Account**. Name it as `trip-analyzer-dev` and press **Create**. Then **Select Role** -> **Editor** -> **Continue** -> **Create Key** -> **Create**. Download the json file. Open this file in Notepad++, select all text and go to **Plugins** -> **MIME Tools** -> **Base64 Encode**. We will add this to Terraform now. Open `trip-analyzer-dev` workspace, go to **Variables** tab and press **Add variable**. For the **Key** field enter `google_credentials`, for the **Value** paste the Base64 encoded content from Notepad++, the **Sensitive** checkbox and press **Save variable**.  

Create `trip-analyzer-prod` service account, select `Editor` role and create a key for that account. In the variables section of the `trip-analyzer-prod` workspace create `env` variable with value `prod` and `google_credentials` sensitive variable with the Base64 encoded key file contents.   

With this configuration when you change files in the `terraform/` directory and push it to the `dev` branch `trip-analyzer-dev` infrastructure will be changed. Similarly, when files in the `terraform/` directory change for the `master` branch `trip-analyzer-prod` infrastructure will be changed.  

Now go the **Google Cloud web console** -> **APIs & Services** -> **Enable APIs & Services**, enter `Kubernetes` in the search field, press `Kubernetes Engine API` and then **Enable**. Then search for and enable `Cloud Resource Manager API`.  

## Configuring CI/CD
* The application runs on Google Kubernetes Engine
* CI/CD is performed by Cloud Build and is configured in [GitOps style](https://cloud.google.com/kubernetes-engine/docs/tutorials/gitops-cloud-build)

