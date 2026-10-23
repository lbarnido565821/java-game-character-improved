# Java Game Character Battle

A Swing GUI battle game that demonstrates abstraction, ADTs, GUI, and user input.

## Features
- **Abstraction:** `GameCharacter` is an abstract class with abstract methods
  (`attack`, `defend`, `performSpecial`, `getType`, `getColor`, `getDescription`).
- **ADT:** a `List` (ArrayList) holds the party being built; `Queue` (LinkedList)
  manages the heroes and enemies waiting to fight.
- **GUI:** Java Swing window with a setup screen and a battle screen.
- **User input:** character name and class are entered by the user, and the buttons
  Attack / Special / Defend control the battle.
- **Input validation:** empty names, special characters, names over 12 characters,
  duplicate names, and a full party are rejected with an error message.
- **Character types:** Warrior, Mage, Archer (new), Healer (new).
  The Healer's special heals the whole party instead of damaging.
- **Design:** dark theme, color-coded characters, animated health bars that
  change color, and a red flash when a character is hit.

## How to run
1. Install JDK 11 or newer.
2. Put all `.java` files in the same folder (`src`).
3. Compile: `javac *.java`
4. Run: `java Main`
(Or open the project in IntelliJ / VS Code / Eclipse and run `Main.java`.)

## How to play
1. Enter a name, pick a class, and click **Add to Party** (up to 4).
2. Click **Start Battle!**
3. Use **Attack**, **Special** (2 charges per character), or **Defend** (halves the next hit).
4. When a fighter falls, the next one in the queue steps forward.
