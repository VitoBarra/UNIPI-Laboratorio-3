import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Reti e Laboratorio III - A.A. 2022/2023
 * Soluzione dell'undicesimo assignment
 * 
 * Questa classe contiene l'implementazione del TimeClient.
 * Il client si unisce ad un gruppo di multicast, riceve 10 messaggi
 * provenienti dal server, li stampa su schermo e termina.
 * 
 * @author Matteo Loporchio
 */
public class TimeClient {
    // Dimensione del buffer per la ricezione dei messaggi.
    public static final int size = 1024;
    // Numero di messaggi da ricevere.
    public static final int count = 10;

    public static void main(String[] args) {
        // Leggo i parametri da riga di comando.
        if (args.length < 2) {
            System.err.println("Esegui come: TimeClient <indirizzo> <porta>");
            System.exit(1);
        }
        String address = args[0];
        int port = Integer.parseInt(args[1]);
        // Apro una MulticastSocket per la ricezione dei messaggi.
        try (MulticastSocket socket = new MulticastSocket(port)) {
            // Ottengo l'indirizzo del gruppo e ne controllo la validità.
            InetAddress group = InetAddress.getByName(address);
            if (!group.isMulticastAddress()) {
                throw new IllegalArgumentException(
                "Indirizzo multicast non valido: " + group.getHostAddress());
            }
            // Mi unisco al gruppo multicast.
            socket.joinGroup(group);
            // Ricevo `count` messaggi dal server prima di terminare.
            for (int i = 0; i < count; i++) {
                DatagramPacket packet = new DatagramPacket(new byte[size], size);
                // Ricevo il pacchetto.
                socket.receive(packet);
                System.out.println("Client: " +
                new String(packet.getData(), packet.getOffset(),
                packet.getLength()));
            }
        }
        catch (Exception e) {
            System.err.println("Errore client: " + e.getMessage());
        }
    }
}
