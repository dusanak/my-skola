/**
 * \class arg::cSocket
 * \brief An abstract base class for simple socket based communication interface.
 *
 * Taken from a web tutorial at http://www.energonsoftware.org/tutorials/socket.
 * The class should be portable (not tested yet) across Linux and Winsock.
 *
 * Do not forget starting and stopping winsock on Windows:
 * \code
	#if defined WIN32
 		WSADATA wsa_data;
 		WSAStartup(MAKEWORD(1,1), &wsa_data);
 	#endif
 		...
 	#if defined WIN32
 		WSACleanup();
 	#endif
 * \endcode
 *
 * Also, the sockets should be closed on interrupt or when the program is killed.
 * I am using signals to do so:
 *
 * \code
 	cServerSocket server;
 	cClientSocket client;

	void terminator(int sig)
	{
		cout << "Ctrl-C detected. Closing socket." << endl;
		server.Close();
		client.Close();
		exit(0);
	}

	...

	signal(SIGABRT, &terminator);
	signal(SIGTERM, &terminator);
	signal(SIGINT, &terminator);
 * \endcode
 *
 *
 * \author Pavel Krömer (c) 2011
 *
 * \version 1.0
 *
 * \b History:
 * 		- 2011-09,	pkromer,	first version
 *
 */
#ifndef CSOCKET_H_
#define CSOCKET_H_

#include <cstring>
#include <cstdio>
#include <string>
#include <iostream>

//#include "arg/core/cDebuggable.h"

#define err std::cout
#define dbg std::cout
//#define strerrno() std::strerror(errno)
#define strerrno() WSAGetLastError()


/** Include headers based on OS. */
#if defined(_MSC_VER)
	#include <winsock.h>  				///< WinSock subsystem.
	#include <BaseTsd.h>
	typedef SSIZE_T ssize_t;

	#pragma comment(lib, "Ws2_32.lib")

#elif defined __linux__
	#include <unistd.h>
	#include <netdb.h>
	#include <sys/types.h>
	#include <sys/socket.h>
	#include <arpa/inet.h>
#endif

/** Redefine some types and constants based on OS. */
#if defined(_MSC_VER)
	typedef int socklen_t; 				///< Unix socket length	
#elif defined __linux__
	typedef int SOCKET;
	#define INVALID_SOCKET -1  			///< WinSock invalid socket.
	#define SOCKET_ERROR   -1  			///< basic WinSock error.
	#define closesocket(s) close(s);  	///< Unix uses file descriptors, WinSock doesn't...
#endif

namespace arg
{
	class cSocket// : public cDebuggable
	{
		protected:
			struct sockaddr_in m_Addr;					///< Address variable.
			socklen_t m_Len; 							///< The length of remote address.
			SOCKET m_ServerSocket;
			bool m_SocketOpen;

			/**
			 * Receive data from a socket.
			 * 		\param[in] 	socket	- the socket to read from. It must be open.
			 * 		\param[out]	data	- the container for data. It will be empty if an error happens.
			 * 		\return true or false (success or error).
			 */
			bool ReceiveData(const SOCKET & socket, std::string & data);

			/**
			 * Send data to a socket.
			 * 		\param[in] 	socket	- the socket to write into. It must be open.
			 * 		\param[in]	data	- the data to send.
			 * 		\return true or false (success or error).
			 */
			bool SendData(const SOCKET & socket, const std::string & data);

		public:
			cSocket(void);

			virtual std::string Receive(void) = 0;					///< Read data from socket.
			virtual bool Send(const std::string & payload) = 0;		///< Write data to socket.
			virtual bool Close(void){return false;};				///< Cleanup sockets.

			virtual ~cSocket(void);
	};
}

#endif /* CSOCKET_H_ */
