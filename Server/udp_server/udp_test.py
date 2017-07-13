#!/usr/bin/env python
# -*- coding: UTF-8 -*-
"""
Program to Test the UDP Message Format to Send

@author: Stefan Turzer
"""

import socket
import random
from time import sleep

host = "localhost"
port = 8844
addr = (host, port)

s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

while 1:
    data = bytearray()
    # Directional byte -> letzte 4 bit gesetzt -> char kleiner als 16
    data.append(255)
    data.append(random.randint(0, 255))

    s.sendto(str(data), (host, port))
    sleep(0.1)
