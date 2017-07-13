import socket
import sys

# Create a UDP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

sock.bind(('', 8844))
f = open('udp_log.txt', 'w')
temp_list = []
while 1:
    print >>sys.stderr, 'waiting to receive'
    sock.settimeout(60)
    data, server = sock.recvfrom(16)
    temp_message = format(bin(ord(data)))[2:10]
    print >>sys.stderr, temp_message

    if (temp_message not in temp_list):
        temp_list.append(temp_message)
        f.write(temp_message)
        f.write("\n")

print >>sys.stderr, 'closing socket'
sock.close()
f.close()


