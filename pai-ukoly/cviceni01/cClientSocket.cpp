#include "cClientSocket.hpp"

using namespace arg;

bool cClientSocket::OpenClientSocket(const std::string & addr, const unsigned short port)
{
	if (m_SocketOpen)
	{
		err << "This object has already an open socket.\n";
		return false;
	}

	const char * host = addr.c_str();
	m_HostAddr = addr;
	memset((void*) &m_Addr, 0, sizeof(m_Addr));
	m_Addr.sin_addr.s_addr = inet_addr(host);
	if (INADDR_NONE == m_Addr.sin_addr.s_addr)
	{
		m_Host = gethostbyname(host);
		if (NULL == m_Host)
		{
			//cout << WSAGetLastError() << endl;
			err << "Could not get host by name. The reason was " << strerrno() << ".\n";
			return false;
		}
	}
	else
	{
		m_Host = gethostbyaddr((const char*) &m_Addr.sin_addr, sizeof(struct sockaddr_in), AF_INET);
		if (NULL == m_Host)
		{
			//cout << WSAGetLastError() << endl;
			err << "Could not get host by addr. The reason was " << strerrno() << ".\n";
			return false;
		}
	}

	m_ServerSocket = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
	if (INVALID_SOCKET == m_ServerSocket)
	{
		err << "Could not create socket. The reason was " << strerrno() << ".\n";
		return false;
	}
	// setup the rest of our local address
	m_Addr.sin_family = AF_INET;
	m_Addr.sin_addr = *((in_addr*) *m_Host->h_addr_list);
	m_Addr.sin_port = htons(port);

	// a little user interaction... ;)
	dbg	<< "Connecting... \n";

	ssize_t ret = connect(m_ServerSocket, (sockaddr*) &m_Addr, sizeof(struct sockaddr));
	if (SOCKET_ERROR == ret)
	{
		//cout << WSAGetLastError() << endl;
		err << "Cannot connect to server. The reason was " << strerrno() << ".\n";
		closesocket(m_ServerSocket);
		return false;
	}
	dbg	<< "Connected to server socket.\n";
	//	dbg << "Receiving server identification.\n";
	//	char recieved[256 * 256];
	//	ssize_t data_len = recv(m_S, recieved, sizeof(recieved), 0);
	//	if (data_len == 0)
	//	{
	//		dbg	<< "Server closed connection.\n";
	//		return false;
	//	}
	//	else if (data_len == -1)
	//	{
	//		err << "Error receiving data. The reason was " << strerrno() << ".\n";
	//		return false;
	//	}
	//	else
	//	{
	//		recieved[data_len] = 0;
	//		dbg	<< "Connected to \'" << recieved << "\'.\n";
	//	}
	return m_SocketOpen = true;
}

bool cClientSocket::Send(const std::string& payload)
{
	return SendData(m_ServerSocket, payload);
}

std::string cClientSocket::Receive(void)
{
	std::string str_received;
	ReceiveData(m_ServerSocket, str_received);
	return str_received;
}

bool cClientSocket::Close(void)
{
	int r2 = 0;

	if (m_SocketOpen)
	{
		dbg	<< "Closing server socket.\n";
		r2 = closesocket(m_ServerSocket);
		dbg	<< "Socket closed.\n";
		m_SocketOpen = false;
	}
	else
	{
		dbg	<< "Server socket is not open, no need for closing.\n";
	}
	return (r2 == 0);
}
