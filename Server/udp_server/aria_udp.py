#!/usr/bin/env python
# -*- coding: UTF-8 -*-

"""
Der UDP Server, der UDP Pakte annimmt und in ARIA Befehle umwandelt.
@author Stefan Turzer
"""

from AriaPy import *
import sys
import socket
import time
import logging
import json

class udp_server:

    def __init__(self):
        # Define Safety Limits
        self.safety_limits = {'front': 500,
                               'back': 500
                              }

        self.speed_limits = {'forward': 750,
                             'backward': -300,
                             'right': 30,
                             'left': -30
                             }

        self.direction = {'forward': False,
                          'backward': False,
                          'right': False,
                          'left': False}

        self.lateral_speed_modifier = 0.0
        self.rotational_speed_modifier = 0.0
        
        # Logger set to debug loglvl
        logging.basicConfig()
        self.dLog = logging.getLogger()
        self.dLog.setLevel(logging.DEBUG)
        
        # MUSS KONFIGURIERT WERDEN (derzeit noch)
        self.THOST = None
        self.TPORT = 8845
        self.CHOST = ''
        self.CPORT = 8844
        
        socket.setdefaulttimeout(0.2)

        self.telmSocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        
        self.ctrlSocket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.ctrlSocket.bind((self.CHOST, self.CPORT))
    def robot_init(self):       
        # Connect to Robot/Simulation
        Aria_init()
        parser = ArArgumentParser(sys.argv)
        parser.loadDefaultArguments()
        if not Aria_parseArgs():
            Aria_logOptions()
            Aria_exit(1)

        self.robot = ArRobot()
        self.conn = ArRobotConnector(parser, self.robot)

        if not self.conn.connectRobot():
            self.dLog.error("Could not connect to robot, exiting")            
            Aria_exit(1)

        # Connect to Sonar
        self.sonar = ArSonarDevice()
        self.robot.addRangeDevice(self.sonar)

        self.robot.enableMotors()
        self.robot.enableSonar()

        # self.socke = ArSocket(8844, True, ArSocket.UDP)

        self.stuff = ArSystemStatus

    def start_robot(self):
        # Enable Subsystems
        self.robot.enableMotors()
        self.robot.enableSonar()

        # Start Robot Socket Listener
        # self.socke.open(8844, ArSocket.UDP)

    def run(self):
        # Init Robot
        self.robot_init()
        
        # Start up Robot Task
        self.start_robot()
        self.robot.runAsync(1)
        
        # Timeout Vars
        fTimeout = False
        t_t = time.time();
        t_c = time.time();
        while 1:
            # 1. Get the current Sonar Readings
            sonar_readings = self.get_sonar_readings()

            # 2. Receive UDP Command, timeout after 200 ms
            try:
                # Better Socket Implementation
                (data, address) = self.ctrlSocket.recvfrom(8)
                # We send back to the sender of the control message
                
                
                if len(data):
                    # Get IP
                    self.THOST = address[0]
                    # Reset Timeout Counter
                    fTimeout = False
                    
                    self.check_msg(data)
                    
                    self.set_robot_velocities(self.direction,
                      sonar_readings,
                      self.lateral_speed_modifier,
                      self.rotational_speed_modifier
                     )

            except socket.timeout:
                if not fTimeout:
                    self.dLog.warn("SOCKET TIMEOUT!")
                    fTimeout = True
                    t_c = time.time()
                elif t_c > time.time() - 1:
                    self.dLog.debug("Keep Driving")
                    self.set_robot_velocities(self.direction,
                                          sonar_readings,
                                          self.lateral_speed_modifier,
                                          self.rotational_speed_modifier
                                         )
                else:
                    if self.robot.getVel() != 0:
                        self.dLog.info("Stopping Lateral Movement now")
                    if self.robot.getRotVel() != 0:
                        self.dLog.info("Stopping Rotational Movement now")
                    self.stop_robot()

            # 3. Telemetry

            if t_t < time.time() - 0.2:
                t_t = time.time();
                # Sonar Ranges in richtiger Reihenfolge und bereinigt holen
                messung = self.getListOfSonarRanges()
                
                # In JSON String verpacken
                telemetry = {"battery": self.robot.getRealBatteryVoltageNow(),
                             "speed": self.robot.getVel(),
                             "messung": messung
                             }
                try:
                    self.telmSocket.sendto(json.dumps(telemetry), (self.THOST, self.TPORT))
                except socket.error:
                    self.dLog.warn("Destination Unreachable or Network Congested")
                except TypeError:
                    #self.dLog.debug("Not yet connected to remote Smartphone")
                    pass
                
    def check_msg(self, data):
        ## print data, "<--- MSG"
        # Direction is in 1st byte
        if len(data) >= 1:
            #self.direction = self.get_directions(data[0])
            #self.lateral_speed_modifier, \
            #self.rotational_speed_modifier = self.get_speed_modifier(data[1])#
            self.direction, \
            self.lateral_speed_modifier, \
            self.rotational_speed_modifier = self.get_modifiers(data[0])

    def get_directions(self, dir_byte):
        directions = {}
        # Konvertiere Byte nach int
        i = ord(dir_byte)
        # Bit Shifting und Modulo 2 um einzelne bits zu extrahieren
        directions['forward'] = bool(i % 2)
        i = i >> 1
        directions['backward'] = bool(i % 2)
        i = i >> 1
        directions['right'] = bool(i % 2)
        i = i >> 1
        directions['left'] = bool(i % 2)

        # Wenn entgegengesetze Richtungen ankommen soll der Roboter einfach still stehen bleiben
        if directions['forward'] == directions['backward']:
            directions['forward'] = False
            directions['backward'] = False
        if directions['left'] == directions['right']:
            directions['left'] = False
            directions['right'] = False

        return directions

    def get_speed_modifier(self, speed_byte):
        # Konvertiere Byte nach int
        i = ord(speed_byte)

        # Modulo and byte shifting by 4 bytes
        lateral_mod = (i % 16) / 16.0
        i = i >> 4
        rotational_mod = (i % 16) / 16.0
        return lateral_mod, rotational_mod

    def get_modifiers(self, msg_byte):
        directions = {}
        i = ord(msg_byte)
        
        lateral_mod = (i % 8) / 7.0
        i = i >> 3
        rotational_mod = (i % 8) / 7.0
        i = i >> 3

        directions['backward'] = bool(i % 2)
        directions['forward'] = not directions['backward']
        i = i >> 1
        directions['left'] = bool(i % 2)
        directions['right'] = not directions['left']
        # print directions, lateral_mod, rotational_mod
        return directions, lateral_mod, rotational_mod

    def set_robot_velocities(self, directions, sonar_readings, lateral_speed_modifier, rotational_speed_mod):

        # Set Velocities according to command and limit by sonar Reading
        if directions['forward'] & (sonar_readings['front'] >= self.safety_limits['front']):
            self.robot.setVel( lateral_speed_modifier * self.speed_limits['forward'] )
        elif sonar_readings['back'] <= self.safety_limits['back']:
            self.robot.setVel(0)
            
        if directions['backward'] & (sonar_readings['back'] >= self.safety_limits['back']):
            self.robot.setVel( lateral_speed_modifier * self.speed_limits['backward'])
        elif sonar_readings['front'] <= self.safety_limits['front']:
            self.robot.setVel(0)


        if directions['forward'] == directions['backward']:
            self.robot.setVel(0)

        if directions['right']:
            self.robot.setRotVel(rotational_speed_mod * self.speed_limits['right'])
        if directions['left']:
            self.robot.setRotVel(rotational_speed_mod * self.speed_limits['left'])

        if directions['right'] == directions['left']:
            self.robot.setRotVel(0)

    def get_sonar_readings(self):
        sonar_readings = dict()
        # Frontale Sensoren
        sonar_readings['front'] = self.robot.getClosestSonarRange(-30, 30)
        # Rückwärtige Sensoren
        sonar_readings['back'] = self.robot.getClosestSonarRange(150, -150)

        return sonar_readings

    def getListOfSonarRanges(self):
        num_sonars = self.robot.getNumSonar()
        messung = [0.0] * num_sonars
        # Roboter sagt er hat 16 Sonars, hat aber nur 8
        for i in range(num_sonars):
            m = self.robot.getSonarRange(i)
	    #if m >= 5000:
            #   m = -1.0
	    index = i - num_sonars / 4
	    if (index < 0):
		index = index + num_sonars
            messung[index] = m

        return messung

    def stop_robot(self):
        self.robot.setRotVel(0)
        self.robot.setVel(0)
        
    def close(self):
        self.dLog.info("Closing Telemetry Socket ...")
        self.telmSocket.close()
        self.dLog.info("Closing Controller Socket ...")
        self.ctrlSocket.close()
        self.dLog.info("Stopping Robot ...")
        self.robot.stopRunning(True)
        self.dLog.info("Shutting down Aria ...")
        Aria_exit(1)