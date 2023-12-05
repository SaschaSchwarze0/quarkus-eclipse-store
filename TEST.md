# Test

## Run locally

Set necessary environment variables:

```sh
export CLOUD_OBJECT_STORAGE_APIKEY="..."
export CLOUD_OBJECT_STORAGE_RESOURCE_INSTANCE_ID="crn:v1:bluemix:public:cloud-object-storage:..."
export CLOUD_OBJECT_STORAGE_BUCKET_LOCATION="eu-de"
export CLOUD_OBJECT_STORAGE_BUCKET_NAME="quarkus-eclipse-store"
```

### Start the application

Run `quarkus dev` to bring it up.

### Test the application

#### List all products

```sh
curl http://localhost:8080/products
```

#### Get product by identifier

```sh
curl http://localhost:8080/products/ab371k
```

#### Create product by identifier

```sh
curl --request POST --header 'Content-Type: application/json' --data '{"id":"kn2s6a","name":"Couch","price":199.99}' http://localhost:8080/products
```

#### Delete product by identifier

```sh
curl --request DELETE http://localhost:8080/products/kn2s6a
```

## Run in IBM Cloud

The following steps outline how to setup this project in the IBM Cloud as application in IBM Cloud Code Engine that uses IBM Cloud Object Storage as persistent storage. Each step provides a shell snippet that you must run. Note that those snippets declare variables which are used within the snippet itself, but also in snippets of later steps. You must therefore run all steps in sequence in one shell.

### Install the CLI

[Knowledge Center: Installing the stand-alone IBM Cloud CLI](https://cloud.ibm.com/docs/cli?topic=cli-install-ibmcloud-cli)

```sh
curl -fsSL https://clis.cloud.ibm.com/install/linux | sh
ibmcloud plugin install cloud-object-storage
ibmcloud plugin install code-engine
```

### Login to your account

```sh
REGION=eu-de

ibmcloud login --sso -r "${REGION}"
```

### Create a resource group

```sh
RESOURCE_GROUP=quarkus-eclipse-store

ibmcloud resource group-create "${RESOURCE_GROUP}"
ibmcloud target -g "${RESOURCE_GROUP}"
```

### Create the Cloud Object Storage instance and a Bucket

The bucket name contains a random suffix because bucket names must be unique across the Cloud Object Storage instances of all users of IBM Cloud.

```sh
CLOUD_OBJECT_STORAGE_RESOURCE_INSTANCE_NAME=quarkus-eclipse-store-cos
CLOUD_OBJECT_STORAGE_BUCKET_NAME="quarkus-eclipse-store-bucket-${RANDOM}"

ibmcloud resource service-instance-create "${CLOUD_OBJECT_STORAGE_RESOURCE_INSTANCE_NAME}" cloud-object-storage standard global -g "${RESOURCE_GROUP}" -d premium-global-deployment-iam

CLOUD_OBJECT_STORAGE_RESOURCE_INSTANCE_GUID="$(ibmcloud resource service-instance "${CLOUD_OBJECT_STORAGE_RESOURCE_INSTANCE_NAME}" --output json | jq -r '.[].guid')"

ibmcloud cos bucket-create --bucket "${CLOUD_OBJECT_STORAGE_BUCKET_NAME}" --class smart --ibm-service-instance-id "${CLOUD_OBJECT_STORAGE_RESOURCE_INSTANCE_GUID}" --region "${REGION}"
```

### Create the Code Engine project

```sh
CODEENGINE_PROJECT_NAME=quarkus-eclipse-store-ce

ibmcloud ce project create --name "${CODEENGINE_PROJECT_NAME}"
```

### Create the Code Engine application

The [.buildenv](.buildenv) file defines environment variables for the build. In particular we must guide Paketo what to pick from the build output into the container image.

[Application scaling](https://cloud.ibm.com/docs/codeengine?topic=codeengine-app-scale) is configured with min=max=1 because the startup time of a Java application is not rocket-fast, and Eclipse Store does not allow concurrent access to a data store.

Two environment variables are set to point to the COS bucket. The access to the service instance itself is configured in the [next step using a service binding](#bind-cloud-object-storage).

Note that the creation of the application can take up to five minutes.

```sh
CODEENGINE_APP_NAME=quarkus-eclipse-store

ibmcloud ce app create --name "${CODEENGINE_APP_NAME}" --build-source . --env CLOUD_OBJECT_STORAGE_BUCKET_LOCATION="${REGION}" --env CLOUD_OBJECT_STORAGE_BUCKET_NAME="${CLOUD_OBJECT_STORAGE_BUCKET_NAME}" --min-scale 1 --max-scale 1

CODEENGINE_APP_URL="$(ibmcloud ce app get --name "${CODEENGINE_APP_NAME}" --output json | jq -r '.status.url')"
```

### Bind Cloud Object Storage

[Documentation](https://cloud.ibm.com/docs/codeengine?topic=codeengine-bind-services#bind-cli)

```sh
ibmcloud ce app bind --name "${CODEENGINE_APP_NAME}" --service-instance-id "${CLOUD_OBJECT_STORAGE_RESOURCE_INSTANCE_GUID}"
```

### Update the Code Engine application

After you made code changes, just run:

```sh
# optionally reselect the project if your session is gone already
ibmcloud ce project select --name "${CODEENGINE_PROJECT_NAME}"
ibmcloud ce app update --name "${CODEENGINE_APP_NAME}" --build-source .
```

### Test the application

#### List all products

```sh
curl "${CODEENGINE_APP_URL}/products"
```

#### Get product by identifier

```sh
curl "${CODEENGINE_APP_URL}/products/ab371k"
```

#### Create product by identifier

```sh
curl --request POST --header 'Content-Type: application/json' --data '{"id":"kn2s6a","name":"Couch","price":199.99}' "${CODEENGINE_APP_URL}/products"
```

After creating a product, you might want to rerun the list command from above to verify it was successfully created.

#### Delete product by identifier

```sh
curl --request DELETE "${CODEENGINE_APP_URL}/products/kn2s6a"
```

After deleting a product, you might want to rerun the list command from above to verify it was successfully deleted.

#### Restart the application

You can restart the application using the following command. That way you can double-check that data was really persisted in Cloud Object Storage.

```sh
ibmcloud ce application restart --application "${CODEENGINE_APP_NAME}"
```

Note that after a restart, your first call will take longer as the application restarts.

### Cleanup everything

```sh
ibmcloud ce project delete --name "${CODEENGINE_PROJECT_NAME}" --force --hard
ibmcloud resource service-instance-delete "${CLOUD_OBJECT_STORAGE_RESOURCE_INSTANCE_NAME}" -g "${RESOURCE_GROUP}" --force --recursive
ibmcloud resource group-delete "${RESOURCE_GROUP}" --force
```
