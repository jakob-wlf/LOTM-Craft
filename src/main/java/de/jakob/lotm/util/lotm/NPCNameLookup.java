package de.jakob.lotm.util.lotm;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class NPCNameLookup {

    private static final List<String> NAMES = Arrays.asList(
            "Alger", "Roselle", "Reynold", "Cattleya", "Lucien", "Ellsworth",
            "Horace", "Tristan Hart", "Aldrich", "Byron Klein", "Lawrence",
            "Oswald Finch", "Godfrey", "Ambrose", "Elias", "Archibald",
            "Cassius", "Rupert Black", "Barnaby Lyle", "Edwin Crow",
            "Calloway", "Lucan Mercer", "Duncan Gable", "Vance Hollow",
            "Balthazar", "Cedric Hollow", "Owen Drexler",
            "Felix", "Graham", "Simon", "Harvey", "Wallace", "Julian",
            "Hector", "Percy", "Otis", "Miles", "Alistair", "Basil",
            "Roland", "Regis", "Ivan", "Cedric", "Victor", "Lloyd",
            "Cyrus", "Hugo", "Gideon", "Jonas", "Silas", "Oscar",
            "Everett", "Quincy", "Vincent", "Milo", "Elliot", "Tobias",
            "Adrian", "Desmond", "Walter", "August", "Wesley", "Harlan",
            "Leopold", "Emmett", "Luther", "Rufus", "Jasper", "Lucian",
            "Raphael", "Ephraim", "Magnus", "Sterling", "Xavier",
            "Dorian", "Edgar", "Phineas"
    );

    private static final Random RANDOM = new Random();

    public static String getRandomName() {
        return NAMES.get(RANDOM.nextInt(NAMES.size()));
    }
}
