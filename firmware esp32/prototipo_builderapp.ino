#include "BluetoothSerial.h" //Header File for Serial Bluetooth, will be added by default into Arduino
#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif

BluetoothSerial ESP_BT; //Object for Bluetooth

String incoming;
String date_ini = "";
String error = "1111#E";
String date_ini_guardado = "2222#G";
String date_fin = ""; 
int energia = 0;
int initiated = 0;
String senergia;
String nombre = "BuilderMiner#0002";

void setup() {
Serial.begin(112500); //Start Serial monitor in 9600
ESP_BT.begin(nombre); //Entre las comillas incluir tu numero de matricula
Serial.println("Bluetooth Device is Ready to Pair");

}

void writeString(String stringData) { // Used to serially push out a String with Serial.write()
//Serial.println("Se envio el String de largo: "+stringData.length());
  for (int i = 0; i < stringData.length(); i++)
  {
    ESP_BT.write(stringData[i]);   // Push each char 1 by 1 on each loop pass
    //Serial.print(stringData[i]);
  }
}// end writeString

void loop() {

if (ESP_BT.available()) //Check if we receive anything from Bluetooth
{
incoming = ESP_BT.readString(); //Read what we recevive
Serial.print("Received:"); Serial.println(incoming);

if (incoming.charAt(0) == '#')
{
  if(date_ini == ""){
    date_ini = incoming;
    Serial.println("Timestamp_i: "+date_ini+" // Timestamp_f: "+date_fin+" // Energía: "+energia);
    energia = 973405667;
    senergia = "!"+String(energia)+date_ini;
    ESP_BT.print(senergia);
    Serial.println("recibo limpio A la ESP: "+senergia);

  }
  else{
    ESP_BT.print(error);
    Serial.println("Envio error a la ESP: "+error);
  }
}

if (incoming == "*")
  {
  senergia = "*"+String(energia)+date_ini;
  ESP_BT.print(senergia);
  Serial.println("Enviado datos guardados:" +senergia);
  }
  
if (incoming == "$")
  {
      if(date_ini ==""){
        senergia = "=";
        //Si no hay nada devuelve un =
        ESP_BT.print(senergia);
        Serial.println("No había nada se envía un :"+senergia);
    }
      else{
        senergia = "%";
          //Si hay algo devuelve un %
          ESP_BT.print(senergia);
        Serial.println("Si había algo se envío dato para seguir :"+senergia);
      }
  }

if (incoming.charAt(0) == '|'){
  date_ini = "";
  date_fin = "";
  energia = 0;
  Serial.println("Borrando datos para nueva transaccion");
}


if (incoming.charAt(0) == '/' || incoming.charAt(1)=='/')
  {
  date_fin = incoming;
  senergia = String(energia)+date_ini;
  Serial.println("Enviado: " +senergia);
  ESP_BT.print(senergia);
  Serial.println("2 Enviado: " +senergia);
  date_ini = "";
  date_fin = "";
  energia = 0;
  }
}
delay(20);
}
