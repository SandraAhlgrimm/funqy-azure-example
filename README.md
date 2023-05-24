# funqy-azure-example

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/funqy-azure-example-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Related Guides

- Funqy HTTP Binding ([guide](https://quarkus.io/guides/funqy-http)): HTTP Binding for Quarkus Funqy framework

## Provided Code

### Funqy HTTP

Start your Funqy functions using HTTP

[Related guide section...](https://quarkus.io/guides/funqy-http#get-query-parameter-mapping)

## Create the cloud infrastructure using Terraform

You need to configure the following environment variables:

```bash
export TF_VAR_AZ_LOCATION=eastus
export TF_VAR_AZ_RESOURCE_GROUP=azure-native-spring-function
export TF_VAR_AZ_FUNCTION_NAME_APP=<your-unique-name>
export TF_VAR_AZ_STORAGE_NAME=<your-unique-name>
```

- `TF_VAR_AZ_LOCATION` : The name of the Azure location to use. Use `az account list-locations` to list available locations. Common values are `eastus`, `westus`, `westeurope`.
- `TF_VAR_AZ_RESOURCE_GROUP` : The resource group in which you will work. It should be unique in your subscription, so you can probably keep the default `azure-native-spring-function`.
- `TF_VAR_AZ_FUNCTION_NAME_APP` : Functions will run into an application, and its name should be unique across Azure. It must contain only alphanumeric characters and dashes and up to 60 characters in length.
- `TF_VAR_AZ_STORAGE_NAME` : Functions binaries and configuration will be stored inside a storage account. Its name should also be unique across Azure. It must be between 3 and 24 characters in length and may contain numbers and lowercase letters only.

In order not to type those values again, you can store them in a `.env` file at the root of this project:

- This `.env` file will be ignored by Git (so it will remain on your local machine and won't be shared).
- You will be able to configure those environment variables by running `source .env`.

Go to the `terraform` directory and run:

- `terraform init` to initialize your Terraform environment
- `terraform apply --auto-approve` to create all the necessary Azure resources

This will create the following Azure resources:

- A resource group that will store all resources (just delete this resource group to remove everything)
- An Azure Functions plan. This is a consumption plan, running on Linux: you will only be billed for your usage, with a generous free tier.
[Here is the full pricing documentation](https://azure.microsoft.com/pricing/details/functions/?WT.mc_id=java-0000-judubois).
- An Azure Functions application, that will use the plan described in the point above.
- An Azure Storage account, which will be used to store your function's data (the binary and the configuration files).

## CICD with GitHub Actions

The `.github/workflows` folder contains a GitHub Actions workflow that will build and deploy your function to Azure.

You need to configure the following environment variables:

- `AZURE_CREDENTIALS` : The content of the `credentials.json` file you downloaded from the Azure portal. You can use `cat credentials.json | base64` to get the content of the file in base64. Or you can use the az cli command

```bash
RESOURCE_ID=$(az group show --name $TF_VAR_AZ_RESOURCE_GROUP --query id -o tsv)
SPNAME="sp-$(az functionapp list --resource-group $TF_VAR_AZ_RESOURCE_GROUP  --query '[].name' -o tsv)"
az ad sp create-for-rbac --name "${SPNAME}" --role contributor --scopes "$RESOURCE_ID" --sdk-auth
```

to generate a new service principal and get the credentials in JSON format.
