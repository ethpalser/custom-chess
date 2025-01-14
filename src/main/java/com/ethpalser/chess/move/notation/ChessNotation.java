package com.ethpalser.chess.move.notation;

import com.ethpalser.chess.annotation.ClassPreamble;
import java.util.Objects;

@ClassPreamble(
        author = "Ethan A. Palser",
        created = "2024-12-19",
        majorVersion = 1,
        minorVersion = 5,
        lastModified = "2025-01-13"
)
public class ChessNotation {

    public static final ChessNotationFormat DEFAULT_FORMAT = new VerboseNotationFormat();

    // Store the expected notation that must be built
    private final ChessNotationFormat format;
    private final String string;
    // Local in-memory cache of its record, to reduce redundant creation
    private ChessRecord chessRecord;

    /**
     * Create a ChessNotation string using an action's alias. This alias should be well known, or it will not
     * be understood by players. For example, the King's Castle actions have aliases O-O-O or O-O for queen-side
     * castle and king-side castle respectively.
     *
     * @param alias String of a specific, unique chess notation
     */
    public ChessNotation(ChessNotationFormat chessFormat, ChessRecord chessRecord, ChessNotationAlias alias) {
        this.format = chessFormat;
        this.string = chessFormat.format(chessRecord, alias);
    }

    public ChessNotation(String notationString) {
        this(DEFAULT_FORMAT, notationString);
    }

    public ChessNotation(ChessNotationFormat chessFormat, String notationString) {
        if (chessFormat == null || notationString == null) {
            throw new IllegalArgumentException();
        }
        this.format = chessFormat;
        this.string = notationString;
    }

    public ChessNotation(ChessRecord chessRecord) {
        this(DEFAULT_FORMAT, chessRecord);
    }

    public ChessNotation(ChessNotationFormat chessFormat, ChessRecord chessRecord) {
        if (chessFormat == null || chessRecord == null) {
            throw new IllegalArgumentException();
        }
        this.format = chessFormat;
        this.string = chessFormat.format(chessRecord);
        this.chessRecord = chessRecord;
    }

    public ChessRecord toRecord() {
        // Not thread safe, but this and record are stateless, so it is only a performance issue
        if (this.chessRecord == null) {
            ChessRecord rec = this.format.parse(this.string);
            if (rec == null) {
                throw new IllegalStateException("ChessFormat created a null ChessRecord");
            }
            this.chessRecord = rec;
        }
        return this.chessRecord;
    }

    @Override
    public String toString() {
        return this.string;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessNotation notation = (ChessNotation) o;
        return format.equals(notation.format) && string.equals(notation.string);
    }

    @Override
    public int hashCode() {
        return Objects.hash(format, string);
    }
}
