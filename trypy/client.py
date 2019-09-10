import socket

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect(('127.0.0.1', 20000))
rx = repr(s.recv(1024))
print(rx)
s.close()
