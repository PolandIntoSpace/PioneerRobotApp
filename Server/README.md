# Mehr Informationen zu Serverseitiger Umsetzung

UDP Message:

Stand 07.05.1027

MSG hat Länge: 2 bytes
1. Byte -> Fahrrichtung
0 	0 	0 	0 	0 	0 	0 	0
n/a 	n/a 	n/a 	n/a 	links   rechts  rück	vor

Beispiel:
1 => vorwärts
3 => keine Bewegung
5 => vorwärts + rechts
9 => vorwärts + links

2. Byte -> Proportinaler Speed Faktor
Bits 0-4 -> Geschwindigkeit lateral ( 0-15 => 0%-100% )
Bits 5-8 -> Geschwindigkeit rotation ( 0-15 = 0% - 100% )

Bsp.
Erst Wert zwischen 0-15 in int schreiben, dann Bit-Shift um 4 nach links
Dann lateral-Wert addieren
