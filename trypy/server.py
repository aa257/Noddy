import time
import socket

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind(('', 20000))
s.listen(5)

while True:
 c, addr = s.accept()
 c.send(b'HTTP/1.1 200 OK\n')
 c.send(b'Content-Type: text/html; charset=utf-8\n')
 c.send(b'Content-Length: 51\n')
 c.send(b'\n')
 c.send(b'<html><body>\n')
 c.send(b'<h1>Hello, World!</h1>\n')
 c.send(b'</body></html>\n')
 time.sleep(5)
 c.close()
s.close()
