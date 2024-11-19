#ifndef _SERVERLINK_H_
#define _SERVERLINK_H_

#include <string>
#include <netinet/in.h>
#include <sys/socket.h> 
#include <arpa/inet.h>  
#include <unistd.h>

class ServerLink {
    private:
        int client_fd;
        struct sockaddr_in server_addr;

    public:
        ServerLink(const std::string &ip, int port) : client_fd(-1);
        ~ServerLink();
        bool connectToServer();
        void sendMessage(const std::string &message);
        std::string receiveMessage();
        void disconnect();
};


#endif 
