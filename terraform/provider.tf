provider "google" {
  credentials = file("account.json")
  project     = "trip-analyzer-prj"
  region      = "us-central1"
  zone        = "us-central1-a"
}