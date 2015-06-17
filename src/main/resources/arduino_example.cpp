void setup() {
  // initialize digital pin 13 as an output.
  pinMode(13, OUTPUT);
  // Initialize serial port
  Serial.begin(9600);
}
void loop() {
  String content = "";
  char character;
  while(Serial.available()) {
    character = Serial.read();
    content.concat(character); delay (10);
  }
  if (content != "") {
    if (content == "HIGH") {
      digitalWrite(13, HIGH);
    } else {
      digitalWrite(13, LOW);
    }
    Serial.println(content);
  }
}