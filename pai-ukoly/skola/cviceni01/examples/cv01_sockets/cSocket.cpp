#include "stdafx.h"

#include "cSocket.h"

using namespace arg;

cSocket::cSocket(void)
{
	m_Len = sizeof(m_Addr); // the length of our remote address
	m_SocketOpen = false;
}

bool cSocket::ReceiveData(const SOCKET & s, std::string & data)
{
	bool success = true;
	data.clear();
	dbg	<< "Waiting for data.\n";
	char received[256 * 256];
	ssize_t data_length = recv(s, received, sizeof(received), 0);
	if (data_length == 0)
	{
		dbg	<< "The client disconnected. No data received.\n";
	}
	else if (data_length == -1)
	{
		err << "Error receiving data. The reason was " << strerrno() << ".\n";
		success = false;
	}
	else
	{
		received[data_length] = 0;
		//dbg	<< "Receiving succeeded. The client sent " << received << ".\n";
		dbg << "Receiving succeeded.\n";
		data = received;
	}
	return success;
}

bool cSocket::SendData(const SOCKET & socket, const std::string & data)
{
	if (!m_SocketOpen)
	{
		err << "A connection must be opened before data can be sent.\n";
		return false;
	}

	dbg	<< "Sending \'" << data << "\'.\n";
	ssize_t data_length = send(socket, data.c_str(), data.size(), 0);

	if (data_length != (unsigned int) data.size())
	{
		err << "Error sending data. The reason was " << strerrno() << ".\n";
		return false;
	}
	return true;
}

cSocket::~cSocket(void)
{
	Close();
}
