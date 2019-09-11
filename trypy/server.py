import time
import socket
import sys
import sqlite3

def serve(csok, license):
 csok.send(b"HTTP/1.1 200 OK\n")
 csok.send(b"Content-Type: text/html; charset=utf-8\n")
 csok.send(b"\n")
 csok.send(b"<html><head>\n")
 csok.send(b"<meta http-equiv='Cache-Control' content='no-cache, no-store, must-revalidate'/>")
 csok.send(b"<meta http-equiv='Pragma' content='no-cache'/>")
 csok.send(b"<meta http-equiv='Expires' content='0'/>")
 csok.send(b"</head><body>\n")
 csok.send(b"DaSync<br><hr>\n")
 csok.send(b"<br>\n")
 csok.send(b"result1\n")
 csok.send(b"<br>\n")
 csok.send(b"result2\n")
 csok.send(b"<br><hr>")
 csok.send(b"<br>\n")
 csok.send(b"error1\n")
 csok.send(b"<br>\n")
 csok.send(b"error2\n")
 csok.send(b"DaSync<br><hr><br>\n")
 csok.send(b"<form action='/insert' method='get'>")
 csok.send(b"<input type='text' name='license' size='32' maxlength='32' value='")
 csok.send(license)
 csok.send(b"'/><br><input type='submit'/></form>\n")
 csok.send(b"<form action='/reset' method='get'><input type='submit' value='Reset Table' /></form>\n")
 csok.send(b"<form action='/list' method='get'><input type='submit' value='List Table' /></form>\n")
 csok.sendall(b"</body></html>\n")
 csok.close()
 return


arg1 = int(sys.argv[1])
arg2 = sys.argv[2]

con = sqlite3.connect(arg2)
cur = con.cursor()

cur.execute("select license from noddysync")
for row in cur: 
 for column in row:
  print(column)

con.commit()
con.close()

Sargs = ('',arg1)

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind(Sargs)
s.listen(5)

while True:
 c, addr = s.accept()
 data = c.recv(1024)
 serve(c, b"Hegegegeh!")
s.close()
