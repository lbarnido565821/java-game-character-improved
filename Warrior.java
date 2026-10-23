package game;

import java.awt.Color;
import java.util.Collection;

public class Warrior extends GameCharacter {
    public Warrior(String name) {
        super(name, 120);
    }

    @Override public String getType() { return "Warrior"; }
    @Override public Color getColor() { return new Color(210, 70, 70); }
    @Override public String getDescription() { return "Tough fighter. Shield Bash deals heavy damage."; }

    @Override
    public String attack(GameCharacter target) {
        return hit(target, rand.nextInt(11) + 15, "swings a mighty sword at");
    }

    @Override
    public String defend() {
        guard();
        return getName() + " raises a heavy shield! (next hit halved)";
    }

    @Override
    protected String performSpecial(GameCharacter target, Collection<GameCharacter> allies) {
        return hit(target, rand.nextInt(16) + 25, "slams Shield Bash into");
    }
}