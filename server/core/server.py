import socket
import os
import threading

MAX_LISTENERS = 1
# FILE_NAME = '/sys/devices/platform/vms/coordinates'
FILE_NAME = 'file.txt'
TIME_OUT_ACCEPT = 5


class ServerWork(object):
    def __init__(self, host, port):
        self._host = host
        self._port = port
        self._is_connect = False
        self._socket = None
        self._fd = None

    def run(self, stop_event):
        if not self._is_connect:
            try:
                self._socket = socket.socket()
                self._socket.bind((self._host, self._port))
                self._socket.listen(MAX_LISTENERS)
                self._socket.settimeout(TIME_OUT_ACCEPT)
            except OSError as ex:
                print(ex)
                return -1

            print(self._socket)
            try:
                self._fd = os.open(FILE_NAME, os.O_TRUNC | os.O_WRONLY)
            except FileNotFoundError:
                print('Error: No file to write coordinates')
                self._socket.close()
                return -2

            self._is_connect = True
            while not stop_event.is_set():
                print("Waiting a client.....")
                try:
                    conn, addr = self._socket.accept()
                except Exception:
                    continue
                print("Connect to: ", addr)
                with conn:
                    data = conn.recv(1024)
                    while data and not stop_event.is_set():
                        print("Received data {0} from {1}".format(str(data), addr))
                        os.write(self._fd, data)
                        # os.fsync(fd)
                        data = conn.recv(1024)
                    conn.close()
            os.close(self._fd)
            self._socket.close()
            self._is_connect = False
            print("Server disconnected")
            return 0
        else:
            print("Server already connected")
            return -1

if __name__ == '__main__':
    sw = ServerWork('192.168.43.125', 9000)
    sw.run(threading.Event())
