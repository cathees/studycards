package flash.server.quiz;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

public final class AnswerMatcher {

    private static final Pattern DIACRITICS = Pattern.compile("\\p{M}+");
    private static final Pattern NON_ALNUM  = Pattern.compile("[^\\p{IsAlphabetic}\\p{IsDigit} ]+");
    private static final Pattern WS         = Pattern.compile("\\s+");
    private static final Pattern VAR_SPLIT  = Pattern.compile("[,;|/]+");

    private static final LevenshteinDistance LD = new LevenshteinDistance();
    private static final JaroWinklerSimilarity JW = new JaroWinklerSimilarity();

    private AnswerMatcher() {}

    public static boolean isCorrect(String userAnswerRaw, String correctAnswerRaw) {
        String user = normalize(userAnswerRaw);
        if (user.isEmpty()) return false;

        for (String variant : splitVariants(correctAnswerRaw)) {
            if (matchesVariant(user, variant)) return true;
        }
        return false;
    }

    private static List<String> splitVariants(String correctRaw) {
        if (correctRaw == null) return List.of();
        String[] parts = VAR_SPLIT.split(correctRaw);
        List<String> out = new ArrayList<>(parts.length);
        for (String p : parts) {
            String v = normalize(p);
            if (!v.isEmpty()) out.add(v);
        }
        return out;
    }

    private static boolean matchesVariant(String user, String variant) {
        if (user.equals(variant)) return true;

        int maxLen = Math.max(user.length(), variant.length());
        if (maxLen <= 2) return false; // avoid accepting too much for tiny answers

        // Edit distance first
        int dist = LD.apply(user, variant);

        if (maxLen <= 4) return dist <= 1;
        if (maxLen <= 8) return dist <= 1;

        // long strings: allow a bit more
        if (dist <= 2) return true;

        // fallback similarity (helps with transpositions etc.)
        Double jw = JW.apply(user, variant);
        return jw != null && jw >= 0.92;
    }

    private static String normalize(String s) {
        if (s == null) return "";
        String x = s.trim().toLowerCase(Locale.ROOT);

        // German special-case
        x = x.replace("ß", "ss");

        // NFKD + remove diacritics (ä->a, é->e, ...)
        x = Normalizer.normalize(x, Normalizer.Form.NFKD);
        x = DIACRITICS.matcher(x).replaceAll("");

        // remove punctuation, keep letters/digits/spaces
        x = NON_ALNUM.matcher(x).replaceAll(" ");

        // collapse whitespace
        x = WS.matcher(x).replaceAll(" ").trim();
        return x;
    }
}
