#include <AFMotor.h>      //add Adafruit Motor Shield library
#include <Servo.h>        //add Servo Motor library            
#include <NewPing.h>      //add Ultrasonic sensor library
#include<SoftwareSerial.h>
#include<stdio.h>
#include<string.h>
#include<Wire.h>
#include <I2Cdev.h>
#include <HMC5883L.h>

SoftwareSerial esp(7,8);
SoftwareSerial GPSModule(12,13);

HMC5883L mag;
int16_t mx, my, mz;

int updates;
int failedUpdates;
int pos;
int stringplace = 0;
int n=0;
String timeUp;
String nmea[15];

#define TRIG_PIN A0 // Pin A0 on the Motor Drive Shield soldered to the ultrasonic sensor
#define ECHO_PIN A1 // Pin A1 on the Motor Drive Shield soldered to the ultrasonic sensor
#define MAX_DISTANCE 300 // sets maximum useable sensor measuring distance to 300cm
#define MAX_SPEED 162 // sets speed of DC traction motors to 150/250 or about 70% of full speed - to get power drain down.
#define MAX_SPEED_OFFSET 40 // this sets offset to allow for differences between the two DC traction motors
#define COLL_DIST 18 // sets distance at which robot stops and reverses to 30cm
#define TURN_DIST COLL_DIST+20 // sets distance at which robot veers away from object
NewPing sonar(TRIG_PIN, ECHO_PIN, MAX_DISTANCE); // sets up sensor library to use the correct pins to measure distance.

AF_DCMotor leftMotor1(1, MOTOR12_1KHZ); // create motor #1 using M1 output on Motor Drive Shield, set to 1kHz PWM frequency
AF_DCMotor leftMotor2(2, MOTOR12_1KHZ); // create motor #2, using M2 output, set to 1kHz PWM frequency
AF_DCMotor rightMotor1(3, MOTOR34_1KHZ);// create motor #3, using M3 output, set to 1kHz PWM frequency
AF_DCMotor rightMotor2(4, MOTOR34_1KHZ);// create motor #4, using M4 output, set to 1kHz PWM frequency
Servo myservo;  // create servo object to control a servo 

int leftDistance, rightDistance; //distances on either side
int curDist = 0;
String motorSet = "";
int speedSet = 0;
float bearing ;//angle to waypoint.
float heading;
int distance=0;//from destination in cms.
int i=20;
//-------------------------------------------- SETUP LOOP ----------------------------------------------------------------------------
void setup() {
  myservo.attach(9);  // attaches the servo on pin 10 (SERVO_1 on the Motor Drive Shield to the servo object 
  myservo.write(90); // tells the servo to position at 90-degrees ie. facing forward.
  delay(1000); // delay for one seconds
  Wire.begin(4);
  GPSModule.begin(4800);
  esp.begin(9600);
  mag.initialize();
  Serial.begin(9600);
 }
//------------------------------------------------------------------------------------------------------------------------------------

//---------------------------------------------MAIN LOOP ------------------------------------------------------------------------------
void loop() {
  
  myservo.write(90);  // move eyes forward
  delay(1000);
  curDist = readPing();// read distance
       if(i%20==0)
       {
       iot();
       checkHeading();
       }
        if (curDist < COLL_DIST) 
        {
          changePath();
          if(curDist>70)
          checkHeading();
        }
        else{ moveForward();}
        delay(500);
 }
//-------------------------------------------------------------------------------------------------------------------------------------

void changePath() {
  moveStop();   // stop forward movement
  myservo.write(36);  // check distance to the right
    delay(500);
    rightDistance = readPing(); //set right distance
    delay(500);
    myservo.write(144);  // check distace to the left
    delay(700);
    leftDistance = readPing(); //set left distance
    delay(500);
    myservo.write(90); //return to center
    delay(100);
    compareDistance();
  }

  
void compareDistance()   // find the longest distance
{
  if (leftDistance>rightDistance) //if left is less obstructed 
  {
    turnLeft();
  }
  else if (rightDistance>leftDistance) //if right is less obstructed
  {
    turnRight();
  }
   else //if they are equally obstructed
  {
    turnAround();
  }
}

int readPing() { // read the ultrasonic sensor distance
  delay(70);   
  unsigned int uS = sonar.ping();
  int cm = uS/US_ROUNDTRIP_CM;
  return cm;
}
void moveStop() {
  leftMotor1.run(RELEASE); 
  leftMotor2.run(RELEASE); 
  rightMotor1.run(RELEASE); 
  rightMotor2.run(RELEASE);
  }  // stop the motors.
