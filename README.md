# Recommender System - "Games"

Diese Read-Me enthält alle relevanten Infos zum Recommender System "Games".

## Beschreibung

Das Recommender System "Games" ist ein mächtiges hybrides Recommender-System zur Empfehlung vom Videospielen und anderen Medien.

Dieses System basiert auf der Nutzung von Java-Beans und einem Tomcat-Server.

## Installation

Folgende Abschnitte sollen die Installation des Projekts und die Ausführung durch Tomcat auführlich beschreiben.
Dabei wird von einem Windows- oder Linuxsystem ausgegangen. In beiden Fällen sollte sich die Installation kaum unterscheiden.

### Voraussetzungen

Betriebssystem: Windows 10+, Linux Mint oder andere Debian/Ubuntu-Distros

Java JDK: min. Version 19 vorhanden

Java-IDE: IntelliJ (min. 2020)

Datenbanken-Umgebung: Datagrip (min. 2020)

(optional) Git-Installation zum Ziehen dieses Repos.

### Was wird mitgeliefert?

Dieses Projekt liefert alle benötigten Daten zu Servlets, Beans, usw. mit, die für die Ausführung des Systems als Apache Tomcat-Server zu ermöglichen.
Entsprechend werden eine Reihe von Bibliotheken mitgeliefert im WEB-INF/lib Verzeichnis. Wie diese eingebunden werden wird im folgenden Abschnitt erklärt.

Die Tomcat-Installation wird jedoch *nicht* mitgeliefert. Entsprechend muss die Tomcat-Version 11.0.9 heruntergeladen werden als .zip, welche in einem beliebigen Ordner mit entsprechenden Rechten auf dem System liegen sollte.

### IntelliJ

**ACHTUNG: Bei Erstellung des Projekts (siehe Schritt 1) sollte die gesamte Konfiguration, bis auf das Verzeichnis der Tomcat-Installation (siehe Schritt 3.2) bereits vorhanden sein. Die Befolgung aller weiteren Schritte ist zu empfehlen, falls es Probleme bei der Ausführung gibt.** 

1. Neues Projekt anlegen (empfohlen: File > New > Project from Version Control...)
2. Navigiere zu Project Structure - File > Project Structure...
   1. Menüpunkt "Project" - SDK: JDK Version 19+ auswählen, Language Level: Auf 19 setzen
   2. Menüpunkt "Modules"
      1. Tab "Sources" - ein Modul namens "DB_Recommender_Games" (enthält den gesamten Projektordner); WEB-INF/classes sollte als Source (blau) markiert sein; out /out sollte als Excluded markiert sein
      2. Tab "Paths" - Radiobutton "Use module compile output path" wählen, "Output path" auf [Ordner in dem Projekt liegt]/DB_Recommender_Games/WEB-INF/classes festlegen; "Exclude output paths" *nicht* setzen
      3. Tab "Dependencies" - Eintrag über "+" hinzufügen, "JARs or Directories..." und anschließend lib-Ordner in WEB-INF anwählen
   3. Menüpunkt "Libraries" - falls nicht vorhanden: lib, lib2 und ojdbc17 aus WEB-INF/lib hinzufügen
   4. Menüpunkt "Artifacts" - "+" > "Web Application: exploded"; Konfiguration den Namen "DB_Recommender_Games" geben; Tab "Output Layout" - "+" > "Directory Content" > Wähle Projektordner (wieder DB_Recommender_Games) aus
3. Navigiere zu "Edit configurations..." - neben Ausführen/Debug-Buttons oben rechts
   1. Neue Config erstellen "+" > Tomcat Server > Local
   2. Tab "Server" - Application Server: Auswählen der Tomcat-Installation 11.0.9; URL auf "http://localhost:8080/DB_Recommender_Games" setzen; JRE 19 wählen; Rest auf Standard lassen
   3. Tab "Deployment" - "+" > Artifact von vorher wählen; Before Launch: "-" alle Einträge entfernen, mit "+" die Einträge "Build Project" und "Build 'DB_Recommender_Games' artifact" hinzufügen
   4. Tab "Logs" (optional) - alles auswählen
   5. alle restliche Einträge auf Default lassen

Um Datenbank-Konnektivität zu ermöglichen muss die entsprechende VPN-Verbindung zur HS Harz aufgebaut werden.

Start des Servers sollte nun über den grünen Play-Button möglich sein. Durch die zuvor erstellte Konfiguration sollte alles automatisch gebaut und deployt werden. Das Browser-Fenster öffnet sich entsprechend.

Zugriff ist nach der Ausführung jederzeit via http://localhost:8080/DB_Recommender_Games möglich.

### Problembehebung

+ Passe die Rechte (ideal rekursiv via "chmod -R 777" <dirname>) des Ordners an, sodass Tomcat Dateien, wenn nötig verschieben kann.
+ Ins Hochschulnetz verbinden (via VPN oder Ethernet)
+ Neustarten und warten, bis es automatisch aufgeht.