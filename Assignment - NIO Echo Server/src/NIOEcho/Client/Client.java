package NIOEcho.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client
{

    public Client()
    {
        var t = new Thread(this::Connect);
        t.start();
    }

    public void Connect()
    {
        try (var server = new Socket(InetAddress.getLocalHost(), 3838))
        {
            var scanner = new Scanner(System.in);
            var FromServerReader = new BufferedReader(new InputStreamReader(server.getInputStream()));
            var ToServerWriter = new PrintWriter(server.getOutputStream(), true);

            ToServerWriter.println(scanner.nextLine());
            while (true)
            {
                var c = FromServerReader.readLine();
                if (c != null)
                    System.out.println(c);
                else
                    break;

            }

        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

}
