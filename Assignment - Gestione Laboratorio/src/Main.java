
import VitoBarra.JavaUtil.Other.Lock1Cond;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Random;

public class Main
{
    public static void main(String[] args) throws InterruptedException
    {


        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        int studenti, Tesisti, Professori;
        try
        {
            System.out.println("Student number:");
            studenti = Integer.parseInt(reader.readLine());
            System.out.println("ThesisStudent number:");
            Tesisti = Integer.parseInt(reader.readLine());
            System.out.println("Professor number:");
            Professori = Integer.parseInt(reader.readLine());

        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        Tutor tutor = new Tutor(new Laboratory());
        Random rand = new Random();
        var Users = new LinkedList<LaboratoryUser>();
        var lock1Cond = new Lock1Cond();

        for (int i = 0; i < studenti; i++)
            Users.add(new Student(rand.nextInt(1, 11), tutor, Integer.toString(i), lock1Cond));
        for (int i = 0; i < Tesisti; i++)
            Users.add(new ThesisStudent(rand.nextInt(1, 11), tutor, rand.nextInt(20), Integer.toString(i), lock1Cond));
        for (int i = 0; i < Professori; i++)
            Users.add(new Professor(rand.nextInt(1, 11), tutor, Integer.toString(i), lock1Cond));

        var GUI = new LaboratoryGUI("Laboratory");
        Users.forEach(LaboratoryUser::MakeRequest);

        WaitUserFinish(Users, lock1Cond);


        tutor.Close();
        GUI.CloseGUI();
    }

    private static void WaitUserFinish(LinkedList<LaboratoryUser> Users, Lock1Cond l1c)
    {
        int FinishedUser = 0;
        while (!Users.stream().allMatch(x -> x.UseTimes == 0))
        {
            l1c.Wait();
            if (++FinishedUser > 10)
            {
                var Unfinished = Users.stream().filter(x -> x.UseTimes != 0).toList();
                Users.clear();
                Users.addAll(Unfinished);
                FinishedUser = 0;
            }

        }
    }
}


