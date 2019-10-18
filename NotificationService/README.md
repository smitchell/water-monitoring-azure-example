# Getting Started

This README file picks-up the Azure set-up from where the Flood Warning Service  README file left off. It
assumes the following is already done:
* Azure CLI installed
* CLI login successful
* The Resource Group name created ($GROUP)
* The Service Bus Namespace created ($NAMESPACE)
* The Service Bus rivers topic created ($RIVERS_TOPIC)
* The Service Bus flood advisories topic created ($FLOOD_ADVISORY_TOPIC)
* Azure SQL Database server created

# Azure Setup

This project needs to do several things.
* Persist data in Azure SQL Database tables
  * Flood advisories
  * River level observations
  * Surface water monitoring points
* Subscribe to the rivers topic.
* Publish alerts to a Service Bus Topic.

## Create a Topic Subscription for River Observations
Create a subscription to the topic.

Create a subscription to the top.

```
export FLOOD_ADVISORY_TOPIC=FloodAdvisoryTopic
az servicebus topic subscription create --resource-group ${GROUP}  \
    --namespace-name ${NAMESPACE} --topic-name ${FLOOD_ADVISORY_TOPIC} \
    --name FloodAdvisoryConsumer
```

az servicebus topic subscription list --resource-group ${GROUP} --namespace-name ${NAMESPACE} --topic-name ${FLOOD_ADVISORY_TOPIC} --query "name" -o tsv
FloodAdvistoryMonitoring
FloodAdvistoryMonitoring
FloodAdvistoryConsumer
FloodAlertMonitoring
az servicebus topic subscription --resource-group ${GROUP} --namespace-name ${NAMESPACE} --topic-name ${FLOOD_ADVISORY_TOPIC} -n FloodAdvistoryMonitoring
## Complete the SQL Database Setup
Each microservice has its own database, so one must be created for the notification service.
It reuses the existing SQL Server that was created for the flood warning service.

### Create the SQL Database
Create a new SQL Database for the microservice.
``` 
export GROUP=azure-training
export SQL_SERVER_NAME=$(az sql server list -g $GROUP --query  '[0].name' -o tsv)
export DATABASE_NAME=notification-service-db 
az sql db create -g $GROUP -s $SQL_SERVER_NAME --name $DATABASE_NAME
```

Next, query the JDBC connection string to put in the properties file:
``` 
az sql db show-connection-string -s $SQL_SERVER_NAME -n $DATABASE_NAME --client jdbc
```

### Create a database user for the Flood Warning Service
* Start a query, make sure the "master" database is selected and enter the following SQL:
```
CREATE LOGIN NotificationService WITH PASSWORD = '7ab5f4bd#b97b39&49218849';
```

Switch to the notification-service-db database.
```
CREATE USER NotificationService FROM LOGIN NotificationService; 
ALTER ROLE [db_owner] ADD MEMBER [NotificationService];
```

