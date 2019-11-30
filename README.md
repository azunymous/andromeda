# Andromeda kubernetes dashboard

**Current Status:**

- Backend APIs: Currently fetches version, liveness & readiness of deployments, statefulSets and pods. Dependencies, feature flags are 
available via Prometheus metrics.
- Frontend Dashboard: work in progress

# Getting started:

Andromeda can be quickly deployed to minikube with `skaffold run`

_You will require kubectl, kustomize, minikube, docker and skaffold_

# Pretext
Andromeda is designed with the idea of deploying a collector into each cluster with a mounted service account bound with a cluster role similar to the default 
`view` cluster role. Each collector should be configured with the environments (namespace suffixes) required for that cluster in the cluster specific config.
All collectors will use the same global config specifying the applications and the namespace prefixes to be scraped.

The aggregator should be deployed in only one cluster along with the web frontend. The aggregator should be configured
to scrape all collectors via accessible ingresses or other multi-cluster networking.
# Configuration
Andromeda's configuration and scraping of Kubernetes clusters is based on selectors and namespaces. 

## Collector
Using a configured namespace prefix for an application and a selector, each collector will search for namespaces matching the configured
global namespace prefix for an app + cluster specific namespace suffix for each environment.

e.g Configuration as follows:
```yaml
# Global config
global:
  teams:
    - name: teamName
      applications:
        - name: web-frontend
          prefix: webapp
          selector:
            type: frontend
        - name: backend-application
          prefix: backend
          selector:
            type: backend
          statefulSet: backend-app-container
```
```yaml
# Cluster specific
cluster:
  namespaceSuffixes:
    - -ft
    - -testing
    - -release
  priority:
    last:
      - -release
```

For the web-frontend, the collector will look at namespaces in turn:
- `webapp-ft`
- `webapp-testing`
- `webapp-release`

Then it will look for both deployments (by default) and pods with the specified selector. 
In this case: `type = frontend`. 

The first container in the deployment's image is used to find the version and
the deployment's replica availability is used to find the health.

A different container and the pod controller type can be specifically configured via the 
`<controller name>: <container name>` configuration. 

e.g the `statefulSet: backend-app-container` above would look for stateful sets rather than deployments and would 
read the version of specifically the `backend-app-container`.

## Aggregator
The aggregator requires configuring _cluster groups_ which each have a list of collector URIs. The cluster groups
are logically grouped clusters e.g by region. 

_Note: The collector URIs should not include the `team` path segment but point to the application root of the collector._

Each cluster group is intended to be shown on one dashboard page.
```yaml
aggregator:
  clusters:
    cluster-one:
      type: kubernetes
      collectors:
       - http://my.other.cluster.com
       - http://another.cluster/collector
    cluster-two:
      type: kubernetes
      collectors:
       - http://third.cluster.com/team/
  teams:
    - awesome-team
  prometheusURI: http://localhost:9091/api/v1/
```

The `teams` can be configured for front end indexing. The `prometheusURI` is required for fetching dependency and feature flag information.
  