import socket

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind(('', 20000))
s.listen(5)

while True:
 c, addr = s.accept()
 c.send(b'Thank you for connecting') 
 c.close()
s.close()
