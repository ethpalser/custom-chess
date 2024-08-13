package com.ethpalser.chess.board;

import com.ethpalser.chess.exception.IllegalActionException;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.custom.CustomPiece;
import com.ethpalser.chess.piece.custom.CustomPieceFactory;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.space.Path;
import com.ethpalser.chess.space.Plane;
import com.ethpalser.chess.space.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomBoard implements Board {

    private final int length;
    private final int width;
    private final Plane<Piece> pieceMap;
    private final Map<Point, Set<CustomPiece>> wThreats;
    private final Map<Point, Set<CustomPiece>> bThreats;
    private Point wKing;
    private Point bKing;
    private boolean wCheck;
    private boolean bCheck;
    private Piece lastMoved;

    public CustomBoard() {
        this.length = 8;
        this.width = 8;
        Plane<Piece> map = new Plane<>();
        map.putAll(this.generatePiecesInRank(0));
        map.putAll(this.generatePiecesInRank(1));
        map.putAll(this.generatePiecesInRank(this.length - 2));
        map.putAll(this.generatePiecesInRank(this.length - 1));
        this.wKing = new Point(4, 0);
        this.bKing = new Point(4, 7);
        this.pieceMap = map;
        Map<Point, Set<CustomPiece>> wThreatMap = new HashMap<>();
        Map<Point, Set<CustomPiece>> bThreatMap = new HashMap<>();
        for (Point v : map.keySet()) {
            CustomPiece p = (CustomPiece) this.getPiece(v);
            Set<Point> pMoves = p.getMovementSet(v, this, false, true, true, false);
            for (Point m : pMoves) {
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

    public CustomBoard(List<String> pieces) {
        this.length = 8;
        this.width = 8;
        Plane<Piece> map = new Plane<>();
        CustomPieceFactory pf = CustomPieceFactory.getInstance();
        for (String s : pieces) {
            CustomPiece customPiece = pf.build(s);
            map.put(customPiece.getPosition(), customPiece);
            if (PieceType.KING.equals(customPiece.getType())) {
                if (Colour.WHITE.equals(customPiece.getColour()))
                    this.wKing = customPiece.getPosition();
                else
                    this.bKing = customPiece.getPosition();
            }
        }
        this.pieceMap = map;
        Map<Point, Set<CustomPiece>> wThreatMap = new HashMap<>();
        Map<Point, Set<CustomPiece>> bThreatMap = new HashMap<>();
        for (Point v : map.keySet()) {
            CustomPiece p = (CustomPiece) this.getPiece(v);
            Set<Point> pMoves = p.getMovementSet(v, this, false, true, true, false);
            for (Point m : pMoves) {
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

    private Map<Point, CustomPiece> generatePiecesInRank(int y) {
        Map<Point, CustomPiece> map = new HashMap<>();
        Colour colour = y < (this.length - 1) / 2 ? Colour.WHITE : Colour.BLACK;

        CustomPieceFactory customPieceFactory = CustomPieceFactory.getInstance();
        if (y == 0 || y == this.length - 1) {
            for (int x = 0; x < 8; x++) {
                Point vector = new Point(x, y);
                CustomPiece customPiece = switch (x) {
                    case 0, 7 -> customPieceFactory.build(PieceType.ROOK, colour, vector);
                    case 1, 6 -> customPieceFactory.build(PieceType.KNIGHT, colour, vector);
                    case 2, 5 -> customPieceFactory.build(PieceType.BISHOP, colour, vector);
                    case 3 -> customPieceFactory.build(PieceType.QUEEN, colour, vector);
                    case 4 -> customPieceFactory.build(PieceType.KING, colour, vector);
                    default -> null;
                };
                map.put(vector, customPiece);
            }
        } else if (y == 1 || y == this.length - 2) {
            for (int x = 0; x < 8; x++) {
                Point vector = new Point(x, y);
                CustomPiece customPiece = customPieceFactory.build(PieceType.PAWN, colour, vector);
                map.put(vector, customPiece);
            }
        }
        return map;
    }

    public int count() {
        Collection<Piece> pieces = this.pieceMap.values();
        int count = 0;
        for (Piece p : pieces) {
            if (p != null)
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
        return pieceMap.get(new Point(x, y));
    }

    public Piece getPiece(Point vector) {
        if (vector == null) {
            return null;
        }
        return pieceMap.get(vector);
    }

    public void addPiece(Point point, Piece piece) {
        if (point == null) {
            throw new NullPointerException();
        }
        if (piece == null) {
            this.pieceMap.remove(point);
        } else {
            this.pieceMap.remove(piece.getPoint());
            this.pieceMap.put(point, piece);
            piece.move(point);
            // Update the king position if it moved
            if (PieceType.KING.getCode().equals(piece.getCode())) {
                if (piece.getColour().equals(Colour.WHITE)) {
                    this.wKing = point;
                } else {
                    this.bKing = point;
                }
            }
        }
    }

    public Plane<Piece> getPieces() {
        return pieceMap;
    }

    public List<Piece> getPieces(Path path) {
        List<Piece> customPieceList = new LinkedList<>();
        if (path == null) {
            for (Piece piece : pieceMap) {
                customPieceList.add(piece);
            }
            return customPieceList;
        }
        for (Point vector : path) {
            customPieceList.add(this.getPiece(vector));
        }
        return customPieceList;
    }

    public Piece getLastMoved() {
        return lastMoved;
    }

    public void setLastMoved(Piece customPiece) {
        this.lastMoved = customPiece;
    }

    public Point getKingLocation(Colour colour) {
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

    public void movePiece(Point start, Point end) {
        if (start == null || end == null) {
            throw new NullPointerException();
        }
        Piece pMoved = this.getPiece(start);
        Piece pCaptured = this.getPiece(end);
        this.addPiece(end, pMoved);

        if (pCaptured != null) {
            this.updatePieceThreats((CustomPiece) pCaptured, end, null);
        }
        this.updateLocationThreats(start);
        // Check if piece is pinned
        if ((pMoved.getColour().equals(Colour.WHITE) && this.isKingInCheck(Colour.WHITE))
                || (!pMoved.getColour().equals(Colour.WHITE) && this.isKingInCheck(Colour.BLACK))) {
            // Undo move and threats
            this.addPiece(start, pMoved);
            this.addPiece(end, pCaptured);
            if (pCaptured != null)
                this.updatePieceThreats((CustomPiece) pCaptured, null, end);
            this.updateLocationThreats(start);
            throw new IllegalActionException("Cannot move piece at " + start + " as player's king will be in check.");
        }
        this.updatePieceThreats((CustomPiece) pMoved, start, end);
        this.updateLocationThreats(end);
        this.bCheck = pMoved.getColour().equals(Colour.WHITE) && isKingInCheck(Colour.BLACK);
        this.wCheck = pMoved.getColour().equals(Colour.BLACK) && isKingInCheck(Colour.WHITE);
        this.setLastMoved((CustomPiece) pMoved);
    }

    @Override
    public boolean isInBounds(int x, int y) {
        return false;
    }

    private void updatePieceThreats(CustomPiece moving, Point start, Point end) {
        if (moving == null) {
            throw new NullPointerException();
        }
        Set<Point> mStart = start != null ? moving.getMovementSet(start, this, false, true, true, true) :
                new HashSet<>();
        Set<Point> mEnd = end != null ? moving.getMovementSet(end, this, false, true, true, true) : new HashSet<>();
        this.updateThreats(moving, mStart, mEnd);
    }

    private void updateLocationThreats(Point vector) {
        if (vector == null) {
            throw new NullPointerException();
        }
        Set<CustomPiece> wCustomPieces = wThreats.get(vector);
        if (wCustomPieces == null)
            wCustomPieces = new HashSet<>();
        Set<CustomPiece> bCustomPieces = bThreats.get(vector);
        if (bCustomPieces == null)
            bCustomPieces = new HashSet<>();
        List<CustomPiece> list = Stream.concat(wCustomPieces.stream(), bCustomPieces.stream()).collect(Collectors.toList());
        for (CustomPiece p : list) {
            Set<Point> movesIgnoringBoard = p.getMovementSet(p.getPosition(), this, false, true, false, false);
            Set<Point> movesWithBoard = p.getMovementSet(p.getPosition(), this, false, true, true, true);
            this.updateThreats(p, movesIgnoringBoard, movesWithBoard);
        }
    }

    private void updateThreats(CustomPiece customPiece, Set<Point> before, Set<Point> after) {
        if (customPiece == null || before == null || after == null) {
            throw new NullPointerException();
        }
        before.removeAll(after);
        if (customPiece.getColour().equals(Colour.WHITE)) {
            // Remove all old threats of this piece
            for (Point v : before) {
                this.wThreats.computeIfAbsent(v, k -> new HashSet<>()).remove(customPiece);
            }
            // Add all new threats of this piece (including overlap)
            for (Point v : after) {
                this.wThreats.computeIfAbsent(v, k -> new HashSet<>()).add(customPiece);
            }
        } else {
            for (Point v : before) {
                this.bThreats.computeIfAbsent(v, k -> new HashSet<>()).remove(customPiece);
            }
            for (Point v : after) {
                this.bThreats.computeIfAbsent(v, k -> new HashSet<>()).add(customPiece);
            }
        }
    }

    public List<CustomPiece> getLocationThreats(Point point, Colour colour) {
        if (point == null) {
            throw new NullPointerException();
        }
        Set<CustomPiece> wThreatCustomPieces = this.wThreats.get(point);
        if (wThreatCustomPieces == null) {
            wThreatCustomPieces = new HashSet<>();
        }
        Set<CustomPiece> bThreatCustomPieces = this.bThreats.get(point);
        if (bThreatCustomPieces == null) {
            bThreatCustomPieces = new HashSet<>();
        }
        if (colour == null) {
            return Stream.concat(wThreatCustomPieces.stream(), bThreatCustomPieces.stream()).collect(Collectors.toList());
        } else if (Colour.WHITE.equals(colour)) {
            return wThreatCustomPieces.stream().collect(Collectors.toList());
        } else {
            return bThreatCustomPieces.stream().collect(Collectors.toList());
        }
    }

    private boolean isKingInCheck(Colour kingColour) {
        if (kingColour == null) {
            throw new NullPointerException();
        }
        Set<CustomPiece> threatsAtKing = this.wThreats.get(this.getKingLocation(kingColour));
        if (threatsAtKing == null) {
            return false;
        }
        for (CustomPiece customPiece : threatsAtKing) {
            if (!kingColour.equals(customPiece.getColour())) {
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

    public List<CustomPiece> getPiecesCausingCheck(Colour kingColour) {
        if (kingColour == null) {
            throw new NullPointerException();
        }
        Point kingLoc = this.getKingLocation(kingColour);
        Set<CustomPiece> threats = Colour.WHITE.equals(kingColour) ? this.bThreats.get(kingLoc) : this.wThreats.get(kingLoc);
        if (threats == null) {
            return List.of();
        }
        List<CustomPiece> customPieces = new ArrayList<>();
        for (CustomPiece p : threats) {
            if (!kingColour.equals(p.getColour())) {
                customPieces.add(p);
            }
        }
        return customPieces;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = this.length - 1; y >= 0; y--) {
            for (int x = 0; x <= this.width - 1; x++) {
                Piece customPiece = getPiece(x, y);
                if (customPiece == null) {
                    sb.append("|   ");
                } else {
                    sb.append("| ");
                    if (PieceType.PAWN.getCode().equals(customPiece.getCode())) {
                        sb.append("P ");
                    } else {
                        sb.append(customPiece.getCode()).append(" ");
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
        Map<Point, Set<CustomPiece>> threats = Colour.WHITE.equals(colour) ? wThreats : bThreats;
        StringBuilder sb = new StringBuilder();
        for (int y = this.length - 1; y >= 0; y--) {
            for (int x = 0; x <= this.width - 1; x++) {
                boolean hasThreat =
                        threats.get(new Point(x, y)) != null && !threats.get(new Point(x, y)).isEmpty();
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

    public boolean isValidLocation(int x, int y) {
        return this.pieceMap.isInBounds(x, y);
    }

    public boolean isValidLocation(Point point) {
        return this.pieceMap.isInBounds(point.getX(), point.getY());
    }

}
