import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CausalCounter implements Runnable
{
    final AccountHolder Account;
    final Lock PrintLock;
    final ConcurrentHashMap<String, Integer> Total;

    public CausalCounter(AccountHolder account, ReentrantLock l, ConcurrentHashMap<String, Integer> o)
    {

        Account = account;
        PrintLock = l;
        Total = o;
    }

    @Override
    public void run()
    {
        var CountDic = new HashMap<String, Integer>();

        for (var s:Account.Records.stream().map(x -> x.Reason).toList())
            SumTransactionCount(s,CountDic,1);

        PrintTransactionNumber(CountDic);

        for (var s : CountDic.keySet())
            SumTransactionCount(s,Total,CountDic.get(s));


    }

    private void SumTransactionCount(String s, Map<String, Integer> CountDic, int IncrementValue)
    {

        var p = CountDic.putIfAbsent(s, IncrementValue);
        if (p != null)
            CountDic.put(s, p + IncrementValue);
    }

    private void PrintTransactionNumber(HashMap<String, Integer> CountDic)
    {
        PrintLock.lock();

        System.out.print(Account.OwnerName + ": ");
        for (var s : CountDic.keySet())
            System.out.print(s + ":" + CountDic.get(s) + " ");
        System.out.print("\n");

        PrintLock.unlock();
    }
}
