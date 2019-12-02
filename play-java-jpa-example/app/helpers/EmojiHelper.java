package helpers;

import java.util.HashMap;
import java.util.stream.Collectors;

public class EmojiHelper {

    private static final HashMap<String, String> numToEmoji = new HashMap<>() {{

        put("1", "1️⃣");
        put("2", "2️⃣");
        put("3", "3️⃣");
        put("4", "4️⃣");
        put("5", "5️⃣");
        put("6", "6️⃣");
        put("7", "7️⃣");
        put("8", "8️⃣");
        put("9", "9️⃣");
        put("0", "0️⃣");
        put("-", "➖");
    }};


    public static String intToEmojis(int value) {

        final String[] result = {""};

        Integer.toString(value).chars()
                .mapToObj(e -> (char) e)
                .map(c -> numToEmoji.get(Character.toString(c)))
                .collect(Collectors.toList()).forEach(s -> result[0] += s);

        return result[0];

    }
}
