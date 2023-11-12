# Test

Set necessary environment variables:

```sh
export COS_ENDPOINT="s3.eu-de.cloud-object-storage.appdomain.cloud"
export COS_API_KEY_ID="..."
export COS_SERVICE_CRN="crn:v1:bluemix:public:cloud-object-storage:..."
export COS_BUCKET_LOCATION="eu-de"
export COS_BUCKET_NAME="quarkus-eclipse-store"
```

Run `quarkus dev` to bring it up.

## List all products

`curl http://localhost:8080/products`

## Get product by identifier

`curl http://localhost:8080/products/ab371k`

## Create product by identifier

`curl --request POST --header 'Content-Type: application/json' --data '{"id":"kn2s6a","name":"Couch","price":199.99}' http://localhost:8080/products`

## Delete product by identifier

`curl --request DELETE http://localhost:8080/products/kn2s6a`
