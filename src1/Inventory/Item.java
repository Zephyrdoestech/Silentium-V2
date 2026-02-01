    package Inventory;

    import Display.TextDisplay;
    import Player.Character;

    public class Item {
        private String name;
        private String description;
        TextDisplay text = new TextDisplay();

        public Item(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public void applyEffect(Character player) {
            switch (name) {
                case "Crimson Chorus":
                    text.printSystemAnnouncement("Crimson Chorus activated! Enemies take more damage for 3 turns.");
                    double percent = 1.05 + (Math.random() * 0.05);
                    int turns = 2 + (int)(Math.random() * 2);
                    player.activateTemporaryBuff(percent, turns);
                    break;

                case "Silent Barrier":
                    text.printSystemAnnouncement("Silent Barrier activated! You are immune for 1 turn!");
                    player.activateBarrier(1);
                    break;

                case "Resolved Dissonance":
                    text.printSystemAnnouncement("Resolved Dissonance active! No HP loss from next B Dim chord.");
                    player.setIgnoreBDiminished(true); // we’d track this in ChordSystem if needed
                    break;

                case "Minor’s Grace":
                    text.printSystemAnnouncement("Minor’s Grace! Start battle with +1 free use of any Minor chord.");
                    player.addMinorChordUse(1);
                    break;

                case "Major’s Blessing":
                    text.printSystemAnnouncement("Major’s Blessing! Start battle with +1 free use of any Major chord.");
                    player.addMajorChordUse(1);
                    break;
            }
        }
    }