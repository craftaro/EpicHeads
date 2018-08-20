package net.sothatsit.heads;

import net.sothatsit.heads.cache.CacheHead;
import net.sothatsit.heads.util.Checks;

import java.util.*;

public final class Search {

    private Query query;
    private Query reusableQuery;
    private double threshold;

    private int[][] editDis;
    private int editDisDim1;
    private int editDisDim2;

    private List<Substring> substrings = new ArrayList<>();

    private Search(String query, double threshold) {
        this.query = new Query(query, toWords(query));
        this.substrings = new ArrayList<>();
        this.reusableQuery = new Query("", null);
        this.threshold = threshold;

        getReusableArray(query.length() + 1, 38);
    }

    private int[][] getReusableArray(int dim1, int dim2) {
        if(dim1 <= editDisDim1 && dim2 <= editDisDim2)
            return editDis;

        dim1 = Math.max(dim1, editDisDim1);
        dim2 = Math.max(dim2, editDisDim2);

        editDis = new int[dim1][dim2];
        editDisDim1 = dim1;
        editDisDim2 = dim2;

        return editDis;
    }

    private void appendSubstring(int index, String string, int start, int end) {
        if(index < substrings.size()) {
            substrings.get(index).reuse(string, start, end);
        } else {
            substrings.add(new Substring(string, start, end));
        }
    }

    public List<Substring> toWords(String string) {
        int len = string.length();

        int wordCount = 0;
        int lastSplit = 0;
        boolean inWord = false;

        for(int index = 0; index < len; ++index) {
            char ch = string.charAt(index);

            if(ch == ' ') {
                if(inWord) {
                    appendSubstring(wordCount, string, lastSplit, index);
                    wordCount += 1;
                    lastSplit = index + 1;
                }

                inWord = false;
            } else {
                inWord = true;
            }
        }

        if(inWord) {
            appendSubstring(wordCount, string, lastSplit, len);
            wordCount += 1;
        }

        return substrings.subList(0, wordCount);
    }

    public Query reuseQuery(String string) {
        return reusableQuery.reuse(string, toWords(string));
    }

    public List<CacheHead> checkAll(Iterable<CacheHead> heads) {
        List<Match> matches = new ArrayList<>();

        for(CacheHead head : heads) {
            double relevance = calculateRelevance(query, head);

            if(relevance <= threshold)
                continue;

            matches.add(new Match(head, relevance));
        }

        Collections.sort(matches);

        List<CacheHead> results = new ArrayList<>();

        for(Match match : matches) {
            results.add(match.subject);
        }

        return results;
    }

    private double calculateRelevance(Query query, CacheHead head) {
        double relevance = calculateRelevance(query, reuseQuery(head.getName()));

        for(String tag : head.getTags()) {
            relevance = Math.max(relevance, 0.8 * calculateRelevance(query, reuseQuery(tag)));
        }

        return relevance;
    }

    private double calculateRelevance(Query query, Query subject) {
        double similarity = calcSimilarity(query.string, subject.string);

        double wordSimilarity = 0d;
        double aggregate = 0d;
        int count = 0;

        for(Substring queryWord : query.words) {
            double querySimilarity = 0d;

            for(Substring subjectWord : subject.words) {
                querySimilarity = Math.max(querySimilarity, calcSimilarity(queryWord, subjectWord));
            }

            aggregate += querySimilarity;
            count += 1;

            wordSimilarity = Math.max(wordSimilarity, querySimilarity);
        }

        if(count > 0) {
            wordSimilarity = 0.9d * wordSimilarity + 0.1d * (aggregate / count);
        }

        return Math.max(similarity, wordSimilarity);
    }

    private double calcSimilarity(Substring query, Substring subject) {
        int len1 = query.length();
        int len2 = subject.length();

        // len1+1, len2+1, because finally return dp[len1][len2]
        int[][] dp = getReusableArray(len1 + 1, len2 + 1);

        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        //iterate though, and check last char
        for (int i = 0; i < len1; i++) {
            char c1 = query.charAt(i);
            for (int j = 0; j < len2; j++) {
                char c2 = subject.charAt(j);

                //if last two chars equal
                if (c1 == c2) {
                    //update dp value for +1 length
                    dp[i + 1][j + 1] = dp[i][j];
                } else {
                    int replace = dp[i][j] + 1;
                    int insert = dp[i][j + 1] + 1;
                    int delete = dp[i + 1][j] + 1;

                    int min = replace > insert ? insert : replace;
                    min = delete > min ? min : delete;
                    dp[i + 1][j + 1] = min;
                }
            }
        }

        int editDistance = dp[len1][len2];

        if(editDistance == 0)
            return 1;

        return 0.75d * (double) (query.length() - editDistance) / (double) query.length();
    }

    private final static class Match implements Comparable<Match> {

        public final CacheHead subject;
        public final double relevance;

        private Match(CacheHead subject, double relevance) {
            this.subject = subject;
            this.relevance = relevance;
        }

        @Override
        public int compareTo(Match other) {
            return Double.compare(other.relevance, relevance);
        }
    }

    private final class Query {

        public Substring string;
        public List<Substring> words;

        public Query(String string, List<Substring> words) {
            this.string = new Substring(string);
            this.words = words;
        }

        public Query reuse(String string, List<Substring> words) {
            this.string.reuse(string);
            this.words = words;

            return this;
        }
    }

    private static class Substring {

        public String string;
        public int start;
        public int end;

        public Substring(String string) {
            this(string, 0, string.length());
        }

        public Substring(String string, int start, int end) {
            reuse(string, start, end);
        }

        public Substring reuse(String string) {
            return reuse(string, 0, string.length());
        }

        public Substring reuse(String string, int start, int end) {
            Checks.ensureNonNull(string, "string");

            this.string = string;
            this.moveTo(start, end);

            return this;
        }

        public void moveTo(int start, int end) {
            Checks.ensureTrue(start >= 0, "start must be >= 0");
            Checks.ensureTrue(end >= start, "end must be >= start");
            Checks.ensureTrue(end <= string.length(), "end must be <= to the length of string");

            this.start = start;
            this.end = end;
        }

        public char charAt(int index) {
            if(index < 0)
                throw new IndexOutOfBoundsException("index cannot be negative");
            if(index >= length())
                throw new IndexOutOfBoundsException("index must be less than the strings length");

            char ch = string.charAt(start + index);

            return (char) (ch >= 'A' && ch <= 'Z' ? ch + ('a' - 'A') : ch);
        }

        public int length() {
            return end - start;
        }

        @Override
        public String toString() {
            return string.substring(start, end);
        }

    }

    /**
     * Search over the list of heads and find all heads with a relevance above a certain threshold.
     * Will simplify the query string in an attempt to improve matches.
     *
     * @param query The search term.
     * @param heads The heads we are checking for matches.
     * @param threshold The threshold relevance that a head must have to be matched.
     * @return All heads sorted by relevance that have a relevance greater than the threshold.
     */
    public static List<CacheHead> searchHeads(String query, Iterable<CacheHead> heads, double threshold) {
        return new Search(query, threshold).checkAll(heads);
    }

}
