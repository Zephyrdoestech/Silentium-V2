# SILENTIUM 🎵🚫

> *"The shield is down, and the world is louder for it."*

**Silentium** is a turn-based RPG implemented in **Java**. Set in a world swallowed by eternal silence, players control a group of musicians who use their instruments not just to play melodies, but to shatter the quiet and defeat the "Shadow Beasts" born from the void.

---

## 📖 Table of Contents
- [About the Game](#about-the-game)
- [Story & Setting](#story--setting)
- [Characters](#characters)
- [Game Mechanics](#game-mechanics)
  - [Combat System](#combat-system)
  - [Metronome System](#metronome-system)
  - [Chord System](#chord-system)
- [Project Structure](#project-structure)
- [Installation & How to Run](#installation--how-to-run)
- [Credits](#credits)

---

## 🎮 About the Game
Silentium combines classic RPG turn-based combat with mathematical logic puzzles. The game emphasizes resource management, calculated risks via the **Metronome System**, and strategic use of musical theory (Chords) to buff allies or debuff enemies.

### Key Features
*   **Three Playable Classes:** Each with unique passive and active skills.
*   **Procedural-style Combat Math:** Damage is calculated based on note ranges and metronome synchronization.
*   **Inventory System:** Collect items like *Crimson Chorus* and *Resolved Dissonance* to turn the tide of battle.
*   **Progression:** Travel through the *Ruined Town of Echoes*, *Silent Caverns*, and the *Abyss of Dissonance*.

---

## 🕯 Story & Setting
Centuries ago, an entity known as the Guardian requested absolute silence to "rest," but this silence birthed monsters known as **Shadow Beasts**. Humanity survived in muted dread until a single Bell rang out, awakening the beasts and shattering the illusion of safety.

Three children—**Sonara**, **Aurelius**, and **Lyron**—discover ancient instruments and realize that sound is the only weapon capable of hurting the silence. They journey into the Abyss to confront **Maestro Syozan**, the entity who seeks to erase sound forever.

---

## 👥 Characters

| Character | Instrument | Role | Special Ability |
| :--- | :--- | :--- | :--- |
| **Sonara** | Banjo | **DPS / Retaliation** | **Body of Thorns:** Reflects true damage back to attackers. |
| **Aurelius** | Flute | **Support / Healer** | **Melodic Remedy:** Heals allies and preserves note damage values. |
| **Lyron** | Harp | **Tank / Risk-Reward** | **Musical Roulette:** Rerolls note damage; gains shields on Metronome sync. |

---

## ⚙️ Game Mechanics

### Combat System
Instead of standard attacks, players input **Notes (A, B, C, D, E, F, G)**.
*   Each note has a damage range (e.g., **A** = 1-10 dmg, **G** = 21-24 dmg).
*   Players select 3 notes per turn.
*   Total damage is calculated based on the sum of these notes, modified by the **Metronome**.

### Metronome System
The **Metronome** is a multiplier (Range: 1 to 4) that changes every turn.
1.  **Synchronization:** If the sum of your selected notes is **divisible** by the current Metronome value:
    *   Damage = `Sum * Metronome`
    *   Metronome value increases by 1 (Caps at 4).
2.  **Dissonance:** If the sum is **not divisible**:
    *   Metronome value decreases by 1.

### Chord System
Inputting specific note combinations grants special effects once per battle:
*   **C Major (C-E-G):** Heals 20% Max HP.
*   **D Minor (D-F-A):** +20% Damage Buff.
*   **F Major (F-A-C):** Gain 25 Shield Points.
*   **B Diminished (B-D-F):** +30% Damage, but lose 10% HP.

---

## 🏗 Project Structure
This project showcases **Object-Oriented Programming (OOP)** principles including Encapsulation, Inheritance, Abstraction, and Polymorphism.

### Package Overview
*   **`Main`**: Entry point (`GameMenu`, `Task`).
*   **`Combat`**: Handles damage logic, metronome math, and turn flow (`ChordSystem`, `Metronome`, `Note`).
*   **`Display`**: Handles UI/UX via console (`AsciiArt`, `CombatDisplay`, `MapDisplay`).
*   **`Enemy`**: Hierarchy of monsters inheriting from `Monster` and `MonsterSkill`.
*   **`Inventory`**: Manages drops and item usage (`Item`, `Inventory`).
*   **`Map`**: Manages navigation and grid coordinates for different worlds (`TownOfEchoes`, `AbyssOfDissonance`).
*   **`Player`**: Manages player stats, leveling, and specific character logic.

---

## 💻 Installation & How to Run

**Prerequisites:**
*   Java Development Kit (JDK) 8 or higher.

**Steps:**
1.  Clone the repository:
    ```bash
    git clone https://github.com/your-username/silentium.git
    ```
2.  Navigate to the source directory (src):
    ```bash
    cd silentium/src
    ```
3.  Compile the Java files:
    ```bash
    javac Main/Main.java
    ```
4.  Run the game:
    ```bash
    java Main.Main
    ```

---

## 📜 Credits

**Development Team:**
*   **Ricksmer Cabatingan** - System Logic, Balancing, Story
*   **Andrew F. Sangasina** - Tig luto pancit canton, tig timpla kape
*   **Ryza Janell P. Mutya** - Chord Systems, Testing
*   **Precious Ann S. Tolentino** - Inventory Systems, QA

*"Silentium" Game Documentation - 2025-2026*
