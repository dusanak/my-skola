// cv01_sockets.cpp: Definuje vstupní bod pro konzolovou aplikaci.
//

#include "stdafx.h"

#include "cClientSocket.h"

using namespace std;


int main()
{
#if defined WIN32
	WSADATA wsa_data;
	WSAStartup(MAKEWORD(1, 1), &wsa_data);
#endif

	//URL: http://name-service.appspot.com/api/v1/names/0.xml

	string host = "name-service.appspot.com";
	string addr = "/api/v1/names/0.xml";

	string get_http = "GET " + addr + " HTTP/1.1\r\nHost: " + host + "\r\nConnection: close\r\n\r\n";

	arg::cClientSocket client;
	if (!client.OpenClientSocket(host, 80)) {
		cout << "Not connected!" << endl;
		return 0;
	}

	client.Send(get_http);
	string response = client.Receive();
	cout << "Response: " << endl << endl << response << endl;
	client.Close();

#if defined WIN32
	WSACleanup();
#endif
	
	return 0;
}

