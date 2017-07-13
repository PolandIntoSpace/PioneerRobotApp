# How to set up a raspberry pi Hotspot
@author Stefan Turzer

Set up a Wireless hotspot on wlan0 (and grant access to the internet on eth0)

1. Install hostapd, isc-dhcp-server, haveged

sudo apt-get install hostapd isc-dhcp-server haveged

2. Configuration of wlan0

edit /etc/network/interfaces to say the following:

--->
auto wlan0
iface wlan0 inet static
address 10.10.0.1
netmask 255.255.255.0
<---

and add the following to the end of /etc/dhcpcd.conf

--->
# Custom static IP address for wlan0.
interface wlan0
static ip_address=10.10.0.1/24
static routers=10.0.0.1
static domain_name_servers=8.8.4.4
<---

3. Set up dhcpd server
copy the file 'dhcpd.conf' to /etc/dhcp/dhcpd.conf

# enable autostart of dhcp server
sudo update-rc.d isc-dhcp-server enable
# startup dhcp server
sudo service isc-dhcp-server start

4. Set up Hostapd
copy file 'hostapd.conf' to /etc/hostapd/hostapd.conf

# change ssid and/or password in hostapd.conf file
default is:
- SSID: Testing
- Pass: S3cr3tP4ssw0rd11

in /etc/init.d/hostapd change the following line:
DAEMON_CONF=
to say:
DAEMON_CONF=/etc/hostapd/hostapd.conf

# enable autostart
sudo update-rc.d hostapd enable
sudo service hostapd start

5. for Internet Access

enable ip4 forwarding:
uncomment the following line in /etc/sysctl.conf :

net.ipv4.ip_forward=1

reboot so it takes effect

change iptables:

create directory /etc/network/iptables/
copy iptables.sav into it
copy iptables-restore to /etc/network/if-pre-up.d/iptables-restore

# make executable
sudo chmod +x /etc/network/if-pre-up.d/iptables-restore

6.
now check if it works!
