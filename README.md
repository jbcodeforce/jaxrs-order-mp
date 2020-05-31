# JaxRS service to serve as template for microprofile 3.x app

## What is it

This is a grouping of jaxrs, CDI, metrics, openapi, health. liveness, from OpenLiberty guides, so this repository can be used for starting app.

## Build

### Locally

```shell
mvm package
# With docker
docker build -t jbcodeforce/orderms .
```

### On Openshift

The dockerfile is a multistage with build and run so oc build will process it.

```shell
oc new-app . --name='orderms'
# Create a route
oc expose svc/orderms
```

## Run 

### Locally

```shell
mvn liberty:dev
# or
mvn liberty:run
```

Or with docker:

```shell
docker run -d --name orderms -p 9080:9080 -p 9443:9443 -v $(pwd)/target/liberty/wlp/usr/servers:/servers jbcodeforce/orderms
```

The application context is : [http://localhost:9080/orderms](http://localhost:9080/orderms)

* Health test [http://localhost:9080/health]( http://localhost:9080/health)
* Readiness [http://localhost:9080/health/ready ](http://localhost:9080/health/ready)
* Access to the application metrics [http://localhost:9080/metrics](http://localhost:9080/metrics)

### On Openshift

Once the build is done the app / pod is started.

```shell
oc get pods
```

## Demonstrate

* The metrics is exposed and secured with the openliberty `quickStartSecurity`.

## Monitoring

## Troubleshouting