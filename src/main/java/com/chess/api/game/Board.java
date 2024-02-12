package com.chess.api.game;

import com.chess.api.game.exception.IllegalActionException;
import com.chess.api.game.movement.Path;
import com.chess.api.game.piece.Piece;
import com.chess.api.game.piece.PieceFactory;
import com.chess.api.game.piece.PieceType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import lombok.NonNull;

public class Board {

    private final int length;
    private final int width;
    private final Map<Vector2D, Piece> pieceMap;
    private final Map<Vector2D, Set<Piece>> wThreats;
    private final Map<Vector2D, Set<Piece>> bThreats;
    private Vector2D wKing;
    private Vector2D bKing;
    private boolean wCheck;
    private boolean bCheck;
    private Piece lastMoved;

    public Board() {
        this.length = 8;
        this.width = 8;
        Map<Vector2D, Piece> map = new HashMap<>();
        map.putAll(this.generatePiecesInRank(0));
        map.putAll(this.generatePiecesInRank(1));
        map.putAll(this.generatePiecesInRank(this.length - 2));
        map.putAll(this.generatePiecesInRank(this.length - 1));
        this.wKing = new Vector2D(4, 0);
        this.bKing = new Vector2D(4, 7);
        this.pieceMap = map;
        Map<Vector2D, Set<Piece>> wThreatMap = new HashMap<>();
        Map<Vector2D, Set<Piece>> bThreatMap = new HashMap<>();
        for (Vector2D v : map.keySet()) {
            Piece p = this.getPiece(v);
            if (Colour.WHITE.equals(p.getColour())) {
                wThreatMap.computeIfAbsent(v, k -> new HashSet<>()).add(p);
            } else {
                bThreatMap.computeIfAbsent(v, k -> new HashSet<>()).add(p);
            }
        }
        this.wThreats = wThreatMap;
        this.bThreats = bThreatMap;
        this.wCheck = false;
        this.bCheck = false;
        this.lastMoved = null;
    }

    private Map<Vector2D, Piece> generatePiecesInRank(int y) {
        Map<Vector2D, Piece> map = new HashMap<>();
        Colour colour = y < (this.length - 1) / 2 ? Colour.WHITE : Colour.BLACK;

        PieceFactory pieceFactory = PieceFactory.getInstance();
        if (y == 0 || y == this.length - 1) {
            for (int x = 0; x < 8; x++) {
                Vector2D vector = new Vector2D(x, y);
                Piece piece = switch (x) {
                    case 0, 7 -> pieceFactory.build(PieceType.ROOK, colour, vector);
                    case 1, 6 -> pieceFactory.build(PieceType.KNIGHT, colour, vector);
                    case 2, 5 -> pieceFactory.build(PieceType.BISHOP, colour, vector);
                    case 3 -> pieceFactory.build(PieceType.QUEEN, colour, vector);
                    case 4 -> pieceFactory.build(PieceType.KING, colour, vector);
                    default -> null;
                };
                map.put(vector, piece);
            }
        } else if (y == 1 || y == this.length - 2) {
            for (int x = 0; x < 8; x++) {
                Vector2D vector = new Vector2D(x, y);
                Piece piece = pieceFactory.build(PieceType.PAWN, colour, vector);
                map.put(vector, piece);
            }
        }
        return map;
    }

    public int count() {
        Collection<Piece> pieces = this.pieceMap.values();
        int count = 0;
        for (Piece piece : pieces) {
            if (piece != null)
                count++;
        }
        return count;
    }

    public int length() {
        return this.length;
    }

    public int width() {
        return this.width;
    }

    public Piece getPiece(int x, int y) {
        if (x < 0 || x > this.width - 1 || y < 0 || y > this.length - 1) {
            return null;
        }
        return pieceMap.get(new Vector2D(x, y));
    }

    public Piece getPiece(Vector2D vector) {
        if (vector == null) {
            return null;
        }
        return pieceMap.get(vector);
    }

    public void setPiece(@NonNull Vector2D vector, Piece piece) {
        if (piece == null) {
            this.pieceMap.remove(vector);
        } else {
            this.pieceMap.remove(piece.getPosition());
            this.pieceMap.put(vector, piece);
            piece.setPosition(vector);
            // Update the king position if it moved
            if (piece.getType().equals(PieceType.KING)) {
                if (piece.getColour().equals(Colour.WHITE)) {
                    this.wKing = vector;
                } else {
                    this.bKing = vector;
                }
            }
        }
    }

    public List<Piece> getPieces() {
        return List.of();
    }

    public List<Piece> getPieces(Path path) {
        if (path == null) {
            return this.getPieces();
        }
        List<Piece> pieceList = new LinkedList<>();
        for (Vector2D vector : path) {
            pieceList.add(this.getPiece(vector));
        }
        return pieceList;
    }

    public Piece getLastMoved() {
        return lastMoved;
    }

    public void setLastMoved(Piece piece) {
        this.lastMoved = piece;
    }

