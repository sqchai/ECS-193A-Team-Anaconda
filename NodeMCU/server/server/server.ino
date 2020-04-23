#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>

ESP8266WebServer server;
uint8_t pin_led = 16;

//motors
uint8_t m1 = D1;
uint8_t m1_dir = D3;
int m1_speed = 0;

char* ssid = "X Air";
char* password = "Jiang991022";

void setup() {
  // put your setup code here, to run once:
  pinMode(pin_led, OUTPUT);

  //setup motor pwm
  pinMode(m1, OUTPUT);
  pinMode(m1_dir, OUTPUT);
  analogWrite(m1, m1_speed);
  digitalWrite(m1_dir, LOW);
  
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
  m1_speed = server.arg("m1speed").toInt();
  analogWrite(m1, m1_speed);
}
