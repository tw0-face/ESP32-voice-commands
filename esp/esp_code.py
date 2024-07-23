from machine import Pin
import usocket as socket
import time
import network
import speech_model
import gc


######################################################
timeout = 0
wifi = network.WLAN(network.STA_IF)

wifi.active(False)
time.sleep(0.5)
wifi.active(True)

wifi.connect('ssid_name','P@ssw0rd')


if not wifi.isconnected():
    print('connecting..')
    while (not wifi.isconnected() and timeout < 5):
        print(5 - timeout)
        timeout = timeout + 1
        time.sleep(1)
        
if(wifi.isconnected()):
    print('Connected...')
    print('network config:', wifi.ifconfig())
#############################################

gc.collect()


#######################################################

led = Pin(2, Pin.OUT)

##################################################

server_socket = socket.socket()
server_socket.bind(('192.168.43.176', 5000))   
server_socket.listen(5) 
conn, address = server_socket.accept() 
while True:
    buffer = conn.recv(7168)
    l, prob = speech_model.predict(buffer)
    print(l)
    gc.collect()
    if l == '[OTHER]':
        continue
    label = l
    speech_model.snapshot()
    if label == "ON": 
        led.value(1)
    elif label == "OFF": 
        led.value(0)
    elif label == "BLINK":
        led.value(1)
        time.sleep(1)
        led.value(0)
        time.sleep(1)
conn.close() 
    
