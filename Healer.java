package game;

import java.awt.Color;
import java.util.Collection;

public class Healer extends GameCharacter {
    public Healer(String name) {
        super(name, 85);
    }

    @Override public String getType() { return "Healer"; }
    @Override public Color getColor() { return new Color(235, 185, 50); }
    @Override public String getDescription() { return "Weak attack, but Divine Light heals the whole party."; }

    @Override
    public String attack(GameCharacter target) {
        return hit(target, rand.nextInt(6) + 5, "strikes with a holy staff at");
    }

    @Override
    public String defend() {
        guard();
        return getName() + " surrounds themself with a blessing! (next hit halved)";
    }

    // Different behavior: heals allies instead of damaging the enemy
    @Override
    protected String performSpecial(GameCharacter target, Collection<GameCharacter> allies) {
        StringBuilder sb = new StringBuilder(getName() + " uses Divine Light! ");
        for (GameCharacter ally : allies) {
            if (ally.isAlive()) {
                int amount = rand.nextInt(11) + 20;
                ally.heal(amount);
                sb.append(ally.getName()).append(" +").append(amount).append(" HP. ");
            }
        }
        return sb.toString().trim();
    }
}