    public Vector2D getKingLocation(@NonNull Colour colour) {
        if (colour.equals(Colour.WHITE)) {
            return this.wKing;
        } else {
            return this.bKing;
        }
    }

    public Piece getKing(@NonNull Colour colour) {
        return pieceMap.get(this.getKingLocation(colour));
    }

    public void movePiece(@NonNull Vector2D start, @NonNull Vector2D end) {
        Piece pMoved = this.getPiece(start);
        Piece pCaptured = this.getPiece(end);
        this.setPiece(end, pMoved);

        if (pCaptured != null) {
            this.updatePieceThreats(pCaptured, end, null);
        }
        this.updateLocationThreats(start);
        // Check if piece is pinned
        if ((pMoved.getColour().equals(Colour.WHITE) && this.isKingInCheck(Colour.BLACK))
                || (!pMoved.getColour().equals(Colour.WHITE) && this.isKingInCheck(Colour.WHITE))) {
            // Undo move and threats
            this.setPiece(start, pMoved);
            this.setPiece(end, pCaptured);
            if (pCaptured != null)
                this.updatePieceThreats(pCaptured, null, end);
            this.updateLocationThreats(start);
            throw new IllegalActionException("Cannot move piece at " + start + " as player's king will be in check.");
        }
        this.updatePieceThreats(pMoved, start, end);
        this.updateLocationThreats(end);
        this.bCheck = pMoved.getColour().equals(Colour.WHITE) && isKingInCheck(Colour.BLACK);
        this.wCheck = pMoved.getColour().equals(Colour.BLACK) && isKingInCheck(Colour.WHITE);
        this.setLastMoved(pMoved);
    }

    private void updatePieceThreats(@NonNull Piece moving, Vector2D start, Vector2D end) {
        Set<Vector2D> mStart = start != null ? moving.getMovementSet(start, this, true) : new HashSet<>();
        Set<Vector2D> mEnd = end != null ? moving.getMovementSet(end, this, true) : new HashSet<>();
        this.updateThreats(moving, mStart, mEnd);
    }

    private void updateLocationThreats(@NonNull Vector2D vector) {
        Stream.concat(wThreats.get(vector).stream(), bThreats.get(vector).stream()).forEach(p -> {
            Set<Vector2D> movesIgnoringBoard = p.getMovementSet(p.getPosition(), null);
            Set<Vector2D> movesWithBoard = p.getMovementSet(p.getPosition(), this, true);
            this.updateThreats(p, movesIgnoringBoard, movesWithBoard);
        });
    }

    private void updateThreats(@NonNull Piece piece, @NonNull Set<Vector2D> before, @NonNull Set<Vector2D> after) {
        before.removeAll(after);
        if (piece.getColour().equals(Colour.WHITE)) {
            // Remove all old threats of this piece
            for (Vector2D v : before) {
                this.wThreats.computeIfAbsent(v, k -> new HashSet<>()).remove(piece);
            }
            // Add all new threats of this piece (including overlap)
            for (Vector2D v : after) {
                this.wThreats.computeIfAbsent(v, k -> new HashSet<>()).add(piece);
            }
        } else {
            for (Vector2D v : before) {
                this.bThreats.computeIfAbsent(v, k -> new HashSet<>()).remove(piece);
            }
            for (Vector2D v : after) {
                this.bThreats.computeIfAbsent(v, k -> new HashSet<>()).add(piece);
            }
        }
    }

    public List<Piece> getLocationThreats(@NonNull Vector2D vector2D) {
        return this.wThreats.get(vector2D).stream().toList();
    }

    private boolean isKingInCheck(@NonNull Colour kingColour) {
        Set<Piece> threatsAtKing = this.wThreats.get(this.getKingLocation(kingColour));
        for (Piece piece : threatsAtKing) {
            if (!kingColour.equals(piece.getColour())) {
                return true;
            }
        }
        return false;
    }

    public boolean getKingCheck(@NonNull Colour kingColour) {
        return kingColour.equals(Colour.WHITE) ? wCheck : bCheck;
    }

    public List<Piece> getPiecesCausingCheck(@NonNull Colour kingColour) {
        Set<Piece> threatsAtKing = this.wThreats.get(this.getKingLocation(kingColour));
        List<Piece> pieces = new ArrayList<>();
        for (Piece p : threatsAtKing) {
            if (!kingColour.equals(p.getColour())) {
                pieces.add(p);
            }
        }
        return pieces;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = this.length - 1; y >= 0; y--) {
            for (int x = 0; x <= this.width - 1; x++) {
                Piece piece = getPiece(x, y);
                if (piece == null) {
                    sb.append("|   ");
                } else {
                    sb.append("| ");
                    if (PieceType.PAWN.equals(piece.getType())) {
                        sb.append("P ");
                    } else {
                        sb.append(piece.getType().getCode()).append(" ");
                    }
                }
            }
            sb.append("|\n");
        }
        return sb.toString();
    }

}
