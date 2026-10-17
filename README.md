# Job Portal Microservices

A microservices-based job portal built with Java and Spring Boot. The
application separates company, job, and review capabilities into
independent services, with an API gateway and supporting infrastructure
for persistence, messaging, and distributed tracing.

The service configuration uses environment-variable overrides with
container-friendly defaults. In the Kubernetes setup, services can
communicate through Kubernetes Service DNS names, and Eureka is disabled
in the application configuration because Kubernetes handles service
discovery.

> **Project note:** The configuration details below are based on the
> properties shared so far. Confirm environment variable names, manifest
> service names, API routes, and deployment commands against the full
> repository before publishing.

## Overview

The project is organized around three core domain services:

-   **Company Service** --- manages company-related information.
-   **Job Service** --- manages job listings and related operations.
-   **Review Service** --- manages reviews associated with companies.

Supporting services and infrastructure provide routing, configuration,
persistence, messaging, and tracing. Kubernetes handles service discovery
in the Kubernetes deployment; Eureka client registration and registry
fetching are disabled in the shared service configuration.

## Architecture

``` text
                         Client
                           |
                           v
                    API Gateway (:8084)
                           |
             +-------------+-------------+
             |             |             |
             v             v             v
      Company Service  Job Service  Review Service
          (:8081)       (:8082)       (:8083)
             |             |             |
             +-------------+-------------+
                           |
       Kubernetes Service DNS
          (in Kubernetes)

     Config Server (:8080)     PostgreSQL (:5432)
     RabbitMQ (:5672)           Zipkin (:9411)
```

This is a conceptual diagram, not a guarantee that every component is
deployed in every environment. The previously used local setup included
a service registry on port `8761`, but the shared application properties
disable Eureka. Do not describe Eureka as active in the current
Kubernetes configuration unless a separate profile or deployment
configuration enables it.

## Technology Stack

  Area                             Technologies
  -------------------------------- ----------------------------
  Backend                          Java, Spring Boot
  API communication                REST
  Architecture                     Microservices
  Routing                          API Gateway
  Service discovery                Kubernetes DNS in Kubernetes
  Configuration                    Spring Cloud Config Server
  Database                         PostgreSQL
  Messaging                        RabbitMQ
  Distributed tracing              Zipkin
  Containers                       Docker, Docker Compose
  Orchestration / local learning   Kubernetes, Minikube
  Source control                   Git

Keep this list aligned with the dependencies and configuration in the
repository. Remove any technology that is not actually used by the
project.

## Services and Local Ports

  Component                  Local port Purpose
  ------------------------ ------------ -------------------------------------
  Config Server                  `8080` Centralized configuration
  Company Service                `8081` Company-related API
  Job Service                    `8082` Job-related API
  Review Service                 `8083` Review-related API
  API Gateway                    `8084` Entry point for routed API requests
  Service Registry (if enabled)   `8761` Optional local registry; not used by the shared Kubernetes service configuration
  Zipkin                         `9411` Distributed tracing UI/API
  RabbitMQ AMQP                  `5672` Message broker connection
  RabbitMQ Management UI        `15672` RabbitMQ administration
  PostgreSQL                     `5432` Relational database
  pgAdmin                        `5050` PostgreSQL administration UI

Ports describe the local Docker setup discussed for this project; verify
them against your current Compose file.

## Prerequisites

Install the following tools before running the project:

-   Java version required by the individual services
-   Maven or the build tool configured in each service
-   Docker Desktop with WSL 2 enabled on Windows
-   Docker Compose
-   `kubectl` and Minikube, if running the Kubernetes manifests

Check the installed tools:

``` bash
java -version
docker --version
docker compose version
kubectl version --client
minikube version
```

## Configuration and Environment Variables

The services use Spring property placeholders in the form
`${VARIABLE_NAME:default-value}`. An environment variable can override
the default when the container starts. The following names and defaults
are confirmed by the `reviewms` configuration shared for this README;
check the other services' property files for their exact settings.

