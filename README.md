# PioneerRobotApp

# Projektstrukur:
- <root>\App\Server\
- Aria-Controller	// UDP Recv for control messages
- Videostream	// RTP Video Streaming to Client over UDP

# Komponenten:
- ARIA
- MobileSim

# START DES ROBOT AUF DEM PC (LINUX)

Für das Video wird das Paket "motion" benötigt. Das muss mit sudo apt install motion nachinstalliert werden. 
Weitere Anweisungen in 'config_Video.txt'.

Vor start des udp_server muss der Roboter eingeschaltet und per USB an den Rechner oder Raspberry Pi angesteckt werden.

Aus dem Ordner 'Server' müssen zwei Server gestartet werden:
- udp_server // für Steuerung und Telemetrie
- sound_server // Server für die Hupe
Anmerkung zum sound_server: Falls das Skript auf einem Raspberry Pi läuft muss die Ausgabe des Tons über 'omxplayer' laufen, unter Ubuntu unter mplayer. Der Hinweis wurde auch im Code vom Skript vermerkt.

Der Befehle um die Server zu starten (USB port kann variieren):
udp_server: python main.py -robotPort /dev/ttyUSB0
sound_server: python main.py
(evntl mit sudo probieren)

Um die Videoübertragung zu starten reicht folgender Befehl:
sudo motion start

Auf dem Rechner muss ein neuer WLAN Access Point aufgemacht werden um sich mit dem Handy zu verbinden. Eine Internetverbindung ist nicht nötig.

# App
Auf dem Smartphone WLAN aktivieren und das neue WLAN auswählen (Robot_x).