void moveForward() {
    motorSet = "FORWARD";
    leftMotor1.run(FORWARD);      // turn it on going forward
    leftMotor2.run(FORWARD);      // turn it on going forward
    rightMotor1.run(FORWARD);     // turn it on going forward
    rightMotor2.run(FORWARD);     // turn it on going forward
  for (speedSet = 90; speedSet < MAX_SPEED; speedSet +=10) // slowly bring the speed up to avoid loading down the batteries too quickly
  {
    leftMotor1.setSpeed(speedSet);
    leftMotor2.setSpeed(speedSet);
    rightMotor1.setSpeed(speedSet); 
    rightMotor2.setSpeed(speedSet);
    delay(5);
  }
}
void moveBackward() {
    motorSet = "BACKWARD";
    leftMotor1.run(BACKWARD);     // turn it on going backward
    leftMotor2.run(BACKWARD);     // turn it on going backward
    rightMotor1.run(BACKWARD);    // turn it on going backward
    rightMotor2.run(BACKWARD);    // turn it on going backward
  for (speedSet = 90; speedSet < MAX_SPEED; speedSet +=10) 
  {
    leftMotor1.setSpeed(speedSet);
    leftMotor2.setSpeed(speedSet);
    rightMotor1.setSpeed(speedSet); 
    rightMotor2.setSpeed(speedSet); 
    delay(5);
  }
}  
void turnRight() {
  motorSet = "RIGHT";
  leftMotor1.run(FORWARD);      // turn motor 1 forward
  leftMotor2.run(FORWARD);// turn motor 2 forward
  leftMotor1.setSpeed(speedSet+MAX_SPEED_OFFSET);      
  leftMotor2.setSpeed(speedSet+MAX_SPEED_OFFSET);     
  rightMotor1.run(BACKWARD);    // turn motor 3 backward
  rightMotor2.run(BACKWARD);    // turn motor 4 backward
  rightMotor1.setSpeed(speedSet+MAX_SPEED_OFFSET);      
  rightMotor2.setSpeed(speedSet+MAX_SPEED_OFFSET);     
  delay(1500); // run motors this way for 1500        
  motorSet = "FORWARD";
  leftMotor1.run(FORWARD);      // set both motors back to forward
  leftMotor2.run(FORWARD);
  rightMotor1.run(FORWARD);
  rightMotor2.run(FORWARD);      
}  
void turnLeft() {
  motorSet = "LEFT";
  leftMotor1.run(BACKWARD);      // turn motor 1 backward
  leftMotor2.run(BACKWARD);      // turn motor 2 backward
  leftMotor1.setSpeed(speedSet+MAX_SPEED_OFFSET);     
  leftMotor2.setSpeed(speedSet+MAX_SPEED_OFFSET);    
  rightMotor1.run(FORWARD);     // turn motor 3 forward
  rightMotor2.run(FORWARD);     // turn motor 4 forward
  rightMotor1.setSpeed(speedSet+MAX_SPEED_OFFSET);      
  rightMotor2.setSpeed(speedSet+MAX_SPEED_OFFSET);     
  delay(1500); // run motors this way for 1500  
  motorSet = "FORWARD";
  leftMotor1.run(FORWARD);      // turn it on going forward
  leftMotor2.run(FORWARD);      // turn it on going forward
  rightMotor1.run(FORWARD);     // turn it on going forward
  rightMotor2.run(FORWARD);     // turn it on going forward
}  
void turnAround() {
  motorSet = "RIGHT";
  leftMotor1.run(FORWARD);      // turn motor 1 forward
  leftMotor2.run(FORWARD);      // turn motor 2 forward
  rightMotor1.setSpeed(speedSet+MAX_SPEED_OFFSET);      
  rightMotor2.setSpeed(speedSet+MAX_SPEED_OFFSET);     
  rightMotor1.run(BACKWARD);    // turn motor 3 backward
  rightMotor2.run(BACKWARD);    // turn motor 4 backward
  rightMotor1.setSpeed(speedSet+MAX_SPEED_OFFSET);      
  rightMotor2.setSpeed(speedSet+MAX_SPEED_OFFSET);
  delay(1700); // run motors this way for 1700
  motorSet = "FORWARD";
  leftMotor1.run(FORWARD);      // set both motors back to forward
  leftMotor2.run(FORWARD);
  rightMotor1.run(FORWARD);
  rightMotor2.run(FORWARD);      
} 
void iot()
{
    GPSModule.listen();
   
while(GPSModule.available()>0){
    GPSModule.read();}
    
if (GPSModule.find("$GPRMC,")) {
String tempMsg = GPSModule.readStringUntil('\n');
for (int i = 0; i < tempMsg.length(); i++) {
if (tempMsg.substring(i, i + 1) == ",") {
nmea[pos] = tempMsg.substring(stringplace, i);
stringplace = i + 1;
pos++;
}
if (i == tempMsg.length() - 1) {
nmea[pos] = tempMsg.substring(stringplace, i);
}
}
updates++;
delay(500);

nmea[2] = ConvertLat();
delay(500);
nmea[4] = ConvertLng();

}
else {

failedUpdates++;

}
stringplace = 0;
pos = 0;
    esp.listen();
    Serial.println(nmea[4]);
    esp.print(nmea[4]);
    delay(1000);

   Serial.println(nmea[2]);
    esp.print(nmea[2]);
    delay(1000);

    // read the input command in a string
    bearing=esp.readString().toFloat();
    Serial.print(bearing);
    delay(1000);
    distance = esp.readString().toInt();
    Serial.println(distance);
    delay(1000);  
}

    
String ConvertLat() {
String posneg = "";
if (nmea[3] == "S") {
posneg = "-";
}
String latfirst;
float latsecond;
for (int i = 0; i < nmea[2].length(); i++) {
if (nmea[2].substring(i, i + 1) == ".") {
latfirst = nmea[2].substring(0, i - 2);
latsecond = nmea[2].substring(i - 2).toFloat();
}
}
latsecond = latsecond / 60;
String CalcLat = "";

char charVal[9];
dtostrf(latsecond, 4, 6, charVal);
for (int i = 0; i < sizeof(charVal); i++)
{
CalcLat += charVal[i];
}
latfirst += CalcLat.substring(1);
latfirst = posneg += latfirst;
return latfirst;
}

