package game;

import java.awt.Color;
import java.util.Collection;

public class Mage extends GameCharacter {
    public Mage(String name) {
        super(name, 80);
    }

    @Override public String getType() { return "Mage"; }
    @Override public Color getColor() { return new Color(80, 130, 240); }
    @Override public String getDescription() { return "Fragile but powerful. Lightning Storm hits very hard."; }

    @Override
    public String attack(GameCharacter target) {
        return hit(target, rand.nextInt(16) + 20, "casts a fireball at");
    }

    @Override
    public String defend() {
        guard();
        return getName() + " creates a magic barrier! (next hit halved)";
    }

    @Override
    protected String performSpecial(GameCharacter target, Collection<GameCharacter> allies) {
        return hit(target, rand.nextInt(16) + 30, "calls Lightning Storm on");
    }
}