/*
  Perangkat monitoring air terdari dari wemos, sensor ultrasonic, water flow sensor G1/2, dan relay.
  Perangkat monitoring air ini berfungsi untuk monitoring pengisian dan pemakaian air.

  Author : Yandi Fitriyanto
  Email : yandi.fitriyanto@gmail.com
*/
#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <ArduinoJson.h>

//inisialisasi pin relay
#define relayPin D1 //in pin

//inisialisasi pin sensor ultrasonic
#define echoPin D5 // Echo Pin
#define trigPin D6 // Trigger Pin

//inisialisasi pin sensor flow meter
byte sensorInterrupt = 0;  // 0 = digital pin 2
byte sensorPin       = D3;

//variable buat inisialisasi setup wifi
const String WEBSITE = "http://192.168.43.104";

const char* MY_SSID = "Rahasia";
const char* MY_PWD =  "yandi2503";

//variable buat sensor ultrasonic
long duration, distance, lastDistance, ketinggianAir; 
long ketinggianTank = 16;
long minimal, maksimal;

boolean pompaAir, lastPompaAir;
boolean statusPompaAir;// status pompa air get dari REST 

String jsonSettingKetinggian, jsonStatusPompaAir;

// variable buat sensor flowmeter
// The hall-effect flow sensor outputs approximately 4.5 pulses per second per
// litre/minute of flow.
float calibrationFactor = 1.5;

volatile byte pulseCount;  

float flowRate;
unsigned int flowMilliLitres;
long totalMilliLitres = 0u;
long lastTotalMilliLitres = 0u;

unsigned long oldTime;

WiFiClient client;  //Instantiate WiFi object
 
void setup()
{
  Serial.begin (38400);

  Serial.print("Connecting to "+*MY_SSID);
  WiFi.begin(MY_SSID, MY_PWD);
  Serial.println("going into wl connect");

  while (WiFi.status() != WL_CONNECTED) //not connected,..waiting to connect
  {
      delay(1000);
      Serial.print(".");
  }
    
  Serial.println("wl connected");
  Serial.println("");
  Serial.println("Credentials accepted! Connected to wifi\n ");
  Serial.println("");

  //inisialisasi pin sensor ultrasonik
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);

  //inisialisasi pin relay
  pinMode(relayPin, OUTPUT);
  digitalWrite(relayPin, 0);

  //inisialisasi pin sensor flowmeter
  pinMode(sensorPin, INPUT);
  digitalWrite(sensorPin, HIGH);

  pulseCount        = 0;
  flowRate          = 0.0;
  flowMilliLitres   = 0;
  totalMilliLitres  = 0;
  lastTotalMilliLitres  = 0;
  oldTime           = 0;

  // The Hall-effect sensor is connected to pin 2 which uses interrupt 0.
  // Configured to trigger on a FALLING state change (transition from HIGH
  // state to LOW state)
  attachInterrupt(sensorInterrupt, pulseCounter, FALLING);

  getSettinganKetinggian();
}
 
void loop()
{
  pemakaianAir();
  
  distanceData();
  ketinggianAir = ketinggianTank - distance;
  Serial.println("Ketinggian Air: " + String(ketinggianAir));
  if (distance != lastDistance && ketinggianAir >= 0) {
    lastDistance = distance;
    sendPengisianAir(String(ketinggianAir));
    simpanStatusPompaAir(ketinggianAir);    
  }

  getStatusPompa();
  
  delay(2000);
}

void distanceData()
{
  /* The following trigPin/echoPin cycle is used to determine the
  distance of the nearest object by bouncing soundwaves off of it. */
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);
  duration = pulseIn(echoPin, HIGH);
  //Calculate the distance (in cm) based on the speed of sound.
  distance = duration / 26 / 2;
  Serial.println("Ketinggian : " + String(distance));
}

/*----------------------------------------------------------
Sends pengisian air
Inputs: String, data berasal dari pembacaan ketinggian air
------------------------------------------------------------*/
void sendPengisianAir(String jumlah)
{
    Serial.println("Data string: " + jumlah);

    Serial.println("...Connecting to api monitoring pengisian air");
    
    HTTPClient http;

    http.begin(WEBSITE + "/api/simpan-pengisian-air/?jumlah=" + jumlah);

    int httpCode = http.GET();

    if (httpCode > 0) {
      String payload = http.getString();
      Serial.println(payload); 
    } else {
      Serial.printf("[HTTP] GET... failed, error: %s\n", http.errorToString(httpCode).c_str());
    } 

    http.end();
}

/*----------------------------------------------------------
GET setting ketinggian maksimal dan minimal air
------------------------------------------------------------*/
void getSettinganKetinggian()
{
  Serial.println("GET data settingan ketinggian air");

  StaticJsonBuffer<200> jsonBuffer;
  HTTPClient http;

    http.begin(WEBSITE + "/api/setting-ketinggian-air");

    int httpCode = http.GET();

    if (httpCode > 0) {
      jsonSettingKetinggian = http.getString();
      Serial.println(jsonSettingKetinggian); 
    } else {
      Serial.printf("[HTTP] GET... failed, error: %s\n", http.errorToString(httpCode).c_str());
    } 

    http.end();

    JsonObject& root = jsonBuffer.parseObject(jsonSettingKetinggian.c_str());
    
    // Test if parsing failed.
    if (!root.success()) {
      Serial.println("parseObject() failed");
      return;
    }

    minimal = root["minimal"];
    maksimal = root["maksimal"];
}

