#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <Servo.h>

ESP8266WebServer server;

bool flag;

//LED
uint8_t pin_led = 16;

//motors
uint8_t m1_speed = D1;
uint8_t m1_dir = D3;
uint8_t m2_speed = D2;
uint8_t m2_dir = D4;
int speed1 = 0;
int speed2  = 0;
int dir1 = 0;
int dir2 = 0;

//servo
uint8_t s1_port = 7;
Servo s1;

char* ssid = "X Air";
char* password = "Jiang991022";

void setup() {
  // put your setup code here, to run once:
  pinMode(pin_led, OUTPUT);

  //setup motor pwm
  pinMode(m1_speed, OUTPUT);
  pinMode(m1_dir, OUTPUT);
  analogWrite(m1_speed, speed1);
  digitalWrite(m1_dir, LOW);
  pinMode(m2_speed, OUTPUT);
  pinMode(m2_dir, OUTPUT);
  analogWrite(m2_speed, speed2);
  digitalWrite(m2_dir, LOW);

  //setup servo
  flag = true;
  s1.attach(13);
  s1.write(10);
  
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
  server.on("/motor", motor);

  //servo test
  server.on("/servo1", servo1);
  
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

void motor() {
  speed1 = server.arg("speed1").toInt();
  speed2 = server.arg("speed2").toInt();
  dir1 = server.arg("dir1").toInt();
  dir2 = server.arg("dir2").toInt();
  
  server.arg("dir2").
  analogWrite(m1_speed, speed1);
  analogWrite(m2_speed, speed2);
  digitalWrite(m1_dir, dir1);
  digitalWrite(m2_dir, dir2);
  
  digitalWrite(pin_led, !digitalRead(pin_led));
  server.send(204, "received");
}

void servo1() {
  int angle = 170;
  if(flag) {
    angle = 10;
    flag = false;
  } else {
    angle = 170;
    flag = true;
  }
  //angle = server.arg("servoAngle").toInt();
//  if(angle < 10) {
//    angle = 10;
//  } else if(angle > 170) {
//    angle = 170;
//  }

  s1.write(angle);
  digitalWrite(pin_led, !digitalRead(pin_led));
  server.send(204, "received");
}
