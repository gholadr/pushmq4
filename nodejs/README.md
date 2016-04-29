#### Nodejs mqtt client

Simple node js mqtt client used for testing purposes. Default topic is `co/ghola/mqtt/test`

Running on [mosca] (https://www.npmjs.com/package/mosca)

###### How to install

`cd nodejs/`

`npm install`

###### How to run

For help, type `node client.js --help` 

Basic usage

`node client.js --message='Hello Friends!'`

Publish, but with different topic

`node client.js  --message='Hello Friends!' --topic='my/new/topic'`

`Publish, but with different broker

`node client.js  --message='Hello Friends!' --broker='tcp:host:1883'`
