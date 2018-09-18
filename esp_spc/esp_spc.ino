#include <Firebase.h>
#include <FirebaseArduino.h>
#include <FirebaseCloudMessaging.h>
#include <FirebaseError.h>
#include <FirebaseHttpClient.h>
#include <FirebaseObject.h>
#include <String.h>
#include <ESP8266WiFi.h>



// Set these to run example.

#define FIREBASE_HOST "http******.firebaseio.com"

#define FIREBASE_AUTH "**************************************"

#define WIFI_SSID "UD"

#define WIFI_PASSWORD "********"

#define LED 1

void setup() {

pinMode(LED,OUTPUT);

digitalWrite(LED,0);

Serial.begin(9600);

WiFi.begin(WIFI_SSID, WIFI_PASSWORD);

Serial.print("connecting");

if(WiFi.status() != WL_CONNECTED) {

Serial.print(".");

delay(500);

}

Serial.println();

Serial.print("connected: ");

Serial.println(WiFi.localIP());

Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);




}

void loop() {
if (Serial.available() > 0){

 Firebase.setString("Car/Longitude",Serial.readString());
Serial.print("ok ");
delay(1000);
if (Serial.available() > 0){

 Firebase.setString("Car/Lattitude",Serial.readString());
Serial.print("ok");
  }
  }
}
