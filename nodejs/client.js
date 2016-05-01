var mqtt = require('mqtt');
var url = require('url');
var argv = require('minimist')(process.argv.slice(2));
var art = require('ascii-art');


art.font('MQTT', 'Basic', 'red').font('v0.1', 'Doom', 'magenta', function(rendered){
    console.log(rendered);
    start();
});


function start(){

  
  if(argv.help){
  console.log(art.style('required arg --message=\'Hello again old friend !\'', 'black+green_bg'));
  console.log(art.style('optional args --broker=\'tcp://IP:1883\' --topic=\'my/topic\'', 'black+green_bg'));
  console.log(art.style('--help for help', 'black+green_bg'));
  process.exit();
}

  var message;
  if(argv.message == undefined){
    console.log();
    console.log(art.style('missing --message. type --help for help', 'black+underline+red_bg'));

    process.exit();
  }
  else{
    message = "from Mosca, with love:" + argv.message;
  }
  
  //var mqtt_url = url.parse("tcp://iot.eclipse.org:1883");
  var mqtt_url = url.parse("tcp://192.168.0.103:1883");

  if(argv.broker !=undefined){
   mqtt_url = argv.broker ;
  }

  var topic = 'co/ghola/mqtt/test';
  if(argv.topic !=undefined){
   topic = argv.topic 
  }
  console.log()
  console.log(art.style('connecting...', 'black+green_bg'));

  client = mqtt.connect(mqtt_url);
   
  console.log()
  client.on('connect', function() { // When connected
    console.log()
    console.log(art.style("Connected!", 'black+green_bg'));
    // subscribe to a topic
    client.subscribe(topic, function() {
      // when a message arrives, do something with it
      client.on('message', function(topic, message, packet) {
        console.log()
        console.log(art.style("Received '" + message + "' on '" + topic + "'", 'black+green_bg'));
      });
    });

    // publish a message to a topic
    client.publish('co/ghola/mqtt/test',message,function() {
      console.log()
      console.log(art.style("Message is published", 'black+green_bg'));
      client.end(); // Close the connection when published
    });
  });
}