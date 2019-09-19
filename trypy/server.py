import socket
import sys
import sqlite3
import _thread

def serve(csok, newLicenseNum, result, results, errors):
 csok.send(result.encode("utf-8"))
 
 csok.send(b"Content-Type: text/html; charset=utf-8\n")
 csok.send(b"\n")
 csok.send(b"<html><head>\n")
 csok.send(b"<meta http-equiv='Cache-Control' content='no-cache, no-store, must-revalidate'/>")
 csok.send(b"<meta http-equiv='Pragma' content='no-cache'/>")
 csok.send(b"<meta http-equiv='Expires' content='0'/>")
 csok.send(b"</head><body>\n")
 csok.send(b"DaSync<br><hr>\n")
 csok.send(b"<br>\n")
 
 for r in results:
  csok.send(r.encode("utf-8"))
  csok.send(b"<br>\n")
 
 csok.send(b"<br><hr><br>\n")
 
 for e in errors:
  csok.send(e.encode("utf-8"))
  csok.send(b"<br>\n")
 
 csok.send(b"<br><hr><br>\n")
 csok.send(b"<form action='/insert' method='get'>")
 csok.send(b"<input type='text' name='license' size='32' maxlength='32' value='")
 
 csok.send(str(newLicenseNum).encode("utf-8"))
 
 csok.send(b"'/><br><input type='submit' value='Insert Value'/></form>\n")
 csok.send(b"<form action='/reset' method='get'><input type='submit' value='Reset Table' /></form>\n")
 csok.send(b"<form action='/list' method='get'><input type='submit' value='List Table' /></form>\n")
 
 csok.sendall(b"</body></html>\n")
 
 csok.close()
 return


def insert(param, dbNameI):
 listingEz = ["insert:", param]
 retValEz  = "HTTP/1.1 200 OK"
 errsEz    = ["success"]

 try:
  dbI = sqlite3.connect(dbNameI)
  cr = dbI.cursor()
  cr.execute("insert into noddysync values(?)", [param]);
  dbI.commit()
  dbI.close()
#leak db connection if error
 except BaseException as ex:
  retValEz = "HTTP/1.1 500 Internal Server Error"
  errsEz = [str(ex)]

 return retValEz, listingEz, errsEz


def reset(dbNameR):
 retValEy  = "HTTP/1.1 200 OK"
 listingEy = ["reset:"]
 errsEy    = []

 try:
  dbR = sqlite3.connect(dbNameR)
  a = dbR.cursor()
  a.execute("drop table noddysync");
 except BaseException as y:
  retValEy = "HTTP/1.1 500 Internal Server Error"
  errsEy.append(str(y))

 try:
  b = dbR.cursor()
  b.execute("create table noddysync(license text)");
  dbR.commit()
  dbR.close()
 except BaseException as z:
  retValEy = "HTTP/1.1 500 Internal Server Error"
  errsEy.append(str(z))

 if len(errsEy) == 0:
  errsEy = ["success"]

 return retValEy, listingEy, errsEy


def list(dbNameL):
 retVal  = "HTTP/1.1 200 OK"
 listing = ["list:"]
 errs    = ["success"]

 try:
  dbL = sqlite3.connect(dbNameL)
  cur = dbL.cursor()
  cur.execute("select license from noddysync")
  for row in cur:
   column = row[0]
   listing.append(column)
  dbL.close()
#leak db connection if error
 except BaseException as x:
  retVal = "HTTP/1.1 500 Internal Server Error"
  errs = [str(x)]

 return retVal, listing, errs


def parse(bytes):
 strung = bytes.decode("utf-8")
 lines = strung.split("\n")
 line = lines[0]
 tokens = line.split(" ")
 token = tokens[1]
 lc = token.lower()

 if lc == "/list":
  return 1, None

 if lc == "/reset":
  return 2, None

 if lc.startswith("/insert?license="):
  inputtedLicense = token[16:]
# don't bother sanitizing http escapes for this toy example
  return 3, inputtedLicense

 return 0, None


def aux(request, license, dbName):
 if request == 1:
  return list(dbName)
 if request == 2:
  return reset(dbName)
 if request == 3:
  return insert(license, dbName)
 return "HTTP/1.1 400 Bad Request", ["bad request"], []


def process(bytesFromSocket, databaseName):
 try:
  request, license = parse(bytesFromSocket)
 except BaseException as x:
  return "HTTP/1.1 400 Bad Request", ["bad request"], [str(x)]
 else:
  return aux(request, license, databaseName)


def serveClient(clientSocket, licenseNum, db):
 inputs = clientSocket.recv(1024)
 resultEx, resultsEx, errorsEx = process(inputs,db)
 serve(clientSocket, licenseNum, resultEx, resultsEx, errorsEx)


#main

port   = int(sys.argv[1])
dbName = sys.argv[2]
Sargs  = ('',port)

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind(Sargs)
s.listen(5)

loopVar = 0
while True:
 c, addr = s.accept()
 threadParams = c, loopVar, dbName
 _thread.start_new_thread(serveClient, threadParams)
 loopVar = loopVar + 1
s.close()
