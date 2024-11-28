#include "Main.hpp"

int main() {
    std::string rep="";
    std::cout << "Bonjour voulez vous etre un client(c) ou un host(h) ?\n>";
    std::cin >> rep;
    while(rep!="c" || rep !="h"){    std::cout << "essayer encore: client(c) ou un host(h) ?\n>";
    if(rep=="c"){
        std::cout << "Vous avez selectioné Client, Création en cours . . .";
        Client C();//a voir plus tard
        C.start();
    }
    else{
        Host H();
        H.start();
        Client C("127.0.0.1",H.getport());// un truc dans ce style
        C.start();
    }
}

    return 0;
}