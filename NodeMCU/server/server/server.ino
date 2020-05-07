#include <Ticker.h>
#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <Servo.h>

//Network Settings
ESP8266WebServer server;
char* ssid = "X Air";
char* password = "Jiang991022";

//LED Settings
bool flag;
//LED
uint8_t pin_led = 16;

//Motor Settings
const uint8_t mA_speed = D1;
const uint8_t mB_speed = D2;
const uint8_t mA_dir = D3;
const uint8_t mB_dir = D4;
volatile int speedA = 0;
volatile int speedB  = 0;
volatile int dirA = 0;
volatile int dirB = 0;

//Encoders Settings
const uint8_t MA_INT = D7;
const uint8_t MB_INT = D8;
const float DISKSLOTS = 20.00;
const float WHEELDIAMETER = 66.10;
volatile unsigned int count_A = 0;
volatile unsigned int count_B = 0;
Ticker ticker;

//Servo Settings
//uint8_t s1_port = 13;
//Servo s1;


//Setup
void setup() {
  // put your setup code here, to run once:
  pinMode(pin_led, OUTPUT);

  //setup motor pwm
  pinMode(mA_speed, OUTPUT);
  pinMode(mA_dir, OUTPUT);
  analogWrite(mA_speed, speedA);
  digitalWrite(mA_dir, LOW);
  pinMode(mB_speed, OUTPUT);
  pinMode(mB_dir, OUTPUT);
  analogWrite(mB_speed, speedB);
  digitalWrite(mB_dir, LOW);

  //setup encoders
  attachInterrupt(digitalPinToInterrupt(MA_INT), ISR_countA, RISING);
  attachInterrupt(digitalPinToInterrupt(MB_INT), ISR_countB, RISING);
  ticker.attach_ms(100, ISR_ticker);

  //setup servo
  //flag = true;
  //s1.attach(s1_port);
  //s1.write(10);
  
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
  //server.on("/servo1", servo1);
  
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

//encoder counter at interrupt
void ISR_countA() {
  count_A++;
}

void ISR_countB() {
  count_B++;
}

void ISR_ticker() {
  ticker.detach();
  Serial.print("Motor A speed: ");
  float speedA = (count_A / DISKSLOTS) * 60.0;
  Serial.print(speedA);
  Serial.print(" RPM - ");
  count_A = 0;

  Serial.print("Motor B speed: ");
  float speedB = (count_B / DISKSLOTS) * 60.0;
  Serial.print(speedB);
  Serial.print(" RPM");
  count_B = 0;
  ticker.attach_ms(100, ISR_ticker);
}

void motor() {
  speedA = server.arg("speedA").toInt();
  speedB = server.arg("speedB").toInt();
  dirA = server.arg("dirA").toInt();
  dirB = server.arg("dirB").toInt();
 
  analogWrite(mA_speed, speedA);
  analogWrite(mB_speed, speedB);
  digitalWrite(mA_dir, dirA);
  digitalWrite(mB_dir, dirB);
  
  digitalWrite(pin_led, !digitalRead(pin_led));
  server.send(204, "received");
}

//void servo1() {
//  int angle = 170;
//  if(flag) {
//    angle = 10;
//    flag = false;
//  } else {
//    angle = 170;
//    flag = true;
//  }
//  //angle = server.arg("servoAngle").toInt();
////  if(angle < 10) {
////    angle = 10;
////  } else if(angle > 170) {
////    angle = 170;
////  }
//
//  s1.write(angle);
//  digitalWrite(pin_led, !digitalRead(pin_led));
//  server.send(204, "received");
//}
