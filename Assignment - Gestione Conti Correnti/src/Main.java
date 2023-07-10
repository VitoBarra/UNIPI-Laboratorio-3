import VitoBarra.JavaUtil.Other.ThreadPoolUtil;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Main
{
    public static void main(String[] args) throws IOException
    {
        var a = Executors.newCachedThreadPool();
        var TotalDic = new ConcurrentHashMap<String,Integer>();
        DeserializeAccount(a,TotalDic);
        ThreadPoolUtil.TryAwaitTermination(a, 40, TimeUnit.SECONDS);

        System.out.print("Total   : ");
        for (var s : TotalDic.keySet())
            System.out.print(s + ":" + TotalDic.get(s) + " ");
        System.out.print("\n");
    }

    private static void DeserializeAccount(ExecutorService ThreadPool,ConcurrentHashMap<String,Integer> o) throws IOException
    {
        var l = new ReentrantLock();

        var re = new JsonReader(new BufferedReader(new FileReader("accounts.json")));
        re.beginArray();
        while (re.hasNext())
        {
            AccountHolder Account = null;
            re.beginObject();
            while (re.hasNext())
            {
                String name = re.nextName();

                if (name.equals("owner"))
                    Account = new AccountHolder(re.nextString());
                else if (Account != null && name.equals("records"))
                {
                    re.beginArray();
                    while (re.hasNext())
                    {
                        String date = null, reason = null;
                        re.beginObject();
                        while (re.hasNext())
                        {
                            String nameRec = re.nextName();
                            if (nameRec.equals("date"))
                                date = re.nextString();
                            if (nameRec.equals("reason"))
                                reason = re.nextString();
                        }
                        re.endObject();
                        Account.AddTransaction(date, reason);
                    }
                    re.endArray();
                }
            }
            re.endObject();
            ThreadPool.execute(new CausalCounter(Account, l,o));
        }
        re.endArray();
    }
}