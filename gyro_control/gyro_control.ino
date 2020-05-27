#include <Wire.h>
#define MPU 0x68

//LED Settings
bool flag;
uint8_t pin_led = 16;

// Gyro Settings
float Angle_prevs;
int16_t GyZ;
float Gy;
float Angle;
long time_prevs;
float dt;

//Motor Settings
const uint8_t mA_speed = 5;
const uint8_t mB_speed = 4;
const uint8_t mA_dir = 0;
const uint8_t mB_dir = 2;

//Setup
void setup() {
  Serial.begin(115200);

  //setup led
  pinMode(pin_led, OUTPUT);

  //setup motor pwm
  pinMode(mA_speed, OUTPUT);
  pinMode(mA_dir, OUTPUT);
  pinMode(mB_speed, OUTPUT);
  pinMode(mB_dir, OUTPUT);

  //setup gyro
  Wire.begin(12, 14);
  Wire.beginTransmission(MPU);
  Wire.write(0x6B);
  Wire.write(0);
  Wire.endTransmission(true);
}

void motor(int speedA, int speedB, int dirA, int dirB) { 
  digitalWrite(mA_speed, speedA);
  digitalWrite(mB_speed, speedB);
  digitalWrite(mA_dir, dirA);
  digitalWrite(mB_dir, dirB);
}

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

void turn(int dA) {
  Angle_prevs = Angle;
  while(abs(Angle_prevs-Angle) < dA) {
    updateGyro();
    Serial.print(Angle_prevs);
    Serial.print(" | ");
    Serial.println(Angle);
    yield();
  }
  digitalWrite(pin_led, !digitalRead(pin_led));
}

void turn() {
  turn(90)
  //Serial.println(Angle);
}
