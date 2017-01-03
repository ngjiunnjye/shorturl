# SHORT URL Project
url shortening

This project contain 2 microservices 
- short url creator 
- short url redirect

# Short Url Creator 
Process short url request from client with longurl and optional shorturl, register the request and apply command to create the short url. 
If shorturl is not presence, a random url will be generated for it. 


# Short Url Redirect
This is the Read perspective of the solution. This microservice perform 2 main tasks
- This microservice replicate the command from Short Url Creator and populate the Query Database. 
- Resolve short url from client request, look up from the Query database and and redirect to the original 

