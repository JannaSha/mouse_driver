import os
import sys
import random
import time


DEVICE_FILENAME = "/sys/devices/platform/my_mouse/coordinates"
#DEVICE_FILENAME = "r.txt"


def main():
    fd = os.open(DEVICE_FILENAME, os.O_RDWR)
    # TODO: errors

    for _ in range(150):
        x = random.randint(0, 50)
        y = random.randint(0, 50)

        if x % 2 == 0:
            x = -x

        if y % 2 == 0:
            y = -y

        cmd = "%d %d" % (x, y)
        os.write(fd, bytes(cmd, encoding='utf-8'))
        # os.fsync(fd)
        time.sleep(1)

    os.fsync(fd)
    os.close(fd)



if __name__ == '__main__':
    main()
    exit(1)