String ConvertLng() {
String posneg = "";
if (nmea[5] == "W") {
posneg = "-";
}

String lngfirst;
float lngsecond;
for (int i = 0; i < nmea[4].length(); i++) {
if (nmea[4].substring(i, i + 1) == ".") {
lngfirst = nmea[4].substring(0, i - 2);
//Serial.println(lngfirst);
lngsecond = nmea[4].substring(i - 2).toFloat();
//delay(500);
//Serial.println(lngsecond);

}
}
lngsecond = lngsecond / 60;
String CalcLng = "";
char charVal[9];
dtostrf(lngsecond, 4, 6, charVal);
for (int i = 0; i < sizeof(charVal); i++)
{
CalcLng += charVal[i];
}
lngfirst += CalcLng.substring(1);
lngfirst = posneg += lngfirst;
return lngfirst;
}

void checkHeading()
{ 
  if(distance<10000)
  {while(i>20)
    moveStop();  
  }
  else
  {
   checkCompass();
   if((heading==bearing)||(heading-bearing<=9)||(bearing-heading<=9))
   {moveForward();}
   else if(heading-bearing>9)
   {
    while(heading-bearing<10)
   {
    motorSet = "LEFT";
    leftMotor1.run(BACKWARD);      // turn motor 1 backward
    leftMotor2.run(BACKWARD);      // turn motor 2 backward
    leftMotor1.setSpeed(speedSet+MAX_SPEED_OFFSET);     
    leftMotor2.setSpeed(speedSet+MAX_SPEED_OFFSET);    
    rightMotor1.run(FORWARD);     // turn motor 3 forward
    rightMotor2.run(FORWARD);     // turn motor 4 forward
    rightMotor1.setSpeed(speedSet+MAX_SPEED_OFFSET);      
    rightMotor2.setSpeed(speedSet+MAX_SPEED_OFFSET);     
 }
 }
 else if(bearing-heading>9)
 {
  while(bearing-heading<10)
  {
  motorSet = "RIGHT";
  leftMotor1.run(FORWARD);      // turn motor 1 forward
  leftMotor2.run(FORWARD);// turn motor 2 forward
  leftMotor1.setSpeed(speedSet+MAX_SPEED_OFFSET);      
  leftMotor2.setSpeed(speedSet+MAX_SPEED_OFFSET);     
  rightMotor1.run(BACKWARD);    // turn motor 3 backward
  rightMotor2.run(BACKWARD);    // turn motor 4 backward
  rightMotor1.setSpeed(speedSet+MAX_SPEED_OFFSET);      
  rightMotor2.setSpeed(speedSet+MAX_SPEED_OFFSET);     
 }
 }
}
}
void checkCompass();
{
  // read raw heading measurements from device
    mag.getHeading(&mx, &my, &mz);  
  // To calculate heading in degrees. 0 degree indicates North
    heading = atan2(my, mx);
    if(heading < 0)
      heading += 2 * M_PI;
    Serial.print("heading:\t");
    heading=heading * 180/M_PI;
    float declinationAngle =-13.2;
    heading += declinationAngle;
   // correct as needed to guarantee that result is between 0 and 360
    if (heading < 0) heading += 360;
    if (heading > 360 ) heading -= 360;
    delay(500);
}
