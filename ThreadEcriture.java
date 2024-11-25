import java.io.PrintWriter;
import java.net.Socket;

import iut.algo.Clavier;

public class ThreadEcriture implements Runnable {

    private Socket      socket;
	private PrintWriter out;

    public ThreadEcriture(Socket socket) 
	{
        this.socket = socket;

        try 
		{
           this.out = new PrintWriter(this.socket.getOutputStream(), true);
        } 
		catch (Exception e) 
		{
            e.printStackTrace();
        }
    }

    public void run() {

		String messageSortant;

        while (true) {

			messageSortant = Clavier.lireString();
			this.out.println(messageSortant);

        }

    }
}