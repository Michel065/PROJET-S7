import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ThreadHostConnexion extends Thread  {

    ThreadHostConnexion(int port){

    }
    
    public void run() { 
    try {
			port = Integer.parseInt(args[0]);
			maSocketEcoute = new ServerSocket(port); // creation d'une socket server sur le port d'ecoute "port"

			lesClients = new Vector<PrintWriter>();

			System.out.println("Serveur pret et ecoute sur le port "+port);

			while(true) {				
				Socket clientSocket = maSocketEcoute.accept(); // attente de connexion d'un client (bloquant tant que pas de client)
				//si un client se connecte, alors accept() retourne une socket pour communiquer avec ce client
				System.out.println("Connexion de : " + clientSocket.getInetAddress() + " : port " + clientSocket.getPort());

				//creation et demarrage d'un thread pour dialoguer avec le client
				ServeurTchatThreadService serviceThread = new ServeurTchatThreadService(clientSocket, lesClients);
				serviceThread.start();
			}
		}
		catch (Exception e) { 
			System.err.println("Erreur : "+e);
			e.printStackTrace();
		}
		finally {
			try {
				if(maSocketEcoute!=null) maSocketEcoute.close();
			} catch (IOException e) {
				System.err.println("Erreur : "+e);
				e.printStackTrace();
			}
		}
    }
}
