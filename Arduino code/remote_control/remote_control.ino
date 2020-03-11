#include <SPI.h>
#include <Pixy.h>
#include <math.h>

/**Video Control Var**/
Pixy pixy;
//route
const int xs[] = {150, 150};
const int ys[] = {85,  200};
const int np = sizeof(xs) / sizeof(xs[0]);
//curr pos, direction in degrees 0-360
int x = 0;
int y = 0;
int dir;
int dir0;
int dir1;
//how many tracking points has the car gone through
int num = 0;


/**Control Board Var**/
const int F_MOVE = 2;
const int N_MOVE = 3;
const int B_MOVE = 4;
const int L_TURN = 8;
const int N_TURN = 9;
const int R_TURN = 10;
const int H = 255;
const int L = 0;

const int TDEG50 = 50;

int serial_sig = 0;

/**Global Control Var**/
bool begin = false;
int cycle = 0;

void stopMove() {
  analogWrite(L_TURN, L);
  analogWrite(N_TURN, H);
  analogWrite(R_TURN, L);

  analogWrite(F_MOVE, L);
  analogWrite(N_MOVE, H);
  analogWrite(B_MOVE, L);
}

void lTurn() {
  analogWrite(L_TURN, H);
  analogWrite(N_TURN, L);
  analogWrite(R_TURN, L);
}

void rTurn(int t) {
  analogWrite(L_TURN, L);
  analogWrite(N_TURN, L);
  analogWrite(R_TURN, H);

  delay(t);
  stopMove();
}

void fMove() {
  analogWrite(F_MOVE, H);
  analogWrite(N_MOVE, L);
  analogWrite(B_MOVE, L);
}

void bMove() {
  analogWrite(F_MOVE, L);
  analogWrite(N_MOVE, L);
  analogWrite(B_MOVE, H);
}

//returns curr direction of the car, assuming first block coordinate is the S1's coord
int getDirection(int diffx, int diffy) {
  if (diffx == 0 && diffy == 0) {
    Serial.print("Wrong angle\n");
  }

  double angle = 0.0;
  if (diffx == 0 && diffy > 0) {
    angle = 90;
  }
  if (diffx == 0 && diffy < 0) {
    angle = 270;
  }
  if (diffx < 0 && diffy == 0) {
    angle = 0;
  }
  if (diffx > 0 && diffy == 0) {
    angle = 180;
  }

  if (!(diffx == 0 || diffy == 0)) {
    double dx = (double)(abs(diffx));
    double dy = (double)(abs(diffy));
    angle = atan(dx / dy) * 180 / 3.14;
    angle = angle == 0 ? 1 : angle;

    if (diffx < 0 && diffy > 0) {
      angle = 90 - angle;
    }
    if (diffx > 0 && diffy > 0) {
      angle = 90 + angle;
    }
    if (diffx > 0 && diffy < 0) {
      angle = 270 - angle;
    }
    if (diffx < 0 && diffy < 0) {
      angle = 270 + angle;
    }
  }

  return ((int)angle);
}

void setup() {
  // initialize serial communications at 9600 bps:
  Serial.begin(9600);

  //reset car remote with stop
  stopMove();

  pixy.init();
  delay(50);
}

void loop() {

  while (!begin) {
    //Serial.println("Initializing Controller: Turn off then on");
    //stopMove();
    if (Serial.available() > 0) {
      serial_sig = Serial.read();
      if (serial_sig == 's') {
        begin = true;
      }
    }
    return;
  }

   rTurn(50); 
   delay(1000);
  /*
  int target = 90;
  bool captured = false;
  while (!captured) {
    uint16_t nBlocks = pixy.getBlocks();
    if (nBlocks == 2 && (pixy.blocks[0].signature == 1 && pixy.blocks[1].signature == 2)) {
      //confirmed recognization on car
      dir0 = getDirection(pixy.blocks[0].x -  pixy.blocks[1].x, pixy.blocks[0].y - pixy.blocks[1].y);
      captured = true;
    }
  }

  dir1 = dir0;
  while (dir0 - dir1 < target) {
    int dt = (target - (dir0 - dir1)) / 50.0 * TDEG50;
    Serial.println(dt);
    rTurn(dt); 
    delay(1000);
    
    //continue turning
    captured = false;
    while (!captured) {
      uint16_t nBlocks = pixy.getBlocks();
      if (nBlocks == 2 && (pixy.blocks[0].signature == 1 && pixy.blocks[1].signature == 2)) {
        //confirmed recognization on car
        dir1 = getDirection(pixy.blocks[0].x -  pixy.blocks[1].x, pixy.blocks[0].y - pixy.blocks[1].y);
        captured = true;
      }
    }

    Serial.print("Dir0: ");  
    Serial.print(dir0);
    Serial.print(" | Dir1: ");  
    Serial.print(dir1);
    Serial.print(" | diff: ");
    Serial.println(dir0 - dir1);
    Serial.println();
  }

  stopMove();
*/
  Serial.println("Stopped");  
  begin = false;



  /*
  if (true) {
    //pixy get pos, dir
    uint16_t nBlocks = pixy.getBlocks();
    if (nBlocks == 2 && (pixy.blocks[0].signature == 1 && pixy.blocks[1].signature == 2)) {
      //confirmed recognization on car
      x = (pixy.blocks[0].x + pixy.blocks[1].x) / 2;
      y = (pixy.blocks[0].y + pixy.blocks[1].y) / 2;
      dir = getDirection(pixy.blocks[0].x -  pixy.blocks[1].x, pixy.blocks[0].y - pixy.blocks[1].y);
      Serial.print("Find the car! Dir = ");
      Serial.println(dir);
    }
  }
  */
}
