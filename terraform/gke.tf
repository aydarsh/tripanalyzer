resource "google_project_service" "project" {
  service = "container.googleapis.com"
}

resource "google_container_cluster" "primary" {
  name               = "trip-analyzer-${var.env}"
  location           = "us-central1-a"
  initial_node_count = 1

  master_auth {
    username = ""
    password = ""

    client_certificate_config {
      issue_client_certificate = false
    }
  }

  node_config {
    oauth_scopes = [
      "https://www.googleapis.com/auth/devstorage.read_only",
      "https://www.googleapis.com/auth/logging.write",
      "https://www.googleapis.com/auth/monitoring",
    ]

    metadata = {
      disable-legacy-endpoints = "true"
    }

    labels = {
      env = var.env
    }

  }

  timeouts {
    create = "30m"
    update = "40m"
  }
}