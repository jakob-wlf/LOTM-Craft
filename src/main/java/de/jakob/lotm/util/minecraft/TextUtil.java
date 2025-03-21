package de.jakob.lotm.util.minecraft;

import org.bukkit.entity.Player;

public class TextUtil {
    public static String helpMessage = """
            §6§lBeyonder Plugin - Help Menu
            §eUse the following commands to control your Beyonder abilities:

            §b/togglegriefing §7- §fEnables or disables destruction caused by your Beyonder powers.
            §b/abilities §7- §fGives you access to all your currently unlocked Beyonder abilities.
            §b/abilityInfos [sequence] §7- §fDisplays information about your abilities.
               §7- Without a parameter: Shows info for all your abilities.
               §7- With a sequence (e.g., §3/abilityInfos 5§7): Shows info for that specific sequence.
            §b/ally §7- §fAdd another player as your ally.
            §b/removeally §7- §Remove another player as your ally.

            §aTip: §7Use §b/beyonderhelp §7to see this message again at any time!""";

    public static void displayMessageWithCorrectLineBreaks(Player player, String message) {
        for(String line : message.split("\n")) {
            player.sendMessage(line);
        }
    }

    public static void displayAbilityInfoWithCorrectLineBreaks(Player player, String message) {
        final String[] lines = message.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int indentLength = 0;

            // Search upwards for indentation if the line starts with "§k§r"
            if (line.startsWith("§k§r")) {
                int searchIndex = i - 1;
                while (searchIndex >= 0 && lines[searchIndex].startsWith("§k§r")) {
                    searchIndex--;
                }

                if (searchIndex >= 0) {
                    String previousLine = lines[searchIndex];
                    for (int j = 0; j < previousLine.length(); j++) {
                        if (previousLine.charAt(j) == '-') {
                            indentLength = j;
                            break;
                        }
                    }
                }
            }

            if (indentLength > 3) {
                indentLength -= 2;
            }

            String indent = " ".repeat(indentLength);
            player.sendMessage(indent + line);
        }
    }
}
