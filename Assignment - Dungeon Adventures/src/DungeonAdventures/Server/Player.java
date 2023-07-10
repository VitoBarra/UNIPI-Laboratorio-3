package DungeonAdventures.Server;

import java.util.concurrent.ThreadLocalRandom;

public class Player extends  Creature
{
    int Potion;
    int MaxPotion;
    Player(String name,int maximumHealth,int MaximumPotion)
    {
        super(name,maximumHealth);
        Potion = ThreadLocalRandom.current().nextInt(0,MaximumPotion);
        MaxPotion  = Potion;
    }

    public int DrinkPotion()
    {
        int PotionDrank = ThreadLocalRandom.current().nextInt(0,Potion);
        HealthLevel+= PotionDrank;
        Potion -= PotionDrank;
        return PotionDrank;
    }
}
