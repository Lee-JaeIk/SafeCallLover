#include <SoftwareSerial.h>
#include <LiquidCrystal.h>

SoftwareSerial BTSerial(10, 11); // SoftwareSerial(TX, RX)


String myString = "";

void setup()
{

Serial.begin(9600);


BTSerial.begin(9600);
}

void loop()
{
    while(BTSerial.available())
    {
        char myChar = (char)BTSerial.read();
        myString += myChar;
        delay(5);
    }
    if(!myString.equals(""))
    {
        Serial.println("input value : " + myString);
        // lcd.clear();
        int len = myString.length();
    }
}
