package DungeonAdventures.Server;

import java.util.concurrent.ThreadLocalRandom;

public class Creature
{
    protected final String Name;
    protected int HealthLevel;
    protected int MaxHealth;

    public Creature(String _name, int _maximumHealth)
    {
        Name = _name;
        HealthLevel = ThreadLocalRandom.current().nextInt(0, _maximumHealth);
        MaxHealth = HealthLevel;
    }

    public int TakeDamage()
    {
        var damage = ThreadLocalRandom.current().nextInt(0, HealthLevel+1);
        HealthLevel -= damage;

        return damage;

    }

    public boolean IsAlive()
    {
        return HealthLevel > 0;
    }
}
