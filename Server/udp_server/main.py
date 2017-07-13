#!/usr/bin/env python
# -*- coding: UTF-8 -*-

"""
Main Methode des Aria UDP Servers
Ausführen mit "-robotPort /dev/ttyUSB0 wenn Roboter über USB angeschlossen ist

@author: Stefan Turzer
"""

import aria_udp
import traceback

if __name__ == "__main__":
    try:
        server = aria_udp.udp_server()
        server.run()
    except:
        traceback.print_exc()
        server.close()