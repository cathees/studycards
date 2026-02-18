# **SWJ3-Übungen - WS2025 - Übung 2 - Ausarbeitung**

## **Ausbaustufe 1: FLASH-UI**

### **Lösungsidee**
Die Anwendung lässt sich sinnvoll in zwei klar getrennte Bereiche gliedern: den Quiz-Client für Benutzer und die Lernkartenverwaltung für Administratoren. Beide Systeme besitzen eigene Login- und Registrierungsszenen, folgen jedoch derselben grundlegenden UI-Struktur, sodass sich ein konsistentes Erscheinungsbild ergibt. Die Navigationslogik wird zentral über einen SceneRouter gesteuert, der beim Login automatisch in den passenden Bereich weiterleitet und bei einem Logout verlässlich zur Startseite zurückkehrt.

Für die Lernkartenverwaltung wird eine dedizierte Admin-Authentifizierung vorausgesetzt, die den Zugriff auf die Kartenerstellung, Filterung, Statistikübersicht und Bearbeitungsfunktionen einschränkt. Lernkarten werden vorerst im Speicher verwaltet; später kann dieser Bereich durch eine Datenbank ersetzt werden, ohne dass sich die UI-Struktur ändern muss. Auch die Quiz-Fragen stammen momentan aus einer einfachen Repository-Klasse, werden aber bereits so genutzt, als kämen sie aus einem echten Back-End, da sie über zentrale Modelle (Quiz, QuizQuestion) abstrahiert werden.

Der gesamte Quiz-Flow besteht aus einer Frage-Szene, die den Fortschritt dynamisch aktualisiert, und einer abschließenden Auswertungsszene, die Korrektheit und Antworten des Nutzers zusammenfasst. Die Quiz-Szene greift dabei nicht direkt auf UI-Code zurück, sondern stützt sich ausschließlich auf das Quiz-Modell, wodurch die Logik klar vom Layout getrennt bleibt.

Die Login- und Registrierungsoberflächen orientieren sich am gleichen strukturellen Aufbau: Ein Back-Link führt zur Startseite, alle Interaktionen sind in einer zentral ausgerichteten Card platziert, und sowohl Eingabevalidierung als auch Navigation erfolgen über einfache Callback-Mechanismen. Die Designstruktur erlaubt es, zukünftige Authentifizierungs- und Persistenzmechanismen einzubauen, ohne die grundsätzliche Szenenlogik verändern zu müssen.

Insgesamt ergibt sich eine robuste, modular aufgebaute Projektarchitektur, bei der UI, Logik und Navigation sauber getrennt sind und die später problemlos um Datenbank-Schichten, Rollenmanagement oder komplexere Quiz- und Lernkartenfunktionen erweitert werden kann.
## **Testfälle**

Hier werde ich Screenshots von der UI anfügen und über den Stand der Funktionalität erläutern.

### **Mockup**
Das Mockup wurde mit Hilfe von Figma und Figma make erstellt.

![mockup1.png](doc/screenshots/mockup1.png)
![mockup2.png](doc/screenshots/mockup2.png)
![mockup3.png](doc/screenshots/mockup3.png)
![mockup4.png](doc/screenshots/mockup4.png)
![mockup5.png](doc/screenshots/mockup5.png)
![mockup6.png](doc/screenshots/mockup6.png)

### **Screenshots**

#### **Startseite**
Eine einfache Startseite die dem User die Möglichkeit gibt die Lernkarten zu verwalten oder im Quizclient Quizzes zu absolvieren.

![home.png](doc/screenshots/home.png)

#### **Lernkartenveerwaltung login**
Das ist der Loginscreen für die Lernkartenverwaltung. Derzeit muss man sich kein Account registrieren, also es gibt
die Möglichkeit einfach anzumelden. Die Authentifizierung werde ich dann bei der nächsten HÜ machen.

![lernkartenverwaltung.png](doc/screenshots/lernkartenverwaltung.png)

