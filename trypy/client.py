import socket

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect(('35.246.81.196', 20000))

rx = repr(s.recv(1024))
print(rx)

rx = repr(s.recv(1024))
print(rx)

s.close()
