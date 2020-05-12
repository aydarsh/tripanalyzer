provider "google" {
  credentials = base64decode(var.google_credentials)
  project     = "trip-analyzer-prj"
  region      = "us-central1"
  zone        = "us-central1-a"
}