| Variable | Default shown in configuration | Purpose |
|---|---|---|
| `JOBMS_URL` | `http://job` | Job service base URL |
| `COMPANYMS_URL` | `http://company` | Company service base URL |
| `REVIEWMS_URL` | `http://review` | Review service base URL |
| `DB_URL` | `jdbc:postgresql://postgres:5432/review` | Review service database URL |
| `DB_USERNAME` | `postgres` | Database username |
| `DB_PASSWORD` | `changeme` | Database password |
| `DDL_AUTO` | `update` | Hibernate schema handling |
| `ZIPKIN_URL` | `http://zipkin:9411/api/v2/spans` | Zipkin span collector endpoint |
| `RABBITMQ_HOST` | `rabbitmq` | RabbitMQ hostname |
| `RABBITMQ_USERNAME` | `guest` | RabbitMQ username |
| `RABBITMQ_PASSWORD` | `guest` | RabbitMQ password |

These hostnames assume the corresponding Docker Compose service names or
Kubernetes Services exist and are reachable from the application
container. If your Kubernetes Service names differ, update the defaults
or set the corresponding environment variables in the Deployment
manifests.

**Security and deployment notes**

- Replace example/default credentials with secrets appropriate for your
  environment. Do not commit real credentials to Git.
- `DDL_AUTO=update` is convenient for development, but schema migrations
  are generally preferable for controlled production deployments.
- The configuration sets tracing sampling probability to `1.0`, which
  samples every trace. Consider a lower rate for high-volume deployments.
- Actuator exposes `health`, `info`, `metrics`, and `prometheus` in the
  shared configuration. Ensure sensitive management endpoints are not
  publicly exposed; the health endpoint is configured to show details
  only when authorized.
- The shared properties disable Eureka registration and registry
  fetching. Kubernetes DNS-based discovery therefore depends on the
  Kubernetes Service resources being present.

## Run Locally with Docker Compose

If the repository contains a Compose file, start the services from the
directory containing it:

``` bash
docker compose up -d
```

Check container status and logs:

``` bash
docker compose ps
docker compose logs -f
```

To inspect a specific service:

``` bash
docker compose logs -f company
docker compose logs -f job
docker compose logs -f review
```

Stop the Compose application when finished:

``` bash
docker compose down
```

> Before publishing, confirm the Compose service names and whether
> database credentials are provided through environment variables or a
> local `.env` file. Never commit real passwords, tokens, or other
> secrets.

## Run with Kubernetes / Minikube

If Kubernetes manifests are included in the repository, start the local
cluster:

``` bash
minikube start --driver=docker
kubectl get nodes
```

Apply the manifests from the directory used by your project:

``` bash
kubectl apply -f k8s/
```

Inspect workloads and services:

``` bash
kubectl get deployments
kubectl get pods
kubectl get services
kubectl get events --sort-by=.lastTimestamp
```

For details about a pod:

``` bash
kubectl describe pod <pod-name>
kubectl logs <pod-name>
```

When you have finished, remove only the resources created for this
project, using the matching manifest path:

``` bash
kubectl delete -f k8s/
```

The Kubernetes commands above assume the repository has a `k8s/`
directory containing valid manifests. Adjust the path if your manifests
are stored elsewhere. Avoid deleting the whole Minikube cluster if it
contains other learning projects.

## API Usage

The API Gateway is exposed locally on port `8084` in the discussed
setup. Use the actual route definitions from the gateway configuration
to determine the complete request paths.

Example base URL:

``` text
http://localhost:8084
```

Example health check, **only if Actuator is enabled and exposed**:

``` text
http://localhost:8084/actuator/health
```

