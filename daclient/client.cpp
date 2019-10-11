#include <iostream>
#include <stdexcept>

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

class NoddyException : public std::runtime_error {
public:
	NoddyException(const string& msg = "") : runtime_error(msg) {}
};

void get(char* hostname, int myPort, char* license)
{
	int s;
	struct sockaddr_in server;

	char ip[100];
	struct hostent* he;
	struct in_addr** addr_list;
	int i;

	s = socket(AF_INET, SOCK_STREAM, 0);
	if (s < 0)
		throw NoddyException("could not create socket");

	he = gethostbyname(hostname);
	if (he == NULL)
		throw NoddyException("gethostbyname failed");

	//Cast the h_addr_list to in_addr , since h_addr_list also has the ip address in long format only
	addr_list = (struct in_addr**) he->h_addr_list;

	for (i = 0; addr_list[i] != NULL; i++)
	{
		//Return the first one;
		strcpy(ip, inet_ntoa(*addr_list[i]));
	}

	server.sin_addr.s_addr = inet_addr(ip);

	server.sin_family = AF_INET;
	server.sin_port = htons(myPort);

	if (connect(s, (struct sockaddr*) &server, sizeof(server)) < 0)
		throw NoddyException("could not connect to server");

	char* preL  = (char*)"GET /insert?license=";
	char* postL = (char*)" HTTP/1.1\r\n\r\n";

	if (send(s, preL, strlen(preL), 0) < 0)
		throw NoddyException("send failed");

	if (send(s, license, strlen(license), 0) < 0)
		throw NoddyException("send failed");

	if (send(s, postL, strlen(postL), 0) < 0)
		throw NoddyException("send failed");

	//pretend to care about reply from the server to delay things long enough that server does not throw exceptions
	for (;;) {
		char server_reply[2000];
		memset(server_reply, 0, sizeof(server_reply));
		int recv_size = recv(s, server_reply, sizeof(server_reply)-1, 0);
		if (recv_size < 0)
			throw NoddyException("recv failed");
		if (recv_size == 0)
			break;
		// it is assumed here the return is printable for now
		fprintf(stderr, "%s", server_reply);
	}

	close(s);
}

int main(int argc, char* argv[])
{
	if (argc < 4)
	{
		cout << "missing params" << endl;
		return 1;
	}

	char* url     = argv[1];
	int   port    = atoi(argv[2]);

	try
	{
		for (int p = 3; p < argc; p++)
		{
			char* license = argv[p];
			get(url, port, license);
		}
	}
	catch (NoddyException e)
	{
		cout << "NoddyException: " << e.what() << endl;
		return 1;
	}
	catch (...)
	{
		cout << "unknown exception caught" << endl;
		return 1;
	}

	cout << "OK" << endl;
	return 0;
}
