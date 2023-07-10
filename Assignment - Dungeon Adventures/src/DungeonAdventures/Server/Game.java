package DungeonAdventures.Server;



import DungeonAdventures.InteractionProtocol;
import VitoBarra.JavaUtil.ColoredOutput.Color;
import VitoBarra.JavaUtil.ColoredOutput.ColoredStringHelper;
import VitoBarra.JavaUtil.ColoredOutput.ColoredWriter;
import VitoBarra.JavaUtil.String.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Game implements Runnable
{
    private int TimePlayed = 1;
    Socket Client;
    ColoredWriter ClientWriter;
    BufferedReader ClientReader;
    Player Player;
    Creature Monster;
    boolean GameEnded = false;

    public Game(Socket _client)
    {
        Client = _client;
        GenerateCreature();

        try
        {
            ClientWriter = new ColoredWriter(Client.getOutputStream());
            ClientReader = new BufferedReader(new InputStreamReader(Client.getInputStream()));
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run()
    {
        while (!GameEnded)
        {
            PlayerSelectAction();

            if (!Monster.IsAlive())
                WinScene();
            else if (!Player.IsAlive())
                LoseScene();

        }
        CloseConnection();
    }


    private void PlayerSelectAction()
    {
        ClientWriter.println("|||  your Health: " + ColoredStringHelper.TextInColor(Player.HealthLevel + "/" + Player.MaxHealth, Color.Green) +
                " - Potion: " + ColoredStringHelper.TextInColor(Player.Potion + "/" + Player.MaxPotion, Color.Red) +
                "  ||  Monster Health: " + ColoredStringHelper.TextInColor(Monster.HealthLevel + "/" + Monster.MaxHealth, Color.Yellow) + "  |||");

        ClientWriter.println("1: combatti\n2: bevi pozione\n3: esci del gioco ");

        boolean InvalidCommand;
        ClientWriter.Clear();
        do
        {
            InvalidCommand = false;
            switch (GetCoGetCommand())
            {
                case 1 ->
                {
                    PlayerCombatTurn();
                    if (Monster.IsAlive())
                        MonsterTurn();
                }
                case 2 ->
                {
                    ClientWriter.println(Player.Name + " Recovered " + Player.DrinkPotion() + " Health Point", Color.Green);
                    MonsterTurn();
                }
                case 3 ->
                {
                    LoseScene();
                }
                default ->
                {
                    ClientWriter.println("Comando inesistente");
                    InvalidCommand = true;
                }
            }
        } while (InvalidCommand);

    }

    private void LoseScene()
    {
        ClientWriter.println("--------HAI PERSO--------", Color.Red);
        GameEnded = true;
    }

    private void WinScene()
    {
        ClientWriter.println("--------HAI VINTO--------", Color.Purple);
        GameEnded = !WantToRestart();
    }

    private void CloseConnection()
    {
        try
        {
            Client.close();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        ClientWriter = null;
        ClientReader = null;
    }


    private void PlayerCombatTurn()
    {
        TakeDamage(Monster, Color.Yellow);
    }

    private void MonsterTurn()
    {
        TakeDamage(Player, Color.Red);
    }

    private void TakeDamage(Creature creature, Color c)
    {
        ClientWriter.println(creature.Name + " taken " + creature.TakeDamage() + " damage", c);
    }

    private int GetCoGetCommand()
    {
        int command = -1;
        boolean IsCommandValid;

        do
        {
            ClientWriter.println("cosa vuoi fare>" + InteractionProtocol.ServerWaitForInputCommand);

            String StrCommand = ReadCommand();

            try
            {
                command = Integer.parseInt(StrCommand);
                IsCommandValid = true;
            } catch (NumberFormatException e)
            {
                ClientWriter.println("Comando non valido");
                IsCommandValid = false;
            }

        } while (!IsCommandValid);

        return command;
    }

    private boolean WantToRestart()
    {
        ClientWriter.println("Vuoi rigiocare [y,n]" + InteractionProtocol.ServerWaitForInputCommand);
        var a = ReadCommand();
        if (StringUtil.IsEqualToAny(a, "y", "Y"))
        {
            TimePlayed++;
            GenerateCreature();
            return true;
        }
        return false;

    }

    private String ReadCommand()
    {
        String StrCommand = null;
        try
        {
            StrCommand = ClientReader.readLine();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return StrCommand;
    }

    private void GenerateCreature()
    {
        Player = new Player("Player", 1000, 1000);
        Monster = new Creature("Monster" + TimePlayed, 1000);
    }


}
