terraform {
  backend "remote" {
    hostname     = "app.terraform.io"
    organization = "trip-analyzer"

    workspaces {
      prefix = "trip-analyzer-"
    }
  }
}
