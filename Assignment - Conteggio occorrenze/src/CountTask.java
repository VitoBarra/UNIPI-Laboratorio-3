import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CountTask implements Runnable
{
    private final ConcurrentHashMap<Character, Integer> CharSet;
    private final File File;

    public CountTask(ConcurrentHashMap<Character, Integer> _charSet, File _file)
    {
        CharSet = _charSet;
        File = _file;
    }

    @Override
    public void run()
    {
        try (Reader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(File))))
        {
            var set = handleCharacters(buffer);

            set.forEach((c, x) -> CountInDictionary(CharSet, c, x));

        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }


    private Map<Character, Integer> handleCharacters(Reader reader) throws IOException
    {
        Map<Character, Integer> Dic = new HashMap<>();

        for (int r = reader.read(); r != -1; r = reader.read())
            if ((r >= 65 && r <= 90) || (r >= 97 && r <= 122))
                CountInDictionary(Dic, (char) r, 1);

        return Dic;
    }

    private static void CountInDictionary(Map<Character, Integer> Dic, char ch, int addon)
    {
        if (!Dic.containsKey(ch))
            Dic.put(ch, addon);
        else
            Dic.replace(ch, Dic.get(ch) + addon);
    }


}
