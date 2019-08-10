#define _WINSOCK_DEPRECATED_NO_WARNINGS

#include <iostream>
#include <winsock2.h>

#pragma comment(lib,"ws2_32.lib") //Winsock Library

void maine(char* hostname)
{
	WSADATA wsa;
	SOCKET s;
	struct sockaddr_in server;

//	char* hostname = (char*)"127.0.0.1";
	char ip[100];
	struct hostent* he;
	struct in_addr** addr_list;
	int i;


	if (WSAStartup(MAKEWORD(2, 2), &wsa) != 0)
		throw("could not initialize winsock");

	s = socket(AF_INET, SOCK_STREAM, 0);
	if (s == INVALID_SOCKET)
		throw("could not create socket");

	he = gethostbyname(hostname);
	if (he == NULL)
		throw("gethostbyname failed");

	//Cast the h_addr_list to in_addr , since h_addr_list also has the ip address in long format only
	addr_list = (struct in_addr**) he->h_addr_list;

	for (i = 0; addr_list[i] != NULL; i++)
	{
		//Return the first one;
		strcpy_s(ip, inet_ntoa(*addr_list[i]));
	}

	server.sin_addr.s_addr = inet_addr(ip);

//	server.sin_addr.s_addr = inet_addr("127.0.0.1");
	server.sin_family = AF_INET;
	server.sin_port = htons(80);

	if (connect(s, (struct sockaddr*) &server, sizeof(server)) < 0)
		throw("could not connect to server");

	char* message = (char*)"GET /noddy.cgi?license=noddy HTTP/1.1\r\n\r\n";
	if (send(s, message, strlen(message), 0) < 0)
		throw("send failed");

	char server_reply[2000];
	int recv_size;

	//pretend to care about reply from the server to delay things long enough that server does not throw exceptions
	recv_size = recv(s, server_reply, 2000, 0);

	if (recv_size == SOCKET_ERROR)
		throw("recv failed");

	Sleep(100);

	closesocket(s);
	WSACleanup();
}

int main(int argc, char* argv[])
{
	try
	{
		maine(argv[1]);
	}
	catch (char* err)
	{
		std::cout << err << std::endl;
		return 1;
	}
	catch (...)
	{
		std::cout << "bad params" << std::endl;
		return 1;
	}

	std::cout << "OK" << std::endl;
	return 0;
}
