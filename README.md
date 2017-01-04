# SHORT URL Project
url shortening.
This solution allow registration of new shorturl request and redirection of short url to original url. 

## Create Short Url Request Sample

*With prefered shortUrl*

Request:

```javascript
{  
   "longUrl":"https://github.com/ngjiunnjye/shorturl",
   "shortUrl":"mySrcCode"
}
```

Response:

```javascript
{  
   "status":true,
   "longUrl":"https://github.com/ngjiunnjye/shorturl",
   "shortUrl":"mySrcCode"
}
```

*Without prefered shortUrl* 

Request:

```javascript
{  
   "longUrl":" http://www.nst.com.my/"
}
```

Respond:

```javascript
{  
   "status":true,
   "longUrl":"nst.com.my",
   "shortUrl":"4"
}
```

# Techonology Stacks

| Tech        | Description           | 
| ------------- |:-------------:| 
| Scala         | Programming Language | 
| Akka          | Toolkit and runtime for building highly concurrent, distributed application      |
| Akka Http     | Exposing Akka actor to http       |
| Akka Cluster Singleton | Implementation of Cluster singleton pattern        |
| Apache Kafka  | Realtime streaming and pipelining     |
| H2 Database   | Lightweight SQL Database   |

# Solution High Level Design
Solution is implemented based on microservice architecture. The solution is break down into small systems that can run its scope of responsibility independently.
The Query (read) and Command (write) processing are separated using CQRS pattern. This pattern allow the read or write component (microservices) to be scale out independently as the need occur.

This project contain 2 microservices 
- url-shortening-creator (for managing request)
- url-shortening-redirect (for managing short url resolution and redirection)

# Inside the microservice

## url-shortening-creator
Process short url request from client with longurl and optional shorturl, register the request and apply command to create the short url. 
If shorturl is not presence, a random url will be generated for it. 

### Design
1)	Support multi node execution to scaling out the solution
2)	Cluster Singleton Pattern for managing Short Url Inventory. 
** in multiple node setup, only 1 of the node will be cluster singleton. In the event this goes down, the next node will resume the responsibility. This ensures service continuality.**
3)	All events are kept as journal and periodic snapshot for recovery
4)	On recovery, snapshot and journal is replayed to reinstate the microservice state. 
5)	shortUrlCommand are partitioned into Kafka by shortUrlId 


### Process flow
1)	Create Url Api receive request from Client
2)	Request for Short Url from URL Inventory Manager (cluster singleton)

3)	Inventory Manager keep url request into Short-Url-Request topic. 
4)	A request snapshot is triggered every interval for summarizing the request. 
5)	CreateUrlApi receive ShortUrl from InventoryManager and store the createShortUrlCommand in kafka. 
6)	CreateUrlApi  construct success/failure Response to client 




## Short Url Redirect
This is the Read perspective of the solution. This microservice perform 2 main tasks
- This microservice replicate the command from Short Url Creator and populate the Query Database. 
- Resolve short url from client request, look up from the Query database and and redirect to the original 

### Design
1)	Support multi node execution to scaling out the solution.
2)	Data is sharded into multiple partitions.
3)	Each node take care of its own database which contains a smaller dataset for faster query responds. 
4)	Nodes are aware of each other and perform redirection request to each other.
5)	Data can be replicated from Kafka Logs for backup/restore or duplicated nodes for High Availability

### Process Flow
1)	WriteToDb Actor replicate the command from Command Actor (url-shortening-creator) into H2 database table. This only process the Command assigned to the node via modulo operation.
2)	Client Request for shortUrlResolution 
3)	UrlResolver lookup the long url from H2 database.
4)	UrlResolver redirect the client to the longUrl. 


## H2 Database
Lightweight SQL Database. This is loosly coupled with the solution. Can be replaced easily.

### Table Design
Table Name : SHORT_URL

| Column Name |	Column Type | 	Description	| Primary Key | 
| --- |:---:|:---:|:---:| 
|ID	  |Big Integer	| Decimal value of the Base62 ShortUrl value|	Y |
|LONG_URL |	Varchar2	|Long Url for redirection	 | |
|Random	| Boolean	| Indicator that the short url is generated or requested	 | |

## Kafka
Bloodstream of the solution. Data flow are conected among the microservices via kafka. 
Increasing the replication factor of the topic will ensure resiliency to the solution. 
Event logging is also do stored in Kafka for playback in case of node crash. 

### Kafka Topic Setup

|Topic	|Partition	|Recommended Replication | Description |
| --- |:---:|:---:|:---:| 
| url.shortening.request	| 1	| 3 | Event Log of all short url creation request. Use for playback |
| url.shortening.request-snapshot	| 1 |	3 | Snapshot of url creation request taken periodically for playback |
| url.shortening.command	| 2 | 	3 | Command of the url request creation. Consumed by the redirect microservice for building the read perspective database.


# Short Url Generation
Short Url Generation is done base on Base 62 encoding which are made up of 0-9, a-z,A-Z. 

ShortUrlId is the decimal equivalent of the Base62. Ie 1 = 1; 10=a; 37=A; 62=Z

## Example 
| base 10 | base 62 | 
|---|:---:| 
|1	| 1 |
|10	| a |
|36	| A | 
|61	| Z |
|62	| 10 |
|124 |	20 |
|3844	|100 |

This is increased by 1 each time a successful ShortUrlRequest is processed. 

A separate list containing all the preferredShortUrl is kept in memory. 

If the increased number collides with the number from the preferredShortUrl, the number is increased again. This goes on until a clean number is found.


# Project Setup 

## Download source code

```
git clone https://github.com/ngjiunnjye/shorturl.git .
```

## Building the project
### url-shortening-common
```
sbt package
```
*copy generated jar to url-shortening-creator/lib and url-shortening-redirect/lib*


### url-shortening-creator
```
sbt assembly
```

### url-shortening-redirect
```
sbt assembly
```

## Configuration 
```
# HTTP Configurations
http.interface = "0.0.0.0"
http.port = 8081

# H2 DB Configuration
db.h2ServerUrls = ["localhost:9101/data/H2/Read0", "localhost:9101/data/H2/Read1"]

# reader
cluster.id = 0
cluster.readerNodeAddressess=["0.0.0.0:8091","0.0.0.0:8092"]

# Loggin
akka.loglevel = DEBUG
```