/*----------------------------------------------------------
Status Pompa Air
Input: long, data berasal dari pembacaan ketinggian air
------------------------------------------------------------*/
void simpanStatusPompaAir(long ketinggianAir)
{
  if (ketinggianAir >= maksimal) {
    pompaAir = 0;
  } else if (ketinggianAir <= minimal) {
    pompaAir = 1;
  }

  if (pompaAir != lastPompaAir) {
    //disini code untuk on/off relay
    digitalWrite(relayPin, pompaAir);
    
    lastPompaAir = pompaAir;
    sendStatusPompaAir(String(lastPompaAir));
  }
}

/*----------------------------------------------------------
send Status Pompa Air
Input: String, data berasal dari status pompa terakhir
------------------------------------------------------------*/
void sendStatusPompaAir(String statusPompa)
{
    Serial.println("Data string: " + statusPompa);

    Serial.println("...Connecting to api monitoring status pompa air");
    
    HTTPClient http;

    http.begin(WEBSITE + "/api/simpan-status-pompa-air/?status=" + statusPompa);

    int httpCode = http.GET();

    if (httpCode > 0) {
      String payload = http.getString();
      Serial.println(payload); 
    } else {
      Serial.printf("[HTTP] GET... failed, error: %s\n", http.errorToString(httpCode).c_str());
    } 

    http.end();
}

/*----------------------------------------------------------
GET status pompa air
------------------------------------------------------------*/
void getStatusPompa()
{
  Serial.println("GET data status pompa air");

  StaticJsonBuffer<200> jsonBuffer;
  HTTPClient http;

    http.begin(WEBSITE + "/api/status-pompa-air");

    int httpCode = http.GET();

    if (httpCode > 0) {
      jsonStatusPompaAir = http.getString();
      Serial.println(jsonStatusPompaAir); 
    } else {
      Serial.printf("[HTTP] GET... failed, error: %s\n", http.errorToString(httpCode).c_str());
    } 

    http.end();

    JsonObject& root = jsonBuffer.parseObject(jsonStatusPompaAir.c_str());
    
    // Test if parsing failed.
    if (!root.success()) {
      Serial.println("parseObject() failed");
      return;
    }

    statusPompaAir = root["status_pompa"];

    if (statusPompaAir != lastPompaAir) {
      pompaAir = statusPompaAir;
      lastPompaAir = statusPompaAir;
      
      //disini code untuk on/off relay
      digitalWrite(relayPin, pompaAir);
      
      Serial.println("status pompa air berasal control dari android :" + String(statusPompaAir));
    }
}

/*------------------------------------------------
Sends sensor pemakaian air
Inputs: String, data to be entered for each field
Returns: 
------------------------------------------------*/
void sendPemakaianAir(String jumlah)
{
    Serial.println("Data string: " + jumlah);

    Serial.println("...Connecting to api monitoring air");
    
    HTTPClient http;

    http.begin(WEBSITE + "/api/simpan-pemakaian-air/?jumlah=" + jumlah);

    int httpCode = http.GET();

    if (httpCode > 0) {
      String payload = http.getString();
      Serial.println(payload); 
    } else {
      Serial.printf("[HTTP] GET... failed, error: %s\n", http.errorToString(httpCode).c_str());
    } 

    http.end();
}

void pemakaianAir()
{
  if((millis() - oldTime) > 1000)    // Only process counters once per second
  { 
    // Disable the interrupt while calculating flow rate and sending the value to
    // the host
    detachInterrupt(sensorInterrupt);

    // Because this loop may not complete in exactly 1 second intervals we calculate
    // the number of milliseconds that have passed since the last execution and use
    // that to scale the output. We also apply the calibrationFactor to scale the output
    // based on the number of pulses per second per units of measure (litres/minute in
    // this case) coming from the sensor.
    flowRate = ((1000.0 / (millis() - oldTime)) * pulseCount) / calibrationFactor;
    
    // Note the time this processing pass was executed. Note that because we've
    // disabled interrupts the millis() function won't actually be incrementing right
    // at this point, but it will still return the value it was set to just before
    // interrupts went away.
    oldTime = millis();
    
    // Divide the flow rate in litres/minute by 60 to determine how many litres have
    // passed through the sensor in this 1 second interval, then multiply by 1000 to
    // convert to millilitres.
    flowMilliLitres = (flowRate / 60) * 1000;
    
    // Add the millilitres passed in this second to the cumulative total
    totalMilliLitres += flowMilliLitres;
      
    unsigned int frac;
    
    // Print the flow rate for this second in litres / minute
    Serial.print("Flow rate: ");
    Serial.print(int(pulseCount));  // Print the integer part of the variable
    Serial.print(".");             // Print the decimal point
    // Determine the fractional part. The 10 multiplier gives us 1 decimal place.
    frac = (flowRate - int(flowRate)) * 10;
    Serial.print(frac, DEC) ;      // Print the fractional part of the variable
    Serial.print("L/min");
    // Print the number of litres flowed in this second
    Serial.print("  Current Liquid Flowing: ");             // Output separator
    Serial.print(flowMilliLitres);
    Serial.print("mL/Sec");

    // Print the cumulative total of litres flowed since starting
    Serial.print("  Output Liquid Quantity: ");             // Output separator
    Serial.print(totalMilliLitres);
    Serial.println("mL\r\n"); 

    // Reset the pulse counter so we can start incrementing again
    pulseCount = 0;
    
    // Enable the interrupt again now that we've finished sending output
    attachInterrupt(sensorInterrupt, pulseCounter, FALLING);
  }

  if (int(flowMilliLitres) == 0) {
      if (totalMilliLitres) {
        sendPemakaianAir(String(totalMilliLitres));  
      }
      lastTotalMilliLitres = 0u;
      totalMilliLitres = 0u;   
  }
}

/*
Insterrupt Service Routine
 */
void pulseCounter()
{
  // Increment the pulse counter
  pulseCount++;
}
