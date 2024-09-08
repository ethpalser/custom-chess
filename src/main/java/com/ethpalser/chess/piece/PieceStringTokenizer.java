package com.ethpalser.chess.piece;

import java.util.ArrayList;
import java.util.List;

public class PieceStringTokenizer {

    private static final String REGEX = "^[wb](?:[PRNBQK]|(?:\\~[a-z0-9]{1,24}\\~))[a-z](?:(?:2[0-6])|" +
            "(?:1\\d)|[1-9])\\*?$";
    private final List<String> tokens;
    private int index;

    public PieceStringTokenizer(String string) {
        List<String> tokenList = new ArrayList<>(6);
        this.index = 0;
        // A standard piece may look like: wQf4 or wPe2*. The asterisk means the piece has not moved.
        // A custom piece may look like: w~dark-knight-7~g1. The string between tilda (~) is its code (max 24 chars).
        if (string != null) {
            // Token 1 is its colour
            String afterColour = this.parseColourToken(tokenList, string);
            String afterCode = this.parseCodeToken(tokenList, afterColour);
            String afterFile = this.parseFileToken(tokenList, afterCode);
            String afterRank = this.parseRankToken(tokenList, afterFile);
            String afterNotMoved = this.parseNotMovedToken(tokenList, afterRank);
            if (afterNotMoved != null && afterNotMoved.length() > 0) {
                System.err.println("piece string has unnecessary excess. remaining chars: " + afterNotMoved);
            }
        }
        this.tokens = tokenList;
    }

    public boolean hasTokens() {
        return this.index < this.tokens.size();
    }

    public String nextToken() {
        if (!this.hasTokens()) {
            throw new IndexOutOfBoundsException("index out of bounds, no token available");
        }
        String token = this.tokens.get(this.index);
        this.index++;
        return token;
    }

    // PRIVATE METHODS

    // Token 1
    private String parseColourToken(List<String> tokens, String piece) {
        if (tokens == null) {
            return null;
        }
        if (piece == null || "".equals(piece)) {
            tokens.add("w"); // default colour
            return null;
        }
        if (piece.charAt(0) == 'w' || piece.charAt(0) == 'b') {
            tokens.add(piece.substring(0, 1));
        } else {
            tokens.add("w"); // default colour
        }
        // Removed one character
        return piece.substring(1);
    }

    // Token 2
    private String parseCodeToken(List<String> tokens, String piece) {
        if (tokens == null) {
            return null;
        }
        if (piece == null || "".equals(piece)) {
            tokens.add("P"); // default piece
            return null;
        }

        if ("PRNBQK".contains(piece.substring(0, 1))) {
            // Expecting piece string to be a substring of its original, thus checking at index 0
            tokens.add(piece.substring(0, 1));
            return piece.substring(1);
        }

        if (piece.charAt(0) != '~' && piece.length() < 5) {
            // When the string is has a short length we can expect the following cases:
            // 1. Aa1* or Aa1 (A is not a standard code)
            // 2. f~a1 (malformed custom code missing a starting ~)
            // 3. rat~ (malformed custom code and missing other piece info)
            // 4. a26* (code is missing)
            tokens.add("P"); // default piece type
            return this.substringWithoutCode(piece);
        } else {
            if (piece.charAt(0) != '~') {
                System.err.println("custom-piece string is invalid, missing starting '~' at index 1. string: " + piece);
                tokens.add("P");
                return this.substringWithoutCode(piece);
            }
            int end = 0;
            for (int i = 1; i < piece.length(); i++) {
                if (piece.charAt(i) == '~') {
                    end = i;
                    break;
                }
            }
            if (end == 0) {
                System.err.println("custom-piece string is invalid, missing enclosing '~'. string: " + piece);
                tokens.add("P");
                return this.substringWithoutCode(piece);
            }
            tokens.add(piece.substring(1, end));
            return piece.substring(end + 1);
        }
    }

    private String substringWithoutCode(String original) {
        if (original == null || original.length() <= 1) {
            return null;
        }
        int substringEnd = original.length() - 1;
        // Checking expected end info
        try {
            if (original.charAt(substringEnd) == '*') {
                substringEnd--;
            }
            if (original.substring(substringEnd).matches("\\d")) {
                substringEnd--;
            }
            if (original.substring(substringEnd).matches("[1-2]")) {
                substringEnd--;
            }
            if (original.substring(substringEnd).matches("[a-z]")) {
                substringEnd--;
            }
            if (original.charAt(substringEnd - 1) == '~') {
                return original.substring(substringEnd);
            }
        } catch (IndexOutOfBoundsException ex) {
            return original.substring(substringEnd);
        }
        return original.substring(substringEnd); // We found at least some values to generate the rest of the piece
    }

    // Token 3
    private String parseFileToken(List<String> tokens, String piece) {
        if (tokens == null) {
            return null;
        }
        if (piece == null || "".equals(piece)) {
            tokens.add("a"); // default file (y-axis)
            return null;
        }

        if (piece.substring(0, 1).matches("[a-z]")) {
            tokens.add(piece.substring(0, 1));
            return piece.substring(1);
        } else {
            tokens.add("a"); // default file
            if (piece.substring(0, 1).matches("\\d") || piece.charAt(0) == '*') {
                return piece;
            } else {
                return piece.substring(1);
            }
        }
    }

    // Token 4
    private String parseRankToken(List<String> tokens, String piece) {
        if (tokens == null) {
            return null;
        }
        if (piece == null || "".equals(piece)) {
            tokens.add("1"); // default rank (x-axis)
            return null;
        }

        int end;
        if (piece.length() == 1 || (piece.length() == 2 && piece.charAt(1) == '*')) {
            end = 1;
        } else {
            end = 2;
        }
        String rank = piece.substring(0, end);
        if (rank.matches("(?:(2[0-6])|(1\\d)|[1-9])\\*?$")) {
            tokens.add(rank);
        } else {
            tokens.add("1"); // default rank
            if (rank.charAt(0) == '*') {
                return piece;
            }
        }
        return piece.substring(end);
    }

    // Token 5
    private String parseNotMovedToken(List<String> tokens, String piece) {
        if (tokens == null) {
            return null;
        }
        // Note: Empty string is okay in this case
        if (piece == null || "".equals(piece)) {
            tokens.add(""); // default is moved
            return null;
        }

        String hasMovedFlag = piece.substring(0, 1);
        if ("*".equals(hasMovedFlag)) {
            tokens.add(hasMovedFlag);
        } else {
            tokens.add("*");
        }
        return piece.substring(1);
    }

}
