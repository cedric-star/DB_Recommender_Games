# Recommender System - "Games"

Diese Read-Me enthält alle relevanten Infos zum Recommender System "Games".

## Beschreibung

Das Recommender System "Games" ist ein mächtiges hybrides Recommender-System zur Empfehlung vom Videospielen und anderen Medien.

Dieses System basiert auf der Nutzung von Java-Beans und einem Tomcat-Server.

## Installation

### Voraussetzungen

### IntelliJ

### Datagrip

### Problembehebung


1. Klone das Repo in einen beliebigen Ordner (mit ausreichenden Rechten.)
2. Starte das Projekt in IntelliJ Ultimate Edition 
3. Passe Projektstruktur an:
   1. Gehe zu: File > Project Structure...
   2. Tab "Project"
      2. SDK: openjdk-19
      3. Language level: 19 - No new language features
   3. Tab "Modules"
      1. Dependencies: WEB_INF/lib > Scope auf "Provided" setzen
   4. Tab "Libraries"
      1. füge lib "ojdbc7.jar" hinzu
      2. füge lib "SortedList.jar" hinzu
4. Passe Run-Config an
   1. Neue Konfiguration mit Tomcat Serve, Local anlegen
   2. URL setzen auf: "http://localhost:8080/DB_Recommender_Games"
   3. JRE auf jdk-19 setzen
   4. Tab Deployment > Add > External Source > .../DB_Recommender_Games (standard)
5. Ausführung über den grünen Button in IntelliJ möglich
6. Zugriff auf den Server über "http://localhost:8080/DB_Recommender_Games"

### Mögliche Fehlerquellen bei der Installation

+ zu 1.: Passe die Rechte (ideal rekursiv via "chmod -R 777" <dirname>) des Ordners an, sodass Tomcat Dateien wenn nötig verschieben kann.
+ zu 5.: Ins Hochschulnetz verbinden (via VPN oder Ethernet)
+ zu 6.: Neustarten und warten, bis es automatisch aufgeht.