void setup() {
  pinMode(2, OUTPUT);
  pinMode(3, OUTPUT);

  Serial.begin(9600);
  
  digitalWrite(2, HIGH);
  digitalWrite(3, LOW);
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
      digitalWrite(2, LOW);
      digitalWrite(3, HIGH);
    } else {
      digitalWrite(2, HIGH);
      digitalWrite(3, LOW);
    }
    Serial.println(content);
  } 
}