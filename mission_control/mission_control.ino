#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>

//Network Settings
ESP8266WebServer server;
char* ssid = "X Air";
char* password = "Jiang991022";

//LED
uint8_t pin_led = 16;

bool flag = true;

//Setup
void setup() {
  Serial.begin(115200);
  
  // put your setup code here, to run once:
  pinMode(pin_led, OUTPUT);
  
  WiFi.begin(ssid, password);
  while(WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }

  //server.on("/toggle", toggleLED);
  server.on("/check", check);
  server.on("/delay", waitT);
  server.begin();
}

//void toggleLED() {
//  digitalWrite(pin_led, !digitalRead(pin_led));
//  server.send(200, "text/plain", "hello");
//}

void waitT() {
  server.send(200, "text/plain", "delay");
  flag = false;
  delay(10000);
  yield();
  flag = true;
}

void check() {
  if(flag) {
    server.send(200, "text/plain", "ready");
  } else {
    server.send(200, "text/plain", "not ready");
  }
}

void loop() {
  server.handleClient();
}
