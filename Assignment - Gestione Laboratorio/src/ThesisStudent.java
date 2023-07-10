import VitoBarra.JavaUtil.Other.Lock1Cond;

public class ThesisStudent extends LaboratoryUser
{
    public ThesisStudent(int times, Tutor tutor, int _positionNeeded,String name, Lock1Cond _l1c)
    {
        super(2,times, tutor,"Thesis Student",name,_l1c);
        Request = new Request(RequestType.Specific,_positionNeeded);
    }
}
