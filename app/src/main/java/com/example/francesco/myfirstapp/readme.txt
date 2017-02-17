test su picker dell'ora: OK
    TestPickerHourActivity.java
    picker_main_hour.xml


test sul picker del calendario: OK
    TestPickerDateActivity.java
    picker_main_date.xml

test su bottone sendmessage (vecchio) OK
    DisplayMessageActivity (fa il get dell'intent)



____ TODO

DA QUI
mi da errore sulla risposta response (come se fosse vuota):
provare a fare tutto da dentro il listsner del ParseUrl (come in abstractreading) facebdo partire da li dei metodi
e senza fargli ritornare roba o agg la response all'hashmap

provare a salvare da dentro volley solo i 4 valori, e poi capire se
1) se salvo e basta, poi ESCE da volley? me li ritrovo nella var globale? se si, metto in coda una funzione che li tira fuori e fa i vari if
2) se non esce da volley (??) come faccio a fare i controlli da li dentro? faccio due volley! devo fare il controllo solo alla seconda interrogazione
(es: facendo prima light e poi power, posso mettere IF(power) -> controlla (tutto dentro il corpo di volley)


!) service (ActivityDisplayAlarm):

1 - quando sono su DisplayAlarmManager, se clicco indietro che succede? e i dati li perdo?
2 - la get da service ha le url fisse, quindi conviene non fare l'ambaradan dello store result ???
 =======> DA PENSARE





2) TimeReadActivity.java:
mettere il bottone "now" accanto al picker che setta l'ora e la data a oggi

Implementare i picker con i FRAGMENT e non con i Dialog
far si che un settaggio erato det picker colori di rosso i bottoni (es from >= to)


3) GraphActivity -> ordinate: tempo? come faccio?

--LineChart
sistem. asse x (data e non troppo sovrapposta)
L?ORARIO CHE MI DA E' SBAGLIATO!!!                                          <<------------


--PieChart
idea click fetta
rappresentaz consumi in perc sul totale


