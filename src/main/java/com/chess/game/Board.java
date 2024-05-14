package com.chess.game;

import com.chess.game.exception.IllegalActionException;
import com.chess.game.movement.Path;
import com.chess.game.piece.Piece;
import com.chess.game.piece.PieceFactory;
import com.chess.game.piece.PieceType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            Set<Vector2D> pMoves = p.getMovementSet(v, this, false, true, true, false);
            for (Vector2D m : pMoves) {
                if (Colour.WHITE.equals(p.getColour())) {
                    wThreatMap.computeIfAbsent(m, k -> new HashSet<>()).add(p);
                } else {
                    bThreatMap.computeIfAbsent(m, k -> new HashSet<>()).add(p);
                }
            }
        }
        this.wThreats = wThreatMap;
        this.bThreats = bThreatMap;
        this.wCheck = false;
        this.bCheck = false;
        this.lastMoved = null;
    }

    public Board(List<String> pieces) {
        this.length = 8;
        this.width = 8;
        Map<Vector2D, Piece> map = new HashMap<>();
        PieceFactory pf = PieceFactory.getInstance();
        for (String s : pieces) {
            Piece piece = pf.build(s);
            map.put(piece.getPosition(), piece);
            if (PieceType.KING.equals(piece.getType())) {
                if (Colour.WHITE.equals(piece.getColour()))
                    this.wKing = piece.getPosition();
                else
                    this.bKing = piece.getPosition();
            }
        }
        this.pieceMap = map;
        Map<Vector2D, Set<Piece>> wThreatMap = new HashMap<>();
        Map<Vector2D, Set<Piece>> bThreatMap = new HashMap<>();
        for (Vector2D v : map.keySet()) {
            Piece p = this.getPiece(v);
            Set<Vector2D> pMoves = p.getMovementSet(v, this, false, true, true, false);
            for (Vector2D m : pMoves) {
                if (Colour.WHITE.equals(p.getColour())) {
                    wThreatMap.computeIfAbsent(m, k -> new HashSet<>()).add(p);
                } else {
                    bThreatMap.computeIfAbsent(m, k -> new HashSet<>()).add(p);
                }
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

    public void setPiece(Vector2D vector, Piece piece) {
        if (vector == null) {
            throw new NullPointerException();
        }
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
        List<Piece> list = new ArrayList<>(32);
        this.pieceMap.values().stream().filter(Objects::nonNull).forEach(list::add);
        return list;
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

    public Vector2D getKingLocation(Colour colour) {
        if (colour == null) {
            throw new NullPointerException();
        }
        if (colour.equals(Colour.WHITE)) {
            return this.wKing;
        } else {
            return this.bKing;
        }
    }

    public Piece getKing(Colour colour) {
        if (colour == null) {
            throw new NullPointerException();
        }
        return pieceMap.get(this.getKingLocation(colour));
    }

    public void movePiece(Vector2D start, Vector2D end) {
        if (start == null || end == null) {
            throw new NullPointerException();
        }
        Piece pMoved = this.getPiece(start);
        Piece pCaptured = this.getPiece(end);
        this.setPiece(end, pMoved);

        if (pCaptured != null) {
            this.updatePieceThreats(pCaptured, end, null);
        }
        this.updateLocationThreats(start);
        // Check if piece is pinned
        if ((pMoved.getColour().equals(Colour.WHITE) && this.isKingInCheck(Colour.WHITE))
                || (!pMoved.getColour().equals(Colour.WHITE) && this.isKingInCheck(Colour.BLACK))) {
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

    private void updatePieceThreats(Piece moving, Vector2D start, Vector2D end) {
        if (moving == null) {
            throw new NullPointerException();
        }
        Set<Vector2D> mStart = start != null ? moving.getMovementSet(start, this, false, true, true, true) :
                new HashSet<>();
        Set<Vector2D> mEnd = end != null ? moving.getMovementSet(end, this, false, true, true, true) : new HashSet<>();
        this.updateThreats(moving, mStart, mEnd);
    }

    private void updateLocationThreats(Vector2D vector) {
        if (vector == null) {
            throw new NullPointerException();
        }
        Set<Piece> wPieces = wThreats.get(vector);
        if (wPieces == null)
            wPieces = new HashSet<>();
        Set<Piece> bPieces = bThreats.get(vector);
        if (bPieces == null)
            bPieces = new HashSet<>();
        List<Piece> list = Stream.concat(wPieces.stream(), bPieces.stream()).collect(Collectors.toList());
        for (Piece p : list) {
            Set<Vector2D> movesIgnoringBoard = p.getMovementSet(p.getPosition(), null, false, true, false, false);
            Set<Vector2D> movesWithBoard = p.getMovementSet(p.getPosition(), this, false, true, true, true);
            this.updateThreats(p, movesIgnoringBoard, movesWithBoard);
        }
    }

    private void updateThreats(Piece piece, Set<Vector2D> before, Set<Vector2D> after) {
        if (piece == null || before == null || after == null) {
            throw new NullPointerException();
        }
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

    public List<Piece> getLocationThreats(Vector2D vector2D, Colour colour) {
        if (vector2D == null) {
            throw new NullPointerException();
        }
        Set<Piece> wThreatPieces = this.wThreats.get(vector2D);
        if (wThreatPieces == null) {
            wThreatPieces = new HashSet<>();
        }
        Set<Piece> bThreatPieces = this.bThreats.get(vector2D);
        if (bThreatPieces == null) {
            bThreatPieces = new HashSet<>();
        }
        if (colour == null) {
            return Stream.concat(wThreatPieces.stream(), bThreatPieces.stream()).collect(Collectors.toList());
        } else if (Colour.WHITE.equals(colour)) {
            return wThreatPieces.stream().collect(Collectors.toList());
        } else {
            return bThreatPieces.stream().collect(Collectors.toList());
        }
    }

    private boolean isKingInCheck(Colour kingColour) {
        if (kingColour == null) {
            throw new NullPointerException();
        }
        Set<Piece> threatsAtKing = this.wThreats.get(this.getKingLocation(kingColour));
        if (threatsAtKing == null) {
            return false;
        }
        for (Piece piece : threatsAtKing) {
            if (!kingColour.equals(piece.getColour())) {
                return true;
            }
        }
        return false;
    }

    public boolean getKingCheck(Colour kingColour) {
        if (kingColour == null) {
            throw new NullPointerException();
        }
        return kingColour.equals(Colour.WHITE) ? wCheck : bCheck;
    }

    public List<Piece> getPiecesCausingCheck(Colour kingColour) {
        if (kingColour == null) {
            throw new NullPointerException();
        }
        Vector2D kingLoc = this.getKingLocation(kingColour);
        Set<Piece> threats = Colour.WHITE.equals(kingColour) ? this.bThreats.get(kingLoc) : this.wThreats.get(kingLoc);
        if (threats == null) {
            return List.of();
        }
        List<Piece> pieces = new ArrayList<>();
        for (Piece p : threats) {
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

    public String printThreats(Colour colour) {
        if (colour == null) {
            throw new NullPointerException();
        }
        Map<Vector2D, Set<Piece>> threats = Colour.WHITE.equals(colour) ? wThreats : bThreats;
        StringBuilder sb = new StringBuilder();
        for (int y = this.length - 1; y >= 0; y--) {
            for (int x = 0; x <= this.width - 1; x++) {
                boolean hasThreat =
                        threats.get(new Vector2D(x, y)) != null && !threats.get(new Vector2D(x, y)).isEmpty();
                if (!hasThreat) {
                    sb.append("|   ");
                } else {
                    sb.append("| x ");
                }
            }
            sb.append("|\n");
        }
        return sb.toString();
    }

}
