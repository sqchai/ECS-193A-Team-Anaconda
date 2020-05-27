#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <Wire.h>
#define MPU 0x68

//Network
ESP8266WebServer server;
char* ssid = "X Air";
char* password = "Jiang991022";

//LED
uint8_t pin_led = 16;

// Gyro
float Angle_prevs;
float Angle;
int16_t GyZ;
float Gy;
long time_prevs;
float dt;

//Encoders
const uint8_t MA_INT = D7;
const uint8_t MB_INT = D8;
const float DISKSLOTS = 20.00;
const float CIRC = 20.77;
volatile unsigned int count_A = 0;
volatile unsigned int count_B = 0;

//Motor
const uint8_t mA_speed = 5;
const uint8_t mB_speed = 4;
const uint8_t mA_dir = 0;
const uint8_t mB_dir = 2;

//reserved control parameters
int myDirection = 1;
int myAngle = 0;
int myDistance = 0;

void setup() {
  Serial.begin(115200);

  //LED
  pinMode(pin_led, OUTPUT);

  //Gyro
  Wire.begin(12, 14);
  Wire.beginTransmission(MPU);
  Wire.write(0x6B);
  Wire.write(0);
  Wire.endTransmission(true);

  //Encoder
  attachInterrupt(digitalPinToInterrupt(MA_INT), ISR_countA, RISING);
  attachInterrupt(digitalPinToInterrupt(MB_INT), ISR_countB, RISING);

  //Motor
  pinMode(mA_speed, OUTPUT);
  pinMode(mA_dir, OUTPUT);
  pinMode(mB_speed, OUTPUT);
  pinMode(mB_dir, OUTPUT);

  //Network
  WiFi.begin(ssid, password);
  while(WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  server.on("/confirm", respondConfirm);
  server.on("/control", respondControl);
  server.begin();
}

//Motor function
void motor(int speedA, int speedB, int dirA, int dirB) { 
  digitalWrite(mA_speed, speedA);
  digitalWrite(mB_speed, speedB);
  digitalWrite(mA_dir, dirA);
  digitalWrite(mB_dir, dirB);
}

//Gyro update
void updateGyro() {
  Wire.beginTransmission(MPU);
  Wire.write(0x43);
  Wire.endTransmission(false);
  Wire.requestFrom(MPU, 6, true);
  Wire.read() << 8 | Wire.read();
  Wire.read() << 8 | Wire.read();
  GyZ = Wire.read() << 8 | Wire.read();
  Gy = GyZ / 125;

  dt = (millis() - time_prevs) / 1000.0;
  time_prevs = millis();
  Angle = Angle+Gy*dt;
  delay(10);
}

//Gyro controlled Turning
void turn(int dir, int dA) {
  Angle_prevs = Angle;  
  if(dir==1) {
    //LEFT
    motor(100,100,HIGH,LOW);
  } else {
    motor(100,100,LOW,HIGH);
  } 
  dA = (int)(dA/1.5);
  while(abs(Angle_prevs-Angle) < dA) {
    updateGyro();    
    yield();
  }

  //counter turn to stop
  if(dir==1) {
    //COUNTER RIGHT
    motor(250,250,LOW,HIGH);
  } else {
    motor(250,250,HIGH,LOW);
  } 
  delay(100);
  yield();
  
  motor(0,0,HIGH, HIGH);
  delay(300);
  yield();
}

//Encoder update
void ISR_countA() {
  count_A++;
}
void ISR_countB() {
  count_B++;
}

//Encoder controlled move
void forward(int dist) {
  //dist is in cm, thus parse it into encoder counts
  int ct = (int)((float)dist / CIRC * DISKSLOTS);
  //reset encoder
  count_A = 0;
  count_B = 0;
  //set motor speed
  motor(400,400,HIGH,HIGH);
  while(count_A < ct && count_B < ct) {
    yield();
  }
  motor(0,0,HIGH,HIGH);
  delay(300);
  yield();
}

//respond to confirm request
void respondConfirm() {
  server.send(200, "text/plain", "confirmed");
}

//respond to control request
void respondControl() {
  if(server.arg("dir")) {
    myDirection = server.arg("dir").toInt();
  }
  if(server.arg("angle")) {
    myAngle = server.arg("angle").toInt();
  }
  if(server.arg("dist")) {
    myDistance = server.arg("dist").toInt();
  }

//  Serial.print(myDirection);
//  Serial.print(" | ");
//  Serial.print(myAngle);
//  Serial.print(" | ");
//  Serial.println(myDistance);

  //first turn to target direction
  turn(myDirection, myAngle);
  forward(myDistance);  
  
  server.send(200, "text/plain", "ok");
}

void loop() {
  server.handleClient();
}
