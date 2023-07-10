import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class AccountHolder implements Serializable
{
    String OwnerName;
    List<Transaction> Records;

    public AccountHolder(String HolderName)
    {
        OwnerName = HolderName;
        Records = new LinkedList<>();
    }

    public void AddTransaction(String date, String reason)
    {
        if (date == null || reason == null) return;
        Records.add(new Transaction(date, reason));
    }
}
