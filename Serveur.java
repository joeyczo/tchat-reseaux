import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

public class Serveur {

    private List<String>            connectes;
    private List<GerantDeClient>    clients;

    public Serveur() {

        this.connectes  = new LinkedList<>();
        this.clients    = new LinkedList<>();

        try {

            ServerSocket ss = new ServerSocket(6000);
            while (true) {

                Socket s = ss.accept();

                GerantDeClient gcg = new GerantDeClient(s, this);

                this.clients.add(gcg);

                Thread tgdc = new Thread(gcg);

                tgdc.start();

            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void envoyerMessage(String s) {

        for (GerantDeClient gdc : this.clients)
            gdc.envoyerMessage(s);

    }

    /**
     * Permet d'ajouter un nouvel utilisateur à la liste des utilisateurs connectés
     * Doit également vérifier que l'utilisateur n'existe pas
     * @param s Nom de l'utilisateur
     * @return true s'il peut se connecter, sinon false
     */
    public boolean ajouterUtilisateur(String s) {

        boolean peutAjouter = true;

        for (String sCo : this.connectes) {
            if (sCo != null && sCo.equals(s)) {
                peutAjouter = false;
                break;
            }
        }

        if (!peutAjouter) return false;

        this.connectes.add(s);

        return true;

    }

    public void deconnecterUtilisateur(String s) {


        for (String so : this.connectes) System.out.print(s + " ,");

        System.out.println("\n");

        this.connectes.remove(s);

        for (String so : this.connectes) System.out.print(s + " ,");

    }
}