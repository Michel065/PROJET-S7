#include "ClientThreadHost.hpp"

ClientThreadHost::ClientThreadHost(ServerLink& serv) : serveur(serv) {}


void ClientThreadHost::start() {
    // Créer un thread
    std::thread([this]() {ClientThreadHost::tache
        try {
            while (true) {
                // Exemple : Recevoir un message du serveur
                std::string message = serveur.receiveMessage();
                if (!message.empty()) {
                    std::cout << "Message reçu du serveur : " << message << std::endl;

                    // Exemple : Envoyer une réponse
                    serveur.sendMessage("Message bien reçu !");
                }

                // Simuler une pause dans la boucle pour éviter une surcharge CPU
                std::this_thread::sleep_for(std::chrono::milliseconds(100));
            }
        } catch (const std::exception& e) {
            std::cerr << "Erreur dans le thread : " << e.what() << std::endl;
        }
    }).detach(); // Détache le thread pour qu’il fonctionne indépendamment
}