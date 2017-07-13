# PioneerRobotApp

# Projektstrukur:
- <root>\App\Server\
- Aria-Controller	// UDP Recv for control messages
- Videostream	// RTP Video Streaming to Client over UDP

# Komponenten:
- ARIA
- MobileSim

# START DES ROBOT AUF DEM PC (LINUX)

für Video wird das Paket motion benötigt. 
Weitere Anweisungen in config_Video.txt

In Server befinden sich udp_server (für Steuerung und telemetrie)
in sound_server befindet sich der Server für die Hupe

vor start des udp_server muss der Roboter eingeschaltet und angesteckt werden.

Zum Start der Steuerung und Telemetrie muss sudo python main.py -robotPort /dev/ttyUSB0 (USB port kann variieren) ausgeführt werden

Zum Start des Sound_Server muss nur sudo python main.py ausgeführt werden

zum Start des Videos muss sudo motion start ausgeführt werden.
