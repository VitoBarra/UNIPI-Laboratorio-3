public class Request
{
    public final RequestType RequestType;
    public final Integer PositionNeeded ;


    Request(RequestType re)
    {
        RequestType= re;
        PositionNeeded = null;
    }
    Request(RequestType re,int i)
    {
        RequestType= re;
        PositionNeeded = i;
    }
}
