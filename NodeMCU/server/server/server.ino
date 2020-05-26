#include <Ticker.h>
#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <Servo.h>
#include <Wire.h>

#define MPU 0x68
#define A_R 16384.0 
#define G_R 131.0
#define RAD_A_DEG 57.295779

//Network Settings
ESP8266WebServer server;
char* ssid = "X Air";
char* password = "Jiang991022";

//LED Settings
bool flag;
//LED
uint8_t pin_led = 16;

// Gyro Settings
int16_t AcX, AcY, AcZ, GyX, GyY, GyZ;
float Acc[2];
float Gy[3];
float Angle[3];
String values;
long time_prevs;
float dt;

//Motor Settings
const uint8_t mA_speed = 5;
const uint8_t mB_speed = 4;
const uint8_t mA_dir = 0;
const uint8_t mB_dir = 2;

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
  Serial.begin(115200);
  
  // put your setup code here, to run once:
  pinMode(pin_led, OUTPUT);

  //setup motor pwm
  pinMode(mA_speed, OUTPUT);
  pinMode(mA_dir, OUTPUT);
  pinMode(mB_speed, OUTPUT);
  pinMode(mB_dir, OUTPUT);

  //setup encoders
  attachInterrupt(digitalPinToInterrupt(MA_INT), ISR_countA, RISING);
  attachInterrupt(digitalPinToInterrupt(MB_INT), ISR_countB, RISING);
  //ticker.attach_ms(300, ISR_ticker);

  //setup gyro
  Wire.begin(12, 14); // D2(GPIO4)=SDA / D1(GPIO5)=SCL
  //register MPU
  Wire.beginTransmission(MPU);
  Wire.write(0x6B);
  Wire.write(0);
  Wire.endTransmission(true);

  //setup servo
  //flag = true;
  //s1.attach(s1_port);
  //s1.write(10);
  
  WiFi.begin(ssid, password);
  while(WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }

  server.on("/led", toggleLED);
  server.on("/pattern1", pattern1);
  
  server.begin();
}

void toggleLED() {
  digitalWrite(pin_led, !digitalRead(pin_led));
  server.send(204, "");
}

void motor(int speedA, int speedB, int dirA, int dirB) { 
  digitalWrite(mA_speed, speedA);
  digitalWrite(mB_speed, speedB);
  digitalWrite(mA_dir, dirA);
  digitalWrite(mB_dir, dirB);
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

//encoder counter at interrupt
void ISR_countA() {
  count_A++;
}

void ISR_countB() {
  count_B++;
}

//void ISR_ticker() {
//  ticker.detach();
//  Serial.print("Motor A speed: ");
//  float speedA = (count_A / DISKSLOTS) * 180.0;
//  Serial.print(speedA);
//  Serial.print(" RPM - ");
//  count_A = 0;
//
//  Serial.print("Motor B speed: ");
//  float speedB = (count_B / DISKSLOTS) * 180.0;
//  Serial.print(speedB);
//  Serial.println(" RPM");
//  count_B = 0;
//  ticker.attach_ms(300, ISR_ticker);
//}

void updateGyro() {
  Wire.beginTransmission(MPU);
  Wire.write(0x3B);
  Wire.endTransmission(false);
  Wire.requestFrom(MPU, 6, true);
  AcX = Wire.read() << 8 | Wire.read();
  AcY = Wire.read() << 8 | Wire.read();
  AcZ = Wire.read() << 8 | Wire.read();

  Acc[0] = atan((AcY / A_R) / sqrt(pow((AcX / A_R), 2) + pow((AcZ / A_R), 2))) * RAD_TO_DEG;
  Acc[1] = atan(-1 * (AcX / A_R) / sqrt(pow((AcY / A_R), 2) + pow((AcZ / A_R), 2))) * RAD_TO_DEG;

  Acc[0] = Acc[0];
  Acc[1] = Acc[1];


  Wire.beginTransmission(MPU);
  Wire.write(0x43);
  Wire.endTransmission(false);
  Wire.requestFrom(MPU, 6, true);
  GyX = Wire.read() << 8 | Wire.read();
  GyY = Wire.read() << 8 | Wire.read();
  GyZ = Wire.read() << 8 | Wire.read();

  Gy[0] = GyX / G_R;
  Gy[1] = GyY / G_R;
  Gy[2] = GyZ / 30;

  Gy[0] = Gy[0];
  Gy[1] = Gy[1];
  Gy[2] = Gy[2] + 0.002;

  dt = (millis() - time_prevs) / 1000.0;
  time_prevs = millis();

  Angle[0] = 0.98 * (Angle[0] + Gy[0] * dt) + 0.02 * Acc[0];
  Angle[1] = 0.98 * (Angle[1] + Gy[1] * dt) + 0.02 * Acc[1];
  Angle[2] = Angle[2]+Gy[2]*dt;
  delay(10);
}

void pattern1() {
  int ec = 0;
  motor(450, 450, HIGH, HIGH);
  delay(200);
  motor(0, 0, HIGH, HIGH);

  motor(450, 450, HIGH, HIGH);
  delay(200);
  motor(0, 0, HIGH, HIGH);

  

  /*
  while(ec < 45) {
    ec = count_A;
  }
  ec = 0;
  motor(0, 0, HIGH, HIGH);

  motor(450, 450, HIGH, HIGH);
  while(ec < 65) {
    ec = count_A;
  }
  ec = 0;
  motor(0, 0, HIGH, HIGH);

  motor(450, 450, HIGH, HIGH);
  while(ec < 25) {
    ec = count_A;
  }
  ec = 0;
  motor(0, 0, HIGH, HIGH);
*/
  updateGyro();
  float angle = Angle[2];
  motor(80, 80, HIGH, LOW);
  while(abs(angle - Angle[2]) < 90) {
    updateGyro();
  }
  motor(0, 0, HIGH, HIGH);

  motor(450, 450, HIGH, HIGH);
  delay(100);
  motor(0, 0, HIGH, HIGH);

/*
  motor(450, 450, HIGH, HIGH);
  while(ec < 45) {
    ec = count_A;
  }
  ec = 0;
  motor(0, 0, HIGH, HIGH);

  motor(450, 450, HIGH, HIGH);
  while(ec < 65) {
    ec = count_A;
  }
  ec = 0;
  motor(0, 0, HIGH, HIGH);

  motor(450, 450, HIGH, HIGH);
  while(ec < 25) {
    ec = count_A;
  }
  ec = 0;
  motor(0, 0, HIGH, HIGH);  
  */
}

void loop() {
  // put your main code here, to run repeatedly:
  server.handleClient();
}
