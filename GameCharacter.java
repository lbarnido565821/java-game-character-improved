package game;

import java.awt.Color;
import java.util.Collection;
import java.util.Random;

public abstract class GameCharacter {
    protected static final Random rand = new Random();

    private final String name;
    private int health;
    private final int maxHealth;
    private int specialCharges = 2;
    private boolean defending = false;

    public GameCharacter(String name, int maxHealth) {
        this.name = name;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
    }

    // ---- Abstract methods: every character type must define these ----
    public abstract String getType();
    public abstract Color getColor();
    public abstract String getDescription();
    public abstract String attack(GameCharacter target);
    public abstract String defend();
    protected abstract String performSpecial(GameCharacter target, Collection<GameCharacter> allies);

    // ---- Concrete methods: inherited by all characters ----
    public final String specialMove(GameCharacter target, Collection<GameCharacter> allies) {
        if (specialCharges <= 0) {
            return name + " has no special charges left!";
        }
        specialCharges--;
        return performSpecial(target, allies);
    }

    public int takeDamage(int amount) {
        if (defending) {
            amount = amount / 2;
            defending = false;
        }
        health = Math.max(0, health - amount);
        return amount;
    }

    public void heal(int amount) {
        health = Math.min(maxHealth, health + amount);
    }

    protected void guard() {
        defending = true;
    }

    protected String hit(GameCharacter target, int damage, String action) {
        int dealt = target.takeDamage(damage);
        return name + " " + action + " " + target.getName() + " for " + dealt + " damage!";
    }

    public boolean isAlive() { return health > 0; }
    public boolean isDefending() { return defending; }
    public boolean canUseSpecial() { return specialCharges > 0; }
    public int getSpecialCharges() { return specialCharges; }
    public String getName() { return name; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
}