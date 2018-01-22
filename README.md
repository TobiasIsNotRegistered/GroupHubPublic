# GroupHub (🐬 + 🍽)

Dieses Projekt verspricht eine einfache Lösung, um sich für ein Essen zu verabreden.

- mit purem Java
- mit einem einfachen Servlet, welches über Jetty gestartet wird
- mit Android als Client-Technologie
- mit Gradle als Build-Automation

## Autoren
* Tobias Sigel
* Mario Winiker


## Arbeitsumgebung
- Java 8 JDK: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
- Git: https://git-scm.com/book/en/v2/Getting-Started-Installing-Git
- Java IDE ihrer Wahl, empfohlen wird IntelliJ: https://www.jetbrains.com/idea/download/#section=windows
- AndroidStudio 3.0+. Es wird später ein VirtualDevice mit API-Level 26+ benötigt. https://developer.android.com/studio/index.html
- Gradle: https://gradle.org/install/

## OpenDolphin Server-Applikation starten
API-Level 1.8
1. Erstelle einen lokalen Ordner mit dem Namen «GroupHubRocks» in deinem Workspace. Ausrufezeichen sind leider nicht erlaubt, wir haben es ausprobiert.
2. Erstelle zwei weitere Ordner innerhalb des «GroupHubRocks»-Ordners. Am besten passen die Namen «Server» und «Android».
3. Führe mit «Shift+Rechtsklick» im Ordner «Server» Git-Bash aus (oder starte cmd).
4. Erstelle ein neues lokales Git-Repo mit git init.
5. Füge dem lokalen Repo unser GitHub Remote-Repo hinzu: `git remote add origin https://github.com/FHNW-IP5-IP6/GroupHubFX.git`
6. Lade den Branch «AAA_final_server_open_dolphin» herunter mit `git pull origin AAA_final_server_open_dolphin`
7. Wechsle auf den Branch  «AAA_final_server_open_dolphin» mit `git checkout AAA_final_server_open_dolphin`

Das Projekt befindet sich nun im Ordner «Server» und kann von der IDE importiert werden.

/Der Server kann jetzt gestartet werden:/ 
* Führe `gradlew install` in der Kommandozeile des Projektes aus (alt+F12)  aus.
* Führe das Kommando `gradlew jettyRun` aus

## Android-Client starten
Es wird ein Emulator-Image mit API-Level 26+ benötigt. Es wird auch die Installation von «Haxm» auf dem Image (wird im Installer gefragt) empfohlen. Source- und Target Compatibility sind beide 1.8 (genau gleich wie bei dem Server).
* Wiederhole die Schritte 2 bis 4 aus dem Server-Teil aber im Ordner «Android».
* Lade den Branch «AAA_final_android» mit `git pull origin AAA_final_android`
* Wechsle in den Branch «AAA_final_android» mit `git checkout AAA_final_android`

+ Das Android-Projekt ist nun im Ordner «Android» abgelegt und kann von Android Studio importiert werden.
+ Ist ein Emulator-Image mit API-Level 26+ installiert, so kann man jetzt die 

/Applikation starten:/
* Klicke in Android Studio auf das grüne Play-Symbol in der oberen Toolbar. 
* Wähle das Image mit API-Level 26+ aus und bestätige dieses.

Hinweis: Es kann sein, dass die «Intel Virtualization Technology» im BIOS ihres Computers aktiviert werden muss. Die Einstellungen dazu können pro Hersteller unterschiedlich. Die Aufarbeitung des Android-Projektes benötigt beim ersten Start einiges an Zeit.

Die Login-Daten für die Android-Applikation sind wie folgt:
Dierk Koenig, Dieter Holz, Mario Winiker, Tobias Sigel → mit dem PW «admin»
alle anderen in der Applikation ersichtlichen Benutzer → mit dem PW «GroupHubRocks!»
