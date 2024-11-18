#include <iostream>
#include <cstring>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>

class SimpleServer {
private:
    int server_fd;
    int port;
    struct sockaddr_in server_addr;

public:
    SimpleServer(int port) : server_fd(-1), port(port) {}

    bool start() {
        server_fd = socket(AF_INET, SOCK_STREAM, 0);
        if (server_fd == 0) {
            perror("Erreur lors de la création de la socket");
            return false;
        }

        server_addr.sin_family = AF_INET;
        server_addr.sin_addr.s_addr = INADDR_ANY;
        server_addr.sin_port = htons(port);

        if (bind(server_fd, (struct sockaddr *)&server_addr, sizeof(server_addr)) < 0) {
            perror("Erreur lors du bind");
            close(server_fd);
            return false;
        }

        if (listen(server_fd, 5) < 0) {
            perror("Erreur lors du listen");
            close(server_fd);
            return false;
        }

        std::cout << "Serveur en attente de connexions sur le port " << port << "...\n";
        return true;
    }

    int acceptClient() {
        struct sockaddr_in client_addr;
        socklen_t addr_len = sizeof(client_addr);
        int client_fd = accept(server_fd, (struct sockaddr *)&client_addr, &addr_len);
        if (client_fd < 0) {
            perror("Erreur lors de l'accept");
        } else {
            std::cout << "Nouveau client connecté\n";
        }
        return client_fd;
    }

    bool send(int client_fd, const std::string &message) {
    if (::send(client_fd, message.c_str(), message.length(), 0) < 0) { // Utilisation de ::send
        perror("Erreur lors de l'envoi");
        return false;
    }
    return true;
}

    std::string receive(int client_fd) {
        char buffer[1024] = {0};
        int bytes_received = recv(client_fd, buffer, sizeof(buffer) - 1, 0);
        if (bytes_received > 0) {
            buffer[bytes_received] = '\0';
            return std::string(buffer);
        } else if (bytes_received == 0) {
            std::cout << "Client déconnecté\n";
            return "q"; // Indique une déconnexion
        } else {
            perror("Erreur lors de la réception");
            return "";
        }
    }

    void stop() {
        if (server_fd >= 0) {
            close(server_fd);
        }
    }

    ~SimpleServer() {
        stop();
    }
};


int main() {
    SimpleServer server(12345);

    if (!server.start()) {
        return 1;
    }

    int client_fd = server.acceptClient();
    if (client_fd < 0) {
        return 1;
    }

    std::string message = "Bienvenue sur le serveur !";
    std::string rep;

    server.send(client_fd, message);

    while (message != "q" && rep != "q") {
        message = server.receive(client_fd);
        if (message == "q") break;

        std::cout << "Message du client : " << message << "\n";
        std::cout << ">";
        std::cin >> rep;
        server.send(client_fd, rep);
    }

    close(client_fd);
    return 0;
}
