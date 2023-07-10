import VitoBarra.JavaUtil.Other.Lock1Cond;

public class Professor extends LaboratoryUser
{

    public Professor(int times,Tutor tutor,String name , Lock1Cond _l1c)
    {
        super(1,times, tutor,"Professor", name,_l1c);
        Request = new Request(RequestType.All);
    }



}

