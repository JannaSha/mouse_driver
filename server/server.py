import socket
import os
import threading
MAX_LISTENERS = 5
FILE_NAME = '/sys/devices/platform/vms/coordinates'
# FILE_NAME = 'file.txt'


def start_server(host, port, stop_event):
    try:
        sock = socket.socket()
        sock.bind((host, port))
        sock.listen(MAX_LISTENERS)
    except OSError:
        print('Connect ERROR!')
        return -1

    print(sock)
    try:
        fd = os.open(FILE_NAME, os.O_TRUNC | os.O_WRONLY)
    except FileNotFoundError:
        print('Error: No file to write coordinates')
        sock.close()
        return -2

    while not stop_event.is_set():
        conn, addr = sock.accept()
        print("Connect to: ", addr)
        with conn:
            data = conn.recv(1024)
            while data and not stop_event.is_set():
                print("Received data {0} from {1}".format(str(data), addr))
                os.write(fd, data)
                # os.fsync(fd)
                data = conn.recv(1024)
            conn.close()
    os.close(fd)
    sock.close()
    print("Server disconnected")
    return 0

if __name__ == '__main__':
    start_server('localhost', 9000, threading.Event())