##
Die UI für die Registrierung existiert auch schon, nur derzeit werden keine User verwaltet, weil ich
es auch dann bei der HÜ mit der Datenbankanbindung realisieren werde. Es gibt zwar Klassen für Session und PasswörterUtils
aber diese sind noch nicht fertig implementiert, da es sich dabei nicht mehr wirklich um UI handelt,
was in dieser HÜ im Mittelpunkt steht.
Diese HÜ ist eh schon aufwendig genug.

![lernkartenverwaltungregistrierung.png](doc/screenshots/lernkartenverwaltungregistrierung.png)

#### **Lernkartenveerwaltung**
Hier kann man die verschiedenen Lernkarten verwalten. Unterstützt wird auch die Erstellung neuer Karten, so wie
die Löschung der Lernkarten mit dem Icon oben rechts.
Man kann auch nach Kategorien filtern.

![lernkarteverwaltungclient.png](doc/screenshots/lernkarteverwaltungclient.png)
![lernkartfilter.png](doc/screenshots/lernkartfilter.png)

Hier nach dem eine Karte gelöscht wurde.
![geloescht.png](doc/screenshots/geloescht.png)

#### **Erstellung einer Lernkarte**
Hier kann man eine neue Karte erstellen. Die hartgecodedten Kategorien kann man anklicken um eine Kategorie zu wählen.
Die Lernkarten sind im Quizclient noch nicht implementiert, daher kann man sie nicht absolvieren.
Das werde ich dann bei der nächsten HÜ machen, die UI gibt es aber schon und sie funktioniert.
![erstellung.png](doc/screenshots/erstellung.png)
![erstellungausgefuehlt.png](doc/screenshots/erstellungausgefuehlt.png)
![lernkarteerstellt.png](doc/screenshots/lernkarteerstellt.png)

#### **Login als User in den Quizclient**
Wie bei der Lernkartenverwaltung ist das Auth hier auch noch nicht fertig implementiert.

![loginuser.png](doc/screenshots/loginuser.png)

#### **Registrierung als User in den Quizclient**

![userregister.png](doc/screenshots/userregister.png)

#### **Quizclient**
Im Quizclient sieht man verschiedene Quizzes. Diese sind jetzt noch hartgecoded und in der nächsten HÜ werde ich
sie dann dynamisch aus der Datenbank laden. Ich fand die verschiedenen Farben für die Kategorien auch ganz cool, habe sie
aber nicht für die Absolvierung der tatsächlichen Quizzes implementiert, mache ich dann in der nächsten HÜ.

![quizclient.png](doc/screenshots/quizclient.png)


#### **Quizfrage**
Die Quizfragen werden hier angezeigt. Es sind tatsächlich richtige Antworten hinterlegt, also sie werden auch korrekt
ausgewertet. Der Progressbalken aktualisiert sich auch dynamisch worauf ich sehr stolz bin weil es eine halbe Ewigkeit gedauert hat.

![quizfrage.png](doc/screenshots/quizfrage.png)
![frage1.png](doc/screenshots/frage1.png)
![frage2.png](doc/screenshots/frage2.png)


#### **Beenden des Quizzes**

Nach dem Absolvieren des Quizzes wird man auf diese Seite weitergeleitet.
Hier steht die Auswertung des Quizzes mit den einzelnen Antworten und ob diese richtig waren. Ich habe die Antworten
jetzt mal mit dem exakten Stringmatching ausgewertet aber ich werde in der nächsten Iteration Keywords verwenden.
Ich werde in der nächsten Iteration auch den grünen Banner auf den Prozentsatz der richtigen Antworten anpassen.
Jetzt zeigt er immer nur "super gemacht" an, auch wenn man 0% hat ;D
Ich will aber ehrlich gesagt diese HÜ jetzt dann mal abgeben, habe schon viel zu viel Zeit investiert.
![quizdone.png](doc/screenshots/quizdone.png)


#### **Responsive Design**
Hier werden die meisten Scenes verkleinert und auf das Responsive Design getestet.
![res1.png](doc/screenshots/res1.png)
![res2.png](doc/screenshots/res2.png)
![res3.png](doc/screenshots/res3.png)
![res4.png](doc/screenshots/res4.png)
![res5.png](doc/screenshots/res5.png)
![res6.png](doc/screenshots/res6.png)


