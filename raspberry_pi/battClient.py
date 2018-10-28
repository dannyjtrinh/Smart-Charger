import socket
import RPi.GPIO as GPIO
import signal
import sys
import datetime

UDP_IP = "224.0.0.1"
UDP_PORT = 10001
SWITCH_PIN = 7 


class battClient():

    def __init__(self):        
        self.i = 0
        self.power_flag = 0
        try:
            self.setup_socket()
            self.setup_GPIO()
        except:
            print("Socket creation error. Make sure client address set\n")
        self.run() #Run the client that controls and uses charging scheme

    def setup_GPIO(self):
        #Setup Raspberry Pi GPIO
        GPIO.setwarnings(False) #ignore warnings because we have GPIO high after exit
        GPIO.setmode(GPIO.BOARD)
        GPIO.setup(SWITCH_PIN, GPIO.OUT)
        GPIO.output(SWITCH_PIN, GPIO.HIGH)

    def setup_socket(self):
        #Setup UDP socket
        HOST_IP = socket.gethostbyname(socket.gethostname())
        self.sock = socket.socket(socket.AF_INET, # Internet
                                  socket.SOCK_DGRAM,
                                  socket.IPPROTO_UDP) # UDP
        self.sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.sock.bind(('', UDP_PORT))
        self.sock.settimeout(10)
        mreq = socket.inet_aton(UDP_IP) + socket.inet_aton(HOST_IP)
        self.sock.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, mreq)
        
    def signal_handler(self, sig, frame):
        print("Exit command\n")
        GPIO.output(SWITCH_PIN, GPIO.HIGH) #revert to regular charging on exit
        self.sock.close()
        sys.exit(0)

    def run(self):
        while True:
            signal.signal(signal.SIGINT, self.signal_handler) #ctrl-c capture
            try:
                data, addr = self.sock.recvfrom(1024) # buffer size is 1024 bytes
                if(data == "100" and ~self.power_flag):
                    GPIO.output(SWITCH_PIN, GPIO.LOW)
                    self.power_flag = 1
                    self.stat = "Fully charged\n"
                elif(int(data) <= 80):
                    GPIO.output(SWITCH_PIN, GPIO.HIGH)
                    self.power_flag = 0
                    self.stat = "Charging %s\n" % (data)
                else:
                    t = datetime.datetime.now().time()
                    t = str(t).split(":")
                    h = int(t[0])
                    if(h <7):
                        GPIO.output(SWITCH_PIN, GPIO.HIGH)
                    else:
                        GPIO.output(SWITCH_PIN, GPIO.LOW)
                    self.stat = "Not Charging %s\n" % (data)
                self.i +=1
                print("received message#%d: %s" % (self.i,self.stat))
            except socket.timeout:
                GPIO.output(SWITCH_PIN, GPIO.HIGH)
                print("Battery service not active, resulting to regular charging")
            
battClient()
