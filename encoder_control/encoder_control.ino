#include <Wire.h>

//LED Settings
uint8_t pin_led = 16;

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
}

//encoder counter at interrupt
void ISR_countA() {
  count_A++;
}
void ISR_countB() {
  count_B++;
}

void motor(int speedA, int speedB, int dirA, int dirB) { 
  digitalWrite(mA_speed, speedA);
  digitalWrite(mB_speed, speedB);
  digitalWrite(mA_dir, dirA);
  digitalWrite(mB_dir, dirB);
}

void move(int dist, int speedA, int speedB, int dirA, int dirB) {
  //reset encoder
  count_A = 0;
  count_B = 0;

  //set motor speed
  motor(speedA, speedB, dirA, dirB);
  while(count_A < dist && count_B < dist) {
    Serial.print(count_A);
    Serial.print(" | ");
    Serial.println(count_B);
    yield();
  }

  //stop
  motor(0, 0, dirA, dirB);
}

void loop() {
  move(100, 400, 400, HIGH, HIGH);
//  Serial.print(count_A);
//  Serial.print(" | ");
//  Serial.println(count_B);
  delay(500);
}
