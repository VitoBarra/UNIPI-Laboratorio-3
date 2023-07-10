import VitoBarra.JavaUtil.Other.Lock1Cond;

public class Student extends LaboratoryUser
{

    public Student(int times, Tutor _tutor , String name, Lock1Cond _l1c)
    {
        super(3,times, _tutor,"Student",name,_l1c);
        Request = new Request(RequestType.Any);
    }
}
