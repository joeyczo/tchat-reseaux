import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class GerantDeClient implements Runnable{

    private Socket  socket;
    private Serveur serv;
    private String  pseudo;

    private PrintWriter     out;
    private BufferedReader  in;

    public GerantDeClient(Socket s, Serveur serv) {

        this.socket = s;
        this.serv   = serv;

        try {

            this.out = new PrintWriter(this.socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public void envoyerMessage(String s) {

        this.out.println(s);

    }

    public void run() {

        System.out.println("Un client s'est connecté");

        try {

            boolean peutSeConnecter = false;

            while (!peutSeConnecter) {

                this.pseudo = this.in.readLine();
                peutSeConnecter = this.serv.ajouterUtilisateur(this.pseudo);
                System.out.println("PSEUDO : " + this.pseudo + " RESULTAT : " + peutSeConnecter);

                if (!peutSeConnecter) this.out.println("Le pseudo est déjà utilisé");
            }

            this.out.println("PSEUDO OK"); // Permets de savoir si le pseudo est disponible pour le client
            this.serv.envoyerMessage(this.pseudo + " s'est connecté");

            boolean bok = true;
            while(bok) { // boucle sans fin

                String msg = this.in.readLine();

                bok = msg != null; // Vérifie si le client est toujours là
                if( !bok )
                {
                    this.serv.envoyerMessage(this.pseudo + " s'est déconnecté");
                }
                else
                {
                    this.serv.envoyerMessage("[" + this.pseudo + "] " + msg );
                }
            }

            this.serv.deconnecterUtilisateur(this.pseudo);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

}
