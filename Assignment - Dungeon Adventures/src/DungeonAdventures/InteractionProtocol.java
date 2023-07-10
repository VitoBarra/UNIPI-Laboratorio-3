package DungeonAdventures;

public class InteractionProtocol
{
    public static String CommandRegex = "#_";
    public static String ServerWaitForInput = "{=}";
    public static String ServerWaitForInputCommand = CommandRegex + "{=}";

    public static String CloseConnection = "{-}";
    public static String CloseConnectionCommand = CommandRegex + "{-}";
}
