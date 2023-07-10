import java.io.Serializable;

public class Transaction implements Serializable
{
    final String Date;
    final String Reason;

    public Transaction(String date, String reason)
    {
        Reason = reason;
        Date = date;
    }
}
