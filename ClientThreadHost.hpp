#ifndef _CLIENT_THREAD_HOST_H_
#define _CLIENT_THREAD_HOST_H_

#include "ServerLink.hpp"
#include <thread>

class ClientThreadHost{
private:
    //Game* game
    ServerLink serveur;

public:
    ClientThreadHost(ServerLink& serv);
    tache();
    start();
};


#endif 