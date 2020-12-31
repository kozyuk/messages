### JAVA HOME ASSIGNMENT SKF AI

#### How to build it:
`mvn clean install`

For build use JDK 1.8 (Oracle or OpenJdk) and maven

#### How to run it with docker-compose:
`docker-compose up`

#### API usage examples:

##### Publish new messages to the REDIS server 
###### Request:
    POST http://localhost:8080/publish. 
    Content-Type: application/json. 
    {"content" : "test content 1"}. 

###### Response:
    HTTP/1.1 200
    Content-Length: 0
    Date: Wed, 30 Dec 2020 23:52:41 GMT
    Keep-Alive: timeout=60
    Connection: keep-alive
    <Response body is empty>
    Response code: 200; Time: 1430ms; 
    Content length: 0 bytes

##### Retrieve the last message that was on the REDIS server 
###### Request:
    GET http://localhost:8080/getLast
    Accept: */*
    Cache-Control: no-cache
###### Response:
    HTTP/1.1 200 
    Content-Type: application/json
    Transfer-Encoding: chunked
    Date: Thu, 31 Dec 2020 00:16:46 GMT
    Keep-Alive: timeout=60
    Connection: keep-alive
    {
      "content": "test content 4"
    }
    Response code: 200; Time: 119ms; Content length: 28 bytes

##### Retrieve all messages that were on the REDIS server
###### Request:
    GET http://localhost:8080/getByTime?start=1609369070039&end=1609372360483
    Accept: application/json
###### Response:
    HTTP/1.1 200 
    Content-Type: application/json
    Transfer-Encoding: chunked
    Date: Thu, 31 Dec 2020 00:13:35 GMT
    Keep-Alive: timeout=60
    Connection: keep-alive
    [
      "test content 1",
      "test content 2"
    ]
    Response code: 200; Time: 101ms; Content length: 35 bytes
