# PioneerRobotApp
Die PioneerRoboterApp wurde von insgesammt vier Studenten von der Hochschule München für das Fach Mobile Anwendungen erstellt. Sie erlaubt es den Roboter Pioneer P3-DX über eine WLAN-Verbindung zu steuern. Der Roboter wurde freundlicherweise von der Fakultät für Geoinformation zur Verfügung gestellt.
Es gibt folgende Funktionalitäten:
- Login über NFC Tag möglich
- Hupe, d.h. Tonausgabe möglich falls Lautsprecher angeschlossen und zugehöriger Server gestartet
- Video, d.h. falls Kamera angeschlossen lässt sich eine Live-Ansicht aus Sicht des Roboters auf das Smartphone streamen.

Alle Komponenten zusammen wurden erfolgreich an einem leistungsschwachen Rechner mit Ubuntu 16.04 LTS getestet.
Bis auf die Kamera wurden auch alle Komponenten auf einem Raspberry Pi getestet. Autostart wurde nicht eingerichtet.

# Komponenten:
- ARIA
- MobileSim

# Start des Roboters auf dem Rechner (LINUX)

Für das Video wird das Paket "motion" benötigt. Das muss mit sudo apt install motion nachinstalliert werden. 
Weitere Anweisungen in 'config_Video.txt'.

Vor start des udp_server muss der Roboter eingeschaltet und per USB an den Rechner angesteckt werden.

Aus dem Ordner 'Server' müssen zwei Server gestartet werden:
- udp_server // für Steuerung und Telemetrie
- sound_server // Server für die Hupe

Der Befehle um die Server zu starten (USB port kann variieren):
- udp_server: python main.py -robotPort /dev/ttyUSB0
- sound_server: python main.py
- (evntl mit sudo probieren)

HINWEIS: Der sound_server wurde ursprünglich für den Raspberry Pi geschrieben. Um Ton auszugeben muss die Ausgabe statt vom omxplayer über ein anderes Programm erfolgen. Eine einfache alternative ist z.B. unter Ubuntu der mplayer. Dafür reicht es im Skript 'omxplayer' gegen 'mplayer' auszutauschen. Der Hinweis wird auch im Code erwähnt.


Um die Videoübertragung zu starten reicht folgender Befehl:
sudo motion start

Auf dem Rechner muss ein neuer WLAN Access Point aufgemacht werden um sich mit dem Handy zu verbinden. Eine Internetverbindung ist nicht nötig.

# Start des Roboters von einem Raspberry Pi aus

Um den udp_server auf einem Raspberry Pi laufen zu lassen sind ein paar weitere Schritte zu den oben genannten erforderlich.
Die genaue Anleitung dazu befindet sich im Ordner PioneerRobotApp/Server/config-raspi/README.md.
WICHTIG!! Nach der Einrichtung in der Einleitung wird der RaspPi nicht mehr per WLAN ins Internet kommen, wenn man keinen weiteren WLAN Stick verwendet (und einrichtet). Es ist aber immer noch möglich per LAN-Kabel sich zu verbinden ohne die Einstellungen rückgängig machen zu müssen. Der WLAN Access Point wird damit auch miteingerichtet.

Der sound_server muss für den RaspPi standartmäßig nicht umgeschrieben werden, es kann aber vorkommen dass falls die Datei zuvor auf einem Rechner lief, das für die Tonausgabe zuständiges Programm geändert werden muss. Standartmässig wird der omxplayer bei Jessie ausgeliefert und kann verwendet werden. Dieser Hinweis befindet sich auch Skirpt selber.  


# App
Auf dem Smartphone WLAN aktivieren und das neue WLAN auswählen sowie IP Adresse angeben.
Beim ändern der IP Adresse sollte auch der NFC Tag neu beschrieben werden. Dafür gibt es zahlreiche Apps in Googles Play Store.
