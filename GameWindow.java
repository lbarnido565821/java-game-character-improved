package game;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class GameWindow extends JFrame {
    private static final Color BG = new Color(25, 25, 40);
    private static final Color PANEL = new Color(40, 40, 65);
    private static final Color GOLD = new Color(240, 190, 60);
    private static final int MAX_PARTY = 4;
    private static final int ATTACK = 0, SPECIAL = 1, DEFEND = 2;

    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    private final Random rng = new Random();

    // ---- ADTs ----
    private final List<GameCharacter> roster = new ArrayList<>();  // List: the party being built
    private Queue<GameCharacter> heroes;                           // Queue: heroes waiting to fight
    private Queue<GameCharacter> enemies;                          // Queue: enemies waiting to fight

    // Setup screen widgets
    private JTextField nameField;
    private JComboBox<String> typeBox;
    private JLabel descLabel;
    private JLabel errorLabel;
    private DefaultListModel<String> listModel;
    private JList<String> rosterList;

    // Battle screen widgets
    private CharacterCard heroCard, enemyCard;
    private JTextArea logArea;
    private JLabel statusLabel;
    private JButton attackBtn, specialBtn, defendBtn;

    public GameWindow() {
        setTitle("Game Character Battle");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        root.add(buildSetupPanel(), "setup");
        root.add(buildBattlePanel(), "battle");
        add(root);
        setSize(720, 600);
        setLocationRelativeTo(null);
    }

    // ================= SETUP SCREEN =================
    private JPanel buildSetupPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = makeLabel("Build Your Party", 28, GOLD);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(title, BorderLayout.NORTH);

        // Form row
        JPanel form = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        form.setBackground(BG);
        nameField = new JTextField(12);
        typeBox = new JComboBox<>(new String[]{"Warrior", "Mage", "Archer", "Healer"});
        JButton addBtn = new JButton("Add to Party");
        styleButton(addBtn, new Color(60, 150, 90));
        form.add(makeLabel("Name:", 14, Color.WHITE));
        form.add(nameField);
        form.add(makeLabel("Class:", 14, Color.WHITE));
        form.add(typeBox);
        form.add(addBtn);

        descLabel = makeLabel(" ", 13, new Color(200, 200, 220));
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorLabel = makeLabel(" ", 13, new Color(255, 100, 100));
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBackground(BG);
        for (JComponent c : new JComponent[]{form, descLabel, errorLabel}) {
            c.setAlignmentX(Component.CENTER_ALIGNMENT);
            top.add(c);
        }

        // Roster list
        listModel = new DefaultListModel<>();
        rosterList = new JList<>(listModel);
        rosterList.setBackground(PANEL);
        rosterList.setForeground(Color.WHITE);
        rosterList.setFont(new Font("SansSerif", Font.PLAIN, 15));
        rosterList.setSelectionBackground(GOLD);
        rosterList.setSelectionForeground(Color.BLACK);
        JScrollPane scroll = new JScrollPane(rosterList);
        scroll.setBorder(BorderFactory.createLineBorder(GOLD, 2));

        JPanel center = new JPanel(new BorderLayout(5, 5));
        center.setBackground(BG);
        center.add(top, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);
        p.add(center, BorderLayout.CENTER);

        // Bottom buttons
        JButton removeBtn = new JButton("Remove Selected");
        JButton startBtn = new JButton("Start Battle!");
        styleButton(removeBtn, new Color(150, 70, 70));
        styleButton(startBtn, new Color(70, 110, 200));
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        bottom.setBackground(BG);
        bottom.add(removeBtn);
        bottom.add(startBtn);
        p.add(bottom, BorderLayout.SOUTH);

        // Events
        addBtn.addActionListener(e -> addCharacter());
        nameField.addActionListener(e -> addCharacter());   // Enter key
        removeBtn.addActionListener(e -> removeSelected());
        startBtn.addActionListener(e -> startBattle());
        typeBox.addActionListener(e ->
            descLabel.setText(createCharacter("x", (String) typeBox.getSelectedItem()).getDescription()));
        descLabel.setText(createCharacter("x", "Warrior").getDescription());

        return p;
    }

    private GameCharacter createCharacter(String name, String type) {
        switch (type) {
            case "Warrior": return new Warrior(name);
            case "Mage":    return new Mage(name);
            case "Archer":  return new Archer(name);
            default:        return new Healer(name);
        }
    }

    // ---- User input with validation ----
    private void addCharacter() {
        String name = nameField.getText().trim();

        if (name.isEmpty()) { showError("Please enter a name."); return; }
        if (!name.matches("[A-Za-z0-9 ]+")) { showError("Use only letters, numbers and spaces."); return; }
        if (name.length() > 12) { showError("Name must be 12 characters or less."); return; }
        for (GameCharacter c : roster) {
            if (c.getName().equalsIgnoreCase(name)) { showError("That name is already used."); return; }
        }
        if (roster.size() >= MAX_PARTY) { showError("Party is full (max " + MAX_PARTY + ")."); return; }

        GameCharacter c = createCharacter(name, (String) typeBox.getSelectedItem());
        roster.add(c);
        listModel.addElement(c.getName() + "  -  " + c.getType() + "  (HP " + c.getMaxHealth() + ")");
        nameField.setText("");
        errorLabel.setText(" ");
        nameField.requestFocusInWindow();
    }

    private void removeSelected() {
        int index = rosterList.getSelectedIndex();
        if (index == -1) { showError("Select a character to remove first."); return; }
        roster.remove(index);
        listModel.remove(index);
        errorLabel.setText(" ");
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
    }

    // ================= BATTLE SCREEN =================
    private JPanel buildBattlePanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        statusLabel = makeLabel(" ", 13, GOLD);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(statusLabel, BorderLayout.NORTH);

        heroCard = new CharacterCard();
        enemyCard = new CharacterCard();
        JPanel arena = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        arena.setBackground(BG);
        arena.add(heroCard);
        arena.add(makeLabel("VS", 26, GOLD));
        arena.add(enemyCard);
        p.add(arena, BorderLayout.CENTER);

        logArea = new JTextArea(6, 40);
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setBackground(new Color(15, 15, 28));
        logArea.setForeground(Color.WHITE);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createLineBorder(GOLD, 2));

        attackBtn = new JButton("Attack");
        specialBtn = new JButton("Special");
        defendBtn = new JButton("Defend");
        styleButton(attackBtn, new Color(200, 70, 70));
        styleButton(specialBtn, new Color(130, 80, 200));
        styleButton(defendBtn, new Color(70, 110, 200));
        attackBtn.addActionListener(e -> playerAction(ATTACK));
        specialBtn.addActionListener(e -> playerAction(SPECIAL));
        defendBtn.addActionListener(e -> playerAction(DEFEND));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttons.setBackground(BG);
        buttons.add(attackBtn);
        buttons.add(specialBtn);
        buttons.add(defendBtn);

        JPanel south = new JPanel(new BorderLayout(5, 5));
        south.setBackground(BG);
        south.add(logScroll, BorderLayout.CENTER);
        south.add(buttons, BorderLayout.SOUTH);
        p.add(south, BorderLayout.SOUTH);

        return p;
    }

    private void startBattle() {
        if (roster.isEmpty()) { showError("Add at least one character first."); return; }

        heroes = new LinkedList<>(roster);
        enemies = new LinkedList<>();
        int enemyCount = Math.min(3, roster.size() + 1);
        GameCharacter[] pool = {
            new Warrior("Orc Brute"), new Mage("Dark Mage"), new Archer("Skeleton Archer")
        };
        for (int i = 0; i < enemyCount; i++) enemies.add(pool[i]);

        logArea.setText("");
        log("Battle begins! The front fighter keeps fighting until defeated.");
        setButtonsEnabled(true);
        refreshBattle();
        cards.show(root, "battle");
    }

    private void playerAction(int action) {
        GameCharacter hero = heroes.peek();
        GameCharacter enemy = enemies.peek();
        if (hero == null || enemy == null) return;

        String msg;
        if (action == ATTACK) {
            msg = hero.attack(enemy);
            enemyCard.flash();
        } else if (action == SPECIAL) {
            if (!hero.canUseSpecial()) {
                log(hero.getName() + " has no special charges left!");
                return;
            }
            msg = hero.specialMove(enemy, heroes);
            if (!(hero instanceof Healer)) enemyCard.flash();
        } else {
            msg = hero.defend();
        }
        log(msg);

        if (!enemy.isAlive()) {
            log(enemy.getName() + " is defeated!");
            enemies.poll();                       // remove from the queue
            if (enemies.isEmpty()) {
                refreshBattle();
                endGame(true);
                return;
            }
            log(enemies.peek().getName() + " steps forward!");
            refreshBattle();
            return;
        }
        enemyTurn();
    }

    private void enemyTurn() {
        GameCharacter enemy = enemies.peek();
        GameCharacter hero = heroes.peek();
        int roll = rng.nextInt(100);
        String msg;
        boolean attacked = true;

        if (roll < 25 && enemy.canUseSpecial()) {
            msg = enemy.specialMove(hero, enemies);
        } else if (roll < 35) {
            msg = enemy.defend();
            attacked = false;
        } else {
            msg = enemy.attack(hero);
        }
        log(msg);
        if (attacked) heroCard.flash();

        if (!hero.isAlive()) {
            log(hero.getName() + " has fallen!");
            heroes.poll();
            if (heroes.isEmpty()) {
                refreshBattle();
                endGame(false);
                return;
            }
            log(heroes.peek().getName() + " steps forward!");
        }
        refreshBattle();
    }

    private void refreshBattle() {
        GameCharacter hero = heroes.peek();
        heroCard.setCharacter(hero);
        enemyCard.setCharacter(enemies.peek());

        StringBuilder order = new StringBuilder("Party queue: ");
        for (GameCharacter c : heroes) {
            order.append(c.getName()).append("  >  ");
        }
        statusLabel.setText(order + "   |   Enemies left: " + enemies.size());

        if (hero != null) {
            specialBtn.setText("Special (" + hero.getSpecialCharges() + ")");
        }
    }

    private void endGame(boolean won) {
        setButtonsEnabled(false);
        log(won ? "*** VICTORY! ***" : "*** DEFEAT... ***");
        JOptionPane.showMessageDialog(this,
            won ? "Victory! Your party won the battle." : "Defeat... Your party was wiped out.");
        roster.clear();
        listModel.clear();
        errorLabel.setText(" ");
        cards.show(root, "setup");
    }

    // ================= HELPERS =================
    private void log(String text) {
        logArea.append(text + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void setButtonsEnabled(boolean on) {
        attackBtn.setEnabled(on);
        specialBtn.setEnabled(on);
        defendBtn.setEnabled(on);
    }

    private JLabel makeLabel(String text, int size, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, size));
        l.setForeground(color);
        return l;
    }

    private void styleButton(JButton b, Color color) {
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
    }
}