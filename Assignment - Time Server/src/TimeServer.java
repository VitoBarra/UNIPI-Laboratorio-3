import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Reti e Laboratorio III - A.A. 2022/2023
 * Soluzione dell'undicesimo assignment
 * 
 * Questa classe contiene l'implementazione del TimeServer.
 * Il server esegue un ciclo infinito in cui invia a un gruppo di multicast
 * pacchetti contenenti la data e l'ora corrente. I pacchetti sono inviati
 * a intervalli regolari di 2 secondi.
 * 
 * @author Matteo Loporchio
 */
public class TimeServer {
    // Intervallo di tempo (in ms) fra l'invio di un messaggio e l'altro.
    public static final long waitDelay = 2000;

    public static void main(String[] args) {
        // Leggo i parametri da riga di comando.
        if (args.length < 2) {
            System.err.println("Esegui come: TimeServer <indirizzo> <porta>");
            System.exit(1);
        }
        String address = args[0];
        int port = Integer.parseInt(args[1]);
        // Creo una DatagramSocket per l'invio dei pacchetti.
        try (DatagramSocket socket = new DatagramSocket()) {
            // Ottengo l'indirizzo del gruppo e ne controllo la validita'.
            InetAddress group = InetAddress.getByName(address);
            if (!group.isMulticastAddress()) {
                throw new IllegalArgumentException(
                "Indirizzo multicast non valido: " + group.getHostAddress());
            }
            // Entro in un ciclo infinito in cui invio la data a intervalli regolari.
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            while (true) {
                String message = df.format(new Date(System.currentTimeMillis()));
                byte[] content = message.getBytes();
                DatagramPacket packet = new DatagramPacket(content, content.length, group, port);
                // Invio il pacchetto.
                socket.send(packet);
                System.out.println("Server: " + message);
                // Attendo `waitDelay` millisecondi.
                Thread.sleep(waitDelay);
            }
        }
        catch (Exception e) {
            System.err.println("Errore server: " + e.getMessage());
        }
    }
}
