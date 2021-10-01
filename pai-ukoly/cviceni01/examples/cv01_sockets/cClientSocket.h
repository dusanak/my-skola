/**
 * \class arg::cClientSocket
 * \brief The client part of our socket interface.
 *
 * Intended use:
 * \code
 * 	 client.OpenClientSocket("localhost", 8888);
 *	 client.Send("Hi.");
 *	 string response = client.Receive();
 * 	 client.Send("Bye.");
 *	 client.Close();
 * \endcode
 *
 * \author Pavel Krömer (c) 2011
 *
 * \version 1.0
 *
 * \b History:
 * 		- 2011-09,	pkromer,	first version
 *
 */

#ifndef CCLIENTSOCKET_H_
#define CCLIENTSOCKET_H_

#include "cSocket.h"

namespace arg
{
	class cClientSocket: public cSocket
	{
		private:
			hostent* m_Host;			///< Server host entry (holds IPs, etc).

		protected:
			std::string m_HostAddr;

		public:
			/**
			 * Connects to a remote server socket.
			 * 		\param[in] addr		- The address of the server (i.e. "localhost").
			 * 		\param[in] port		- The port to connect to.
			 * 		\return	true if success, false otherwise.
			 */
			bool OpenClientSocket(const std::string & addr, const unsigned short port);

			virtual std::string Receive(void);
			virtual bool Send(const std::string & payload);
			virtual bool Close(void);
	};
}

#endif /* CCLIENTSOCKET_H_ */
