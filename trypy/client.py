import socket
import sys

arg1 = sys.argv[1]
arg2 = int(sys.argv[2])
args = (arg1, arg2)

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect(args)

rx = repr(s.recv(1024))
print(rx)

rx = repr(s.recv(1024))
print(rx)

s.close()
