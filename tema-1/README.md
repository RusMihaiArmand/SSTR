# Tema 1

Instructiuni:

- pentru a urmari mesajele se foloseste MQTTX sau alternative, dand subscribe de tipul QoS 1 pe
  topicurile folosite de aplicatie, mai precis:
  > topic_T2, topic_T3, topic_T4, topic_T5, topic_T6, topic_T7, topic_T8 pentru intrarile in
  tranzitii
  > topic_P1, topic_P2, topic_P3, topic_P46, topic_P5, topic_P7, topic_P8 pentru intrarile in
  pozitii (P4 si P6 au intrare comuna, deci si canalul de intrare e unul comun)
- tranzitia T1 din desen indica activarea sistemului de catre utilizator, ea neexistand ca si obiect
  in cod
- dupa rularea programului se asteapta conectarea tuturor pozitiilor si tranzitiilor, conectarea lui
  T8 indicand finalul initializarii, acum putand fi date instructiuni prin MQTTX
- pentru a incepe se transmite pe topic_P1 o instructiune de tipul "Execute" cu cel putin 1 token, aceasta
  urmand a porni sistemul, desfasurarea putand fi observata in terminal sau prin mesajele aratate in
  MQTTX

Lista completa de instructiuni:

{
"msg": "Execute",
"tokens": "1"
}
> va adauga 1 token in pozitie si o va pune sa trimita mai departe mesajul de executie; partea de
> token poate lipsi, in acest caz pozitia va activa tranzitia urmatoare doar daca are deja suficienti
> tokeni; acelasi mesaj poate fi dat si spre tranzitii pentru activare, partea de token nefiind
> necesara aici; numarul de tokeni din mesaj poate fi modificat


{
"msg": "Ping"
}
> indica in terminal pozitiile si tranzitiile ce asculta pe canalul respectiv;
> in cazul pozitiilor va indica si numarul de tokeni


{
"msg": "Clear"
}
> curata tokenii din pozitie