For each API, document the implemented endpoints, HTTP methods, request
bodies, and sample responses here. Do not publish example routes until
you have verified them against the controllers and gateway
configuration.

  -----------------------------------------------------------------------
  Service                             What to document
  ----------------------------------- -----------------------------------
  Company Service                     Create/read/update/delete company
                                      operations that are implemented

  Job Service                         Job listing and job-detail
                                      operations that are implemented

  Review Service                      Review operations that are
                                      implemented

  API Gateway                         Gateway route predicates and target
                                      services
  -----------------------------------------------------------------------

## Observability and Messaging

-   **Zipkin:** Use the configured Zipkin UI, commonly available at
    `http://localhost:9411`, to inspect traces if tracing
    instrumentation is configured.
-   **RabbitMQ:** The management interface is commonly available at
    `http://localhost:15672`. Use the credentials configured for your
    local environment.
-   **Centralized configuration:** Review the Config Server
    configuration and each service's config import settings to
    understand how properties are loaded.
-   **Metrics and health:** The shared service properties expose
    Actuator endpoints for `health`, `info`, `metrics`, and `prometheus`.
    Check the actual management port/path and access rules before
    documenting externally accessible URLs.
-   **Service discovery:** In Kubernetes, verify the relevant Service
    resources and DNS names. The shared configuration disables Eureka;
    a registry UI at `http://localhost:8761` is relevant only if a
    separate local setup actually runs and uses it.

The availability of traces, queues, and registered instances depends on
the application's current configuration and whether the relevant
integrations are enabled.

## Troubleshooting

### Kubernetes node is `NotReady`

``` bash
kubectl get nodes
kubectl describe node minikube
```

If Minikube or Docker Desktop is unhealthy, diagnose the Docker/WSL
runtime before deleting pods or recreating the cluster.

### Pods remain in `Terminating`

``` bash
kubectl describe pod <pod-name>
kubectl get events --sort-by=.lastTimestamp
```

A stuck pod can indicate a node or container-runtime issue. Force
deletion should not be the first troubleshooting step when the node is
unhealthy.

### A service is unavailable

``` bash
docker compose ps
docker compose logs <service-name>
```

Confirm the service is running, its configured port is correct,
dependencies are reachable, and the gateway route matches the service.
For container deployments, check that environment-variable overrides
and Docker/Kubernetes service names match the hostnames used by the
application properties.

### Database connection fails

Check PostgreSQL container status, database configuration, credentials,
and whether the application is using the correct hostname for its
environment. Within a Compose network, services usually connect using
the Compose service name rather than `localhost`.

## Engineering Practices

When extending this project, consider:

-   Validating request payloads and returning consistent error
    responses.
-   Keeping service configuration outside source code and avoiding
    committed secrets.
-   Adding unit and integration tests for controllers and service-layer
    logic.
-   Applying health checks and sensible timeouts to service
    dependencies.
-   Documenting gateway routes and API contracts.
-   Using structured logs and distributed traces to investigate
    cross-service failures.
-   Pinning image and dependency versions for repeatable builds.

## Roadmap

Possible future improvements, if not already implemented:

-   Add OpenAPI/Swagger documentation for each service.
-   Add automated tests and a CI pipeline.
-   Add authentication and authorization at the gateway.
-   Add service health checks and readiness/liveness probes.
-   Add metrics and dashboards.
-   Add retry, timeout, and circuit-breaker policies for remote calls.
-   Add sample requests or a Postman collection.

## Repository Structure

Your actual directory structure may differ. A possible organization is:

``` text
.
├── company-service/
├── job-service/
├── review-service/
├── api-gateway/
├── service-registry/
├── config-server/
├── docker-compose.yml
└── k8s/
```

Rename these entries to match the real directories in the repository.

## Contributing

1.  Create a feature branch.
2.  Make a focused change.
3.  Run the relevant tests.
4.  Verify the service locally.
5.  Open a pull request with a clear summary and testing notes.

## License

Add a license file if you intend to make this project open source. If
this repository is a personal portfolio project, specify the license you
want to use rather than assuming one.
