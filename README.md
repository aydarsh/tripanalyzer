# Trip Analyzer  

## Configuring Terraform  
* Terraform Cloud is used
* Terraform configuration files are located in the terraform/ directory
* Push to the `dev` branch triggers `trip-analyzer-dev` terraform workspace
* Push to the `master` branch triggers `trip-analyzer-prod` terraform workspace
* Two Terraform variables are declared in each workspace:  
  * env - environment: env, prod
  * google_credentials - base64 encoded service account key
* A service account is created in GCP for each of the environments: trip-analyzer-dev, trip-analyzer-prod
* Service accounts are assigned roles:
  * Kubernetes Engine Admin
  * Service Usage Admin
  * Editor
 
