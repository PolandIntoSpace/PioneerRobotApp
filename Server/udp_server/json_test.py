#!/usr/bin/env python

import socket
import json
import time
import random
import math

s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
addr = ('10.42.0.184',8845)
s.connect(addr)
bat = 0.0
spd = 0.0


while 1:
	bat = bat + random.random()
	spd = spd + random.random()
	messung = list()
	for i in range(32):
		if ( i > 8 and i < 24):
			messung.append(0.0)
		else:
			messung.append( math.floor( 100. * random.random() * 5000) / 100.)

	telemetry = {"battery": bat, "speed": spd , "messung": messung}
	try:
		s.send(json.dumps(telemetry))
	except socket.error:
		print "lol"
		time.sleep(1)
	time.sleep(0.2)
