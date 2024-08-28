package com.ethpalser.chess.log;

import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.standard.Pawn;
import com.ethpalser.chess.space.Point;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ChessLogTest {

    @Test
    void testPeek_givenEmpty_thenNull() {
        ChessLog log = new ChessLog();

        LogEntry<Point, Piece> entry = log.peek();
        Assertions.assertNull(entry);
    }

    @Test
    void testPeek_givenNotEmpty_thenNotNull() {
        ChessLog log = new ChessLog();
        Point p1 = new Point(3, 1);
        Point p2 = new Point(3, 3);
        log.push(new ChessLogEntry(p1, p2, new Pawn(Colour.WHITE, p1), new Pawn(Colour.WHITE, p2)));

        LogEntry<Point, Piece> entry = log.peek();
        Assertions.assertNotNull(entry);
    }

    @Test
    void testPop_givenEmpty_thenNull() {
        ChessLog log = new ChessLog();

        LogEntry<Point, Piece> entry = log.pop();
        Assertions.assertNull(entry);
    }

    @Test
    void testPop_givenNotEmpty_thenNotNull() {
        ChessLog log = new ChessLog();
        Point p1 = new Point(3, 1);
        Point p2 = new Point(3, 3);
        log.push(new ChessLogEntry(p1, p2, new Pawn(Colour.WHITE, p1), new Pawn(Colour.WHITE, p2)));

        LogEntry<Point, Piece> entry = log.pop();
        Assertions.assertNotNull(entry);
    }

    @Test
    void testUndo_givenEmpty_thenNoChange() {
        ChessLog log = new ChessLog();

        LogEntry<Point, Piece> entry = log.undo();
        Assertions.assertNull(entry);
    }

    @Test
    void testUndo_givenOneEntry_thenEmpty() {
        ChessLog log = new ChessLog();
        Point p1 = new Point(3, 1);
        Point p2 = new Point(3, 3);
        LogEntry<Point, Piece> move = new ChessLogEntry(p1, p2, new Pawn(Colour.WHITE, p1), new Pawn(Colour.WHITE, p2));
        log.push(move);

        LogEntry<Point, Piece> entry = log.undo();
        Assertions.assertEquals(move, entry);

        LogEntry<Point, Piece> peek = log.peek();
        Assertions.assertNull(peek);
    }

    @Test
    void testUndo_givenTwoEntries_thenNotEmpty() {
        ChessLog log = new ChessLog();
        Point p1 = new Point(3, 1);
        Point p2 = new Point(3, 2);
        Point p3 = new Point(3, 3);
        LogEntry<Point, Piece> move1 = new ChessLogEntry(p1, p2, new Pawn(Colour.WHITE, p1), new Pawn(Colour.WHITE, p2));
        LogEntry<Point, Piece> move2 = new ChessLogEntry(p1, p2, new Pawn(Colour.WHITE, p2), new Pawn(Colour.WHITE, p3));
        log.push(move1);
        log.push(move2);

        LogEntry<Point, Piece> entry = log.undo();
        Assertions.assertEquals(move2, entry);

        LogEntry<Point, Piece> peek = log.peek();
        Assertions.assertNotNull(peek);
        Assertions.assertEquals(move1, peek);
    }

    @Test
    void testRedo_givenEmpty_thenNoChange() {
        ChessLog log = new ChessLog();
        Point p1 = new Point(3, 1);
        Point p2 = new Point(3, 3);
        log.push(new ChessLogEntry(p1, p2, new Pawn(Colour.WHITE, p1), new Pawn(Colour.WHITE, p2)));

        LogEntry<Point, Piece> entry = log.redo();
        Assertions.assertNull(entry);
    }

    @Test
    void testRedo_givenOneEntry_thenRedoneIsTop() {
        ChessLog log = new ChessLog();
        Point p1 = new Point(3, 1);
        Point p2 = new Point(3, 2);
        Point p3 = new Point(3, 3);
        LogEntry<Point, Piece> move1 = new ChessLogEntry(p1, p2, new Pawn(Colour.WHITE, p1), new Pawn(Colour.WHITE, p2));
        LogEntry<Point, Piece> move2 = new ChessLogEntry(p1, p2, new Pawn(Colour.WHITE, p2), new Pawn(Colour.WHITE, p3));
        log.push(move1);
        log.push(move2);

        LogEntry<Point, Piece> entry = log.undo();
        Assertions.assertEquals(move2, entry);

        LogEntry<Point, Piece> redo = log.redo();
        Assertions.assertNotNull(redo);
        Assertions.assertEquals(move2, redo);
    }

}
