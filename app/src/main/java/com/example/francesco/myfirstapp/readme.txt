test su picker dell'ora: OK
    TestPickerHourActivity.java
    picker_main_hour.xml


test sul picker del calendario: OK
    TestPickerDateActivity.java
    picker_main_date.xml

test su bottone sendmessage (vecchio) OK
    DisplayMessageActivity (fa il get dell'intent)



____ TODO

da chiedere

dati ambientali -> richieste particolari?



1) salvataggio dati
mettere inserimento in memoria con timestamp ordinato??
gestire i duplicati -> (quando nello stesso sensore ins. dati con timestamp già presente, non devo aggiungerlo!! )


2) TimeReadActivity.java:
mettere il bottone "now" accanto al picker che setta l'ora e la data a oggi

Implementare i picker con i FRAGMENT e non con i Dialog
far si che un settaggio erato det picker colori di rosso i bottoni (es from >= to)

controllo sul ris:
se il num ricevuto è più piccolo di quello che pensavo, aggiusta il fattore di conversione
es non visualizzare 0.001kw ma 1049 W per essere più preciso


3) GraphActivity -> ordinate: tempo? come faccio?

--LineChart
sistem. asse x (data e non troppo sovrapposta)
L?ORARIO CHE MI DA E' SBAGLIATO!!!                                          <<------------


--PieChart
idea click fetta
rappresentaz consumi in perc sul totale


