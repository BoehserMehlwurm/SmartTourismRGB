# SmartTourismRGB

 App Prototyp für meine Bachelor Arbeit Smart Tourism.
 
 
 Installationshinweise:
 
 - Android Studio installieren und folgende Tools installieren
  --> Tools / SDK Manager / SDK Platforms / Android 12 und Android 11 installieren evtl auch niedriger. App ist auf min. API Level 24 ausgelegt
  --> Tools / SDK Manager / SDK Tools / Android SDK Platform-Tools, Google Play services, Android SDK Build-Tools-33-rc1 installieren
 
 Java muss installiert sein und als Umgebungsvariable gesetzt sein.
 
Anschließend:

- entweder Git als .zip herunterladen und in Android Studio öffnen 
- oder direkt in Android Stuido / Git / Clone / https://github.com/BoehserMehlwurm/SmartTourismRGB.git
 
 Zum Testen:
 - Smartphone mit Android API Level 24 oder höher und mit Open GL ES Version 3.0 oder höher (siehe https://developers.google.com/ar/devices)
   Mein Testgerät, ein Mi 11, läuft mit Android 11. Es müssen die Entwickleroptionen aufgedeckt werden und USB-Debugging aktiviert werden.
 - oder eventuell Emulator mit eingeschränkten Möglichkeiten (siehe https://developers.google.com/ar/develop/java/emulator#update-arcore)
 
 Zuletzt Compilieren und auf Testgerät ausführen. Meldungen auf dem Smartphone müssen bestätigt werden. 
 Auch wenn Checks für Geräteberechtigungen in der App eingebaut sind. Bei Problemen diese manuell auf dem Smartphone vergeben. (Standort / Kamera / Ordner)
 Zuletzt keinen Dark Mode am Smartphone benutzen.
 
 Zum Testen muss ein Google API Code vorhanden sein mit folgenden aktivierten API's. (siehe https://developers.google.com/maps/documentation/android-sdk/start?hl=de)
 - Directions API
 - Maps SDK for Android
 - Geocoding API
 
Der Google Maps Key muss in eine Datei namens: 
google_maps_api.xml unter res / values (gleicher Ordner wie strings.xml und colors.xml)

Nach erstellen folgenden Inhalt reinkopieren und API Key einfügen.

<resources>
<string name="google_maps_key" templateMergeStrategy="preserve" translatable="false">HIER eigenen Maps Key einfügen. Beginnt mit AI...</string>
</resources>
