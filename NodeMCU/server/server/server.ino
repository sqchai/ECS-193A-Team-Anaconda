#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>

ESP8266WebServer server;
uint8_t pin_led = 16;

//motors
uint8_t m1 = D1;
bool m1_running_high = false;

char* ssid = "X Air";
char* password = "Jiang991022";

void setup() {
  // put your setup code here, to run once:
  pinMode(pin_led, OUTPUT);

  //setup motor pwm
  //pinMode(m1, OUTPUT);
  
  WiFi.begin(ssid, password);
  Serial.begin(9600);
  while(WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  Serial.println("");
  Serial.print("IP Address: ");
  Serial.print(WiFi.localIP());

  server.on("/", [](){server.send(200, "text/plain", "Hello World!");});
  server.on("/toggle", toggleLED);

  //motor test
  server.on("/m1", motor1);
  server.begin();
}

void loop() {
  // put your main code here, to run repeatedly:
  server.handleClient();
}

void toggleLED() {
  digitalWrite(pin_led, !digitalRead(pin_led));
  server.send(204, "");
}

void motor1() {
  if(m1_running_high) {
    analogWrite(m1, 512);
    m1_running_high = false;
  } else {
    analogWrite(m1, 1023);
    m1_running_high = true;
  }
}
