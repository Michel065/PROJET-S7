#ifndef _CLIENT_H_
#define _CLIENT_H_

#include "ServerLink.hpp"
#include "Structure.hpp"


class Client {
    private:
        //a voir

    public:
        Client(const std::string &ip, int port);
        ~Client();
        start();
};


#endif 
