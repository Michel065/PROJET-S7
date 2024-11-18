#include <iostream>
#include <cstring>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <unistd.h>

class SimpleClient {
private:
    int client_fd;
    struct sockaddr_in server_addr;

public:
    // Constructeur
    SimpleClient(const std::string &ip, int port) : client_fd(-1) {
        client_fd = socket(AF_INET, SOCK_STREAM, 0);
        if (client_fd < 0) {
            perror("Erreur lors de la création de la socket");
        }

        server_addr.sin_family = AF_INET;
        server_addr.sin_port = htons(port);

        if (inet_pton(AF_INET, ip.c_str(), &server_addr.sin_addr) <= 0) {
            perror("Adresse invalide");
            close(client_fd);
            client_fd = -1;
        }
    }

    // Connexion au serveur
    bool connectToServer() {
        if (connect(client_fd, (struct sockaddr *)&server_addr, sizeof(server_addr)) < 0) {
            perror("Erreur lors de la connexion");
            return false;
        }
        std::cout << "Connecté au serveur\n";
        return true;
    }

    // Envoi d'un message
    void sendMessage(const std::string &message) {
        if (client_fd >= 0) {
            send(client_fd, message.c_str(), message.length(), 0);
        }
    }

    // Réception d'un message
    std::string receiveMessage() {
        char buffer[1024] = {0};
        int bytes_received = recv(client_fd, buffer, sizeof(buffer) - 1, 0);
        if (bytes_received > 0) {
            buffer[bytes_received] = '\0';
            return std::string(buffer);
        }
        return "";
    }

    // Fermeture de la connexion
    void disconnect() {
        if (client_fd >= 0) {
            close(client_fd);
        }
    }

    // Destructeur
    ~SimpleClient() {
        disconnect();
    }
};


int main() {
    SimpleClient client("127.0.0.1", 12345);

    if (client.connectToServer()) {
        std::string message="";
        std::string rep="";
        while (message!="q" && rep !="q")
        {
            message = client.receiveMessage();
            std::cout << "Message du serveur : " << message << "\n";
            std::cout << ">";
            std::cin >> rep;
            client.sendMessage(rep);
        }  
    }

    return 0;
}
