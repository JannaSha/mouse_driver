import socket
import os
import time


PORT = 9010
BLOCK_SIZE = 1024
HOST = 'localhost'# '192.168.0.100'

socket_t = socket.socket()
host = socket.gethostname()
socket_t.connect((HOST, PORT))

is_work = True
while is_work:
    data = '6 0 0\n'
    socket_t.send(data.encode())
    print('Done sending!')
    time.sleep(10)



