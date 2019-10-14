# Azure Water Monitoring System

The purpose of this example is to demonstrate the use of Azure storage options.
It consists of the following:
1) Monitoring Station
2) Flood Warning Service
3) Notification Service
4) API Gateway

For simplicity, there is no security or cloud services.
Here is brief description of the systems:

## Monitoring Stations
Uses a sensor to measure river level.

### API
* PUT void stationPreference: StationPreferences preferences
* GET StationPreferences stationPreference: void
* GET Observation observation: void

## Flood Warning Service
Uses data collected from monitoring stations to detect flood stage:
* 5 - Major Flooding
* 4 - Moderate Flooding
* 3 - Minor Flooding
* 2 - Near Flood Stage
* 1 - No Flooding
* 0 - At or Below Low Water Threshold

### API
* POST monitoringStation createMonitoringStations: MonitoringStation monitoringStation
* monitoringStation putMonitoringStations: MonitoringStation monitoringStation
* StationPreferences getStationPreferences: String stationId

## Notification Service

### API
* void addRecipient: NotificationPreferences notificationPreferences
* void removeRecipient: String emailAddress

### Events
* notificationSent

## API Gateway

### API
* void changeStationPreferences: StationPreferences preferences
* StationPreferences getStationPreferences: String stationId
* List riverForecast: String stationId
* void addRecipient: NotificationPreferences notificationPreferences
* void removeRecipient: String emailAddress

### Events
* stationOnline
* stationOffline
* measurementObserved

# Running this Example

Each project has it's own read about how resources were created on Azure
if you wish to create your own. If you choose to create your own resource
you will need the Azure CLI and, at a minimum, the a free trial account.

# Azure Setup

Each of the child project has its own README file that covers the creation of Azure
resources need by that project. Follow the README instructions in this order:
1) This README.
2) Monitor Station README.
3) Flood Warning Service README.

Here we will login to Azure to create a Resource Group under which all Azure resources
for the all projects will be created.  

## Install the Azure CLI
Follow the instructions to install the Azure CLI and login
(https://docs.microsoft.com/en-us/cli/azure/install-azure-cli?view=azure-cli-latest).
```
az login
```

## Create a Resource Group
Begin by looking at the list of Azure locations to find the location you wish to use.
```
az account list-locations -o table
```

I chose the "centralus location". Name the group anything you want. I used the name "azure-training" for this exercise.

### Find Available Resource Group Name
```
export GROUP=azure-training
az group create --name $GROUP --location centralus

```


create table surface_water_monitor_point1 (
       id varchar(255) not null,
        flood_major int not null,
        flood_minor int not null,
        flood_moderate int not null,
        lat numeric(19,2),
        lon numeric(19,2),
        name varchar(255),
        station_id varchar(255),
        primary key (id)
    )

    GRANT SELECT, DELETE -- , etc.
       ON SCHEMA::schema_name
       TO user_name;

     GRANT ALL PRIVILEGES ON SCHEMA flood_warnings TO FloodWarningService;

    SELECT * FROM sysobjects a
    LEFT JOIN syspermissions b
    ON a.id=b.id
    WHERE a.xtype='P'
    AND b.id IS NULL;

    create table surface_water_monitor_point1 (
       id varchar(255) not null,
        flood_major int not null,
        flood_minor int not null,
        flood_moderate int not null,
        lat numeric(19,2),
        lon numeric(19,2),
        name varchar(255),
        station_id varchar(255),
        primary key (id)
    )
