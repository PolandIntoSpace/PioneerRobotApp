import socket
import sys
import os

# @Author Aleksandra Targowicki
# This script belongs to the Pioneer Robot App.
# It hast so run to receive and play the sound ID from the app.
#
# For Ubuntu: use mplayer instead of omxplayer 

# Create a UDP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

sock.bind(('', 8846))

while 1:
    print >>sys.stderr, 'waiting to receive sound'
    data, server = sock.recvfrom(16)
    temp_message = format(bin(ord(data)))[2:10]
    print >>sys.stderr, temp_message

    if int(temp_message, 2) is 0:
        os.system('omxplayer Roboter_Sounds/bycicle.mp3')
    elif int(temp_message, 2) is 1:
        os.system('omxplayer Roboter_Sounds/horn.mp3')
    elif int(temp_message, 2) is 2:
        os.system('omxplayer Roboter_Sounds/mieze.mp3')
    #elif int(temp_message, 2) is 3:
	#do sth

print >>sys.stderr, 'closing sound socket'
sock.close()


