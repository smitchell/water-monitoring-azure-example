# Getting Started

This README file picks-up the Azure set-up from where the Monitor Station README file left off. It
assumes the following is already done:
* Azure CLI installed
* CLI login successful
* The Resource Group name was exported as GROUP.
* The Service Bus Namespace was exported as NAMESPACE
* The Service Bus Topic was exported ast RIVERS_TOPIC.

# Azure Setup

This project needs to do several things.
* Subscribe to the rivers topic.
* Write the observations to a database.
* Publish alerts to a Service Bus Topic.

## Create a Topic Subscription for River Observations
Create a subscription to the top.

```
az servicebus topic subscription create --resource-group ${GROUP}  \
    --namespace-name ${NAMESPACE} --topic-name ${RIVERS_TOPIC} \
    --name FloodMonitoring
```

## Create a Service Bus Topic to Publish Flood Advisories
Create a topic for the flood advisory events. For this exercise it is called "FloodAdvisoryTopic".
```
export FLOOD_ADVISORY_TOPIC=FloodAdvisoryTopic
az servicebus topic create --resource-group ${GROUP} \
    --namespace-name ${NAMESPACE} \
    --name ${FLOOD_ADVISORY_TOPIC}
```


## Create the SQL Server

It is easiest to create the SQL Database using the Azure portal. If you want to use the Azure CLI follow this script:
https://docs.microsoft.com/en-us/azure/sql-database/scripts/sql-database-create-and-configure-database-cli?toc=%2fcli%2fazure%2ftoc.json.

### Create the SQL Database
Use Home > SQL Database to add a new SQL Database: (https://azure.microsoft.com/en-us/resources/videos/create-sql-database-on-azure/).
* Make the server public and select the options to "Allow this IP Address" to whitelist the IP of the computer you are using for this lab.

### Connect to the SQL Database
Use the tool of your choice to connect to the master database using the Admin id and password created above.
I download Azure Data Studio for my Mac (https://docs.microsoft.com/en-us/sql/azure-data-studio/what-is?view=sql-server-ver15)
* Navigate to Home > SQL Database > river-db-service. Locate the Service Name on the upper right site of the page and copy it.
* Launch Azure Data Studio.
* Create a new Database connection using the Admin user, password, and Server Name.

### Create a database user for the Flood Warning Service
* Start a query, make sure the "master" database is selected and enter the following SQL:
```
CREATE LOGIN FloodWarningService WITH PASSWORD = '***********************';
```

Switch to the river-db-service database.
```
CREATE USER FloodWarningService FROM LOGIN FloodWarningService; 
ALTER ROLE [db_owner] ADD MEMBER [FloodWarningService];
```

