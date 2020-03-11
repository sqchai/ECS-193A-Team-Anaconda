//Serial Test Program
//Edited by Siqi Chai on Mar.9.2020
//Use together with Python program serial_test.py
//type in python console, arduino will send back the same thing.

void setup() {
  Serial.begin(9600);
}

void loop() {
  String str;
  
  while (!Serial.available()) {
    //nothing sent, wait here
  }

  while (Serial.available()) {
    //start reading buffer by 1 char each time, then concatenate together
    delay(30);
    if (Serial.available() > 0) {
      char c = Serial.read();
      str += c;
    }
  }

  if(str.length() > 0) {
    Serial.println(str);
  }

  delay(500);
  Serial.flush();
}
