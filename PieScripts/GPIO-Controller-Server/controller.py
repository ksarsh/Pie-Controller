import os
from socket import *

from gpio import setupGPIO, toggle_led

BUFSIZE = 1024
H_ADDR = "10.0.0.156"
H_PORT = 8080
ADDR = (H_ADDR, H_PORT)

# CPU Temp
CPU_TEMP_CMD = "/opt/vc/bin/vcgencmd measure_temp"

class Controller():
    def __init__(self):
        self.tcpServerSock = socket(AF_INET, SOCK_STREAM)
        self.tcpServerSock.bind(ADDR)
        self.tcpServerSock.listen(5)
        self.cpu_temp = 0

    def run_server(self):
        while True:
            print("Waiting for Connection ...")
            self.tcpClientSock, self.addr = self.tcpServerSock.accept()
            print("... Connected from Droid: {}".format(self.addr))
            total_data = ""
            try:
                while True:
                    data = ''
                    data = self.tcpClientSock.recv(BUFSIZE)
                    # print("data received: {}".format(data))
                    total_data += data
                    if not total_data:
                        break
                    elif total_data == 'On':
                        toggle_led(total_data)
                        total_data = ""
                        print("LED: On")
                    elif total_data == 'Off':
                        toggle_led(total_data)
                        total_data = ""
                        print("LED: Off")
                    
                    # Send CPU temperature
                    temperature = self.get_cpu_temp()
                    self.tcpClientSock.send(temperature)

            except KeyboardInterrupt:
                toggle_led("Off")
                GPIO.cleanup()
    
    def get_cpu_temp(self):
        self.cpu_temp = os.popen(CPU_TEMP_CMD).read()
        return self.cpu_temp[5:9]
        
    def clean(self):
        self.tcpServerSock.close()


def main():
    try:
        setupGPIO()
        control = Controller()
        control.run_server()
    except KeyboardInterrupt:
        control.clean()
    finally:
        control.clean()


if __name__ == '__main__':
    main()
