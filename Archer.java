package game;

import java.awt.Color;
import java.util.Collection;

public class Archer extends GameCharacter {
    public Archer(String name) {
        super(name, 90);
    }

    @Override public String getType() { return "Archer"; }
    @Override public Color getColor() { return new Color(70, 175, 95); }
    @Override public String getDescription() { return "Ranged attacker with steady damage."; }

    @Override
    public String attack(GameCharacter target) {
        return hit(target, rand.nextInt(16) + 12, "fires an arrow at");
    }

    @Override
    public String defend() {
        guard();
        return getName() + " dodges behind a tree! (next hit halved)";
    }

    @Override
    protected String performSpecial(GameCharacter target, Collection<GameCharacter> allies) {
        return hit(target, rand.nextInt(16) + 20, "unleashes Rain of Arrows on");
    }
}