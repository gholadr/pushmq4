##### java MQTT client

###### How to install/run

Download [IntelliJ IDEA](https://www.jetbrains.com/idea/download/), run gradle, pick 1.8 JRE and make sure [language level is set to 8](http://stackoverflow.com/questions/22703412/java-lambda-expressions-not-supported-at-this-language-level)´in order to support lambda expressions

This demo runs on port `8081`

###### Basic Usage

Use [Postman](https://chrome.google.com/webstore/detail/postman/fhbjgbiflinjbdggehcddcbncdddomop?hl=en)

do a simple POST on localhost:8081/publish and use x-www-form-urlencoded with key/value pair message/value

cURL
```
curl -X POST -H "Content-Length: 82" -H "Connection: close" -H "Content-Type: application/x-www-form-urlencoded" -H "Cache-Control: no-cache" -H -d 'message=not so beautiful!' "http://0.0.0.0:8081/publish"
```

HTTP
```
POST /publish HTTP/1.1
Host: 0.0.0.0:8081
Content-Length: 82
Connection: close
Content-Type: application/x-www-form-urlencoded
Cache-Control: no-cache
Postman-Token: 6333a955-9876-c677-1730-279857562180

message=not+so+beautiful!
```
