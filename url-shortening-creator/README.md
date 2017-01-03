# Short Url Creator

Microservice for processing create command 

Usage :

*Prefered Short Url
curl -X POST -H  "Content-Type:application/json"  http://<hostname>:<port>/url/create --data '{"longUrl" : "abc.com" , "shortUrl": "short"}'

*Random Generated Short Url
curl -X POST -H  "Content-Type:application/json"  http://<hostname>:<port>/url/create --data '{"longUrl" : "abc.com"}'

