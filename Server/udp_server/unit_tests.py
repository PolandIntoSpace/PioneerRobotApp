#!/usr/bin/env python2
# -*- coding: utf-8 -*-
"""
Created on Sat Jul  1 11:29:37 2017

@author: Stefan Turzer
"""

import aria_udp
import unittest
import subprocess
import time

class AriaUDPTestSuite(unittest.TestCase):
    def setUp(self):
        self.aria = aria_udp.udp_server()
        # self.p = subprocess.Popen(["MobileSim","--nomap", "--minimize-gui"])
        pass
    
    def tearDown(self):
        self.aria = None
        pass
        
    def test_maxmsg(self):
        max_val = bytearray()
        max_val.append(255)

        dir, lat, rot = self.aria.get_modifiers(max_val)
        
        self.assertAlmostEqual(lat, 1.0, "Lateral Speed Modifier Bit Wrong")
        self.assertAlmostEqual(rot, 1.0, "Rotation Speed Modifier Bit Wrong")
        self.assertEqual(dir, {'forward': False,
                               'backward': True,
                               'right': False,
                               'left': True}, "Directional Bits Wrong")
    def test_minmsg(self):
        max_val = bytearray()
        max_val.append(0)

        dir, lat, rot = self.aria.get_modifiers(max_val)
        
        self.assertAlmostEqual(lat, 0.0)
        self.assertAlmostEqual(rot, 0.0)
        self.assertEqual(dir, {'forward': True,
                               'backward': False,
                               'right': True,
                               'left': False})

    def test_fwdLeftMediumSpeed(self):
        max_val = bytearray()
        max_val.append(27)
    
        dir, lat, rot = self.aria.get_modifiers(max_val)
        
        self.assertAlmostEqual(lat, 0.42857142857142855)
        self.assertAlmostEqual(rot, 0.42857142857142855)
        self.assertEqual(dir, {'forward': True,
                               'backward': False,
                               'right': True,
                               'left': False})
    def test_bwdRightMediumSpeed(self):
        max_val = bytearray()
        max_val.append(219)
    
        dir, lat, rot = self.aria.get_modifiers(max_val)
        
        self.assertAlmostEqual(lat, 0.42857142857142855)
        self.assertAlmostEqual(rot, 0.42857142857142855)
        self.assertEqual(dir, {'forward': False,
                               'backward': True,
                               'right': False,
                               'left': True})
    
        
if __name__ == '__main__':
    suite = unittest.TestLoader().loadTestsFromTestCase(AriaUDPTestSuite)
    unittest.TextTestRunner(verbosity=2).run(suite)