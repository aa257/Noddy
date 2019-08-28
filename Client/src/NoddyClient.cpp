#include <iostream>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <unistd.h>

using namespace std;

void maine(char* hostname)
{
	int s;
	struct sockaddr_in server;

//	char* hostname = (char*)"127.0.0.1";
	char ip[100];
	struct hostent* he;
	struct in_addr** addr_list;
	int i;

	s = socket(AF_INET, SOCK_STREAM, 0);
	if (s < 0)
		throw("could not create socket");

	he = gethostbyname(hostname);
	if (he == NULL)
		throw("gethostbyname failed");

	//Cast the h_addr_list to in_addr , since h_addr_list also has the ip address in long format only
	addr_list = (struct in_addr**) he->h_addr_list;

	for (i = 0; addr_list[i] != NULL; i++)
	{
		//Return the first one;
		strcpy(ip, inet_ntoa(*addr_list[i]));
	}

	server.sin_addr.s_addr = inet_addr(ip);

//	server.sin_addr.s_addr = inet_addr("127.0.0.1");
	server.sin_family = AF_INET;
	server.sin_port = htons(20000);

	if (connect(s, (struct sockaddr*) &server, sizeof(server)) < 0)
		throw("could not connect to server");

	char* message = (char*)"GET /noddy.cgi?license=noddy HTTP/1.1\r\n\r\n";
	if (send(s, message, strlen(message), 0) < 0)
		throw("send failed");

	char server_reply[2000];
	int recv_size;

	//pretend to care about reply from the server to delay things long enough that server does not throw exceptions
	recv_size = recv(s, server_reply, 2000, 0);
	if (recv_size < 0)
		throw("recv failed");

	sleep(100);

	close(s);
}

int main(int argc, char* argv[])
{
	try
	{
		maine(argv[1]);
	}
	catch (char* err)
	{
		cout << err << endl;
		return 1;
	}
	catch (...)
	{
		cout << "bad params" << endl;
		return 1;
	}

	cout << "OK" << endl;
	return 0;
}