## **Ausbaustufe 2: FLASH-Server**

### **Lösungsidee**
In dieser Ausbaustufe wurde die bestehende Lern-Quiz-Anwendung zu einer Client-Server-Architektur erweitert.
Die gesamte fachliche Logik sowie die Datenhaltung wurden in eine zentrale Serverkomponente ausgelagert,
welche ihre Funktionalität über RMI-Schnittstellen bereitstellt.
Sowohl die Lernkartenverwaltung als auch der Quiz-Client greifen ausschließlich über diese Schnittstellen
auf die Daten zu und übernehmen lediglich Darstellung und Benutzerinteraktion. Die Daten werden in dieser
Ausbaustufe vollständig im Hauptspeicher des Servers gehalten, wodurch mehrere Clients gleichzeitig auf
denselben Datenbestand zugreifen können. Quiz werden dynamisch aus den vorhandenen Lernkarten erzeugt,
wobei pro Kategorie ein eigenes Quiz entsteht. Für die Durchführung eines Quiz verwaltet der Server eine
eigene Quiz-Session, welche den Fortschritt, die gegebenen Antworten und die Auswertung speichert.
Die Überprüfung der Antworten erfolgt serverseitig mithilfe eines robusten Vergleichsalgorithmus,
der Normalisierung, alternative Antwortvarianten sowie kleinere Tippfehler berücksichtigt.
Die Client-Anwendungen bleiben während der Kommunikation mit dem Server durch asynchrone Aufrufe bedienbar.
Zur Qualitätssicherung wurden Unit-Tests für die Antwortbewertung sowie Integrationstests für die Server- und
Quiz-Logik implementiert.

### **Testfälle**
Bitte beachten, dass hier wirklich nur die neue Funktionalität getestet wird. Nichts aus der vorherigen Ausbaustufe.

### Alle Unittests und Integrationtests sind grün:
![unitundintegration.png](doc/screenshots/unitundintegration.png)

### Jetzt kann man sich nicht mit irgendwelchen Credentials anmelden:
![loginfehler.png](doc/screenshots/loginfehler.png)

### Man kann sich vorher registrieren, das Passwort wird auf gültigkeit geprüft:
![pwreg.png](doc/screenshots/pwreg.png)

### Hier mit gültigem Passwort:
![pwgültig.png](doc/screenshots/pwg%C3%BCltig.png)

### Und jetzt kann man sich einloggen. Man sieht oben recht den ausgewählten Namen:
Es bestehen jetzt noch keine Daten, da es ja ein frischer Account ist.
![loggedin.png](doc/screenshots/loggedin.png)

### Wir können jetzt eine Karte erstellen:
Wie wir sehen können, haben wir die Möglichkeit, der Karte Kategorien zuzuweisen, diese spielen dann im Quiz
Client eine entscheidende Rolle.
![card.png](doc/screenshots/card.png)

### Wir haben auch die Möglichkeit, benutzerdefinierte Kategorien zu erstellen:
![customcat.png](doc/screenshots/customcat.png)

### Und dann auch zwischen den Kategorien zu filtern:
![geo.png](doc/screenshots/geo.png)
![customcat2.png](doc/screenshots/customcat2.png)

### Dasselbe gilt auch für den Quizclient: Man muss sich jetzt registrieren oder die Dummydaten verwenden um sich einloggen zu können.

### Das Quizclient sieht dann wie folgt aus:
Ich habe noch ein paar Mathematikkarteien zu Vorzeigezwecken erstellt.
![client.png](doc/screenshots/client.png)

### Wenn wir das Quiz starten, sieht es dann so aus:
![start.png](doc/screenshots/start.png)

### Basierend auf unseren Antworten bekommen wir eine Ergebnisseite:
![ergebnis.png](doc/screenshots/ergebnis.png)
![ergebnis2.png](doc/screenshots/ergebnis2.png)

#### Die Überprüfung der Antworten erfolgt serverseitig mithilfe eines robusten Vergleichsalgorithmus, der Normalisierung, alternative Antwortvarianten sowie kleinere Tippfehler berücksichtigt.
#### Dieser wurde in den Unittests genauer getestet.
