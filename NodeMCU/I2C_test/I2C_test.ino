#include <Wire.h>

#define MPU 0x68

#define A_R 16384.0 // 32768/2
#define G_R 131.0 // 32768/250

#define RAD_A_DEG 57.295779

int16_t AcX, AcY, AcZ, GyX, GyY, GyZ;

float Acc[2];
float Gy[3];
float Angle[3];

String values;

long time_prevs;
float dt;

void setup()
{
  Serial.begin(115200);
  Wire.begin(12, 14); // D2(GPIO4)=SDA / D1(GPIO5)=SCL
  //register MPU
  Wire.beginTransmission(MPU);
  Wire.write(0x6B);
  Wire.write(0);
  Wire.endTransmission(true);
}

void loop()
{

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

  values = String(Angle[0]) + "," + String(Angle[1]) + "," + String(Angle[2]);


  Serial.println(values);

  delay(100);
}
