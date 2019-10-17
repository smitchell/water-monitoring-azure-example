# Getting Started

This README file picks-up the Azure set-up from where the parent README file left off. It
assumes the following is already done:
* Azure CLI installed
* CLI login successful
* The Resource Group name environment variable, GROUP, exists.

# Azure Setup

This project needs to publish water level observations. It uses the Azure Service Bus for messaging.
To setup Azure Service Bus you need to do the following.
* Create the Service Bus Namespace
* Create the Service Bus Topic
* Create the Service Bus Read/Write Authorization Rule

## Create a Service Bus Namespace
The Service Bus name must be unique across Azure. The namespace "rivers"
was available at the time of this writing. Use any name you like that is available.
You will export that to the "NAMESPACE" environment variable and will use
the variable name in all the commands.

### Find an available Service Bus namespace name
Rerun the two commands below as needed until you find an available Namespace name.
```
export NAMESPACE=rivers-$(openssl rand -hex 6)
az servicebus namespace exists --name $NAMESPACE
```

### Create Service Bus namespace
This uses the resource group created in the parent project README file.
```
az servicebus namespace create --resource-group ${GROUP}  --name ${NAMESPACE}
```

## Create a Service Bus Topic for River Observations
Create a topic for publishing river observation events. For this exercise it is called "RiverObservationsTopic".
```
export RIVERS_TOPIC=RiverObservationsTopic
az servicebus topic create --resource-group ${GROUP} \
    --namespace-name ${NAMESPACE} \
    --name ${RIVERS_TOPIC}
```

### Create a Read/Write Authorization rule
```
az servicebus namespace authorization-rule create --resource-group ${GROUP} \
 --namespace-name ${NAMESPACE} --name ReadWrite --rights Listen Send
```

### Lookup the ReadWrite primaryConnectionString Using --query
You need the primary connection string from the authorization rule created above.
Adding --query to the following command returns the connection string without all the other JSON.
```
 az servicebus namespace authorization-rule keys list --resource-group ${GROUP} \
  --namespace-name ${NAMESPACE} --name ReadWrite --query 'primaryConnectionString' -o json

"Endpoint=sb://rivers.servicebus.windows.net/;SharedAccessKeyName=ReadWrite;SharedAccessKey=*******************"
```

### Create a Read-only Authorization rule
```
az servicebus namespace authorization-rule create --resource-group ${GROUP} \
 --namespace-name ${NAMESPACE} --name ReadOnly --rights Listen
```

Lookup the ReadWrite primaryConnectionString to use in the application configuration.
```
 az servicebus namespace authorization-rule keys list --resource-group ${GROUP} \
  --namespace-name ${NAMESPACE} --name ReadOnly --query 'primaryConnectionString' -o json

"Endpoint=sb://rivers.servicebus.windows.net/;SharedAccessKeyName=ReadOnly;SharedAccessKey=********************"
```

## Create an Azure Storage Account

The project requires a Storage account in order to store river photos from the monitor stations. It uses BLOB storage.
```
export STORAGE_NAME=rivermonitorstorage
az storage account create \
  --resource-group $GROUP \
  --kind StorageV2 \
  --name $STORAGE_NAME \
  --https-only false \
  --location centralus
```   

Create Role-based access control (RBAC) to allow access to the Azure storage account.
```
az ad sp create-for-rbac --sdk-auth > river.azureauth
```
* ad : Activity Directory
* sp : Service Principal
* create-for-rbac : Create a service principal and configure its access to Azure resources.
* --sdk-auth : Output result in compatible with Azure SDK auth file.

Copy the river.azureauth into the monitor station project:
* water-monitoring-azure-example/MonitorStation/src/main/resources/river.azureauth
* water-monitoring-azure-example/MonitorStation/src/main/resources/river.azureauth

The monitor station should now start-up and being publishing events and storing new image files in the Azure storage account.



