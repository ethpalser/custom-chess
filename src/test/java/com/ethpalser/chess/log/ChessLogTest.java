package com.ethpalser.chess.log;

import com.ethpalser.chess.game.event.MoveEvent;
import com.ethpalser.chess.game.log.ChessLog;
import com.ethpalser.chess.move.notation.ChessNotation;
import com.ethpalser.chess.move.notation.ChessRecord;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.Piece;
import com.ethpalser.chess.piece.standard.Pawn;
import com.ethpalser.chess.space.Point;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ChessLogTest {

    @Test
    void testPeek_givenEmpty_thenNull() {
        ChessLog.Entry entry = new ChessLog().peek();
        Assertions.assertNull(entry);
    }

    @Test
    void testPeek_givenNotEmpty_thenNotNull() {
        ChessLog log = new ChessLog();
        Point p1 = new Point(3, 1);
        Point p2 = new Point(3, 3);

        Piece moving = new Pawn(Colour.WHITE, p1); // This will not move in this test
        ChessNotation notation = new ChessNotation(new ChessRecord.Builder(p1, p2, moving, null).build());
        MoveEvent event = new MoveEvent(p1, p2);
        log.push(new ChessLog.Entry(notation, event));

        ChessLog.Entry entry = log.peek();
        Assertions.assertNotNull(entry);
    }

    @Test
    void testPop_givenEmpty_thenNull() {
        ChessLog log = new ChessLog();

        ChessLog.Entry entry = log.pop();
        Assertions.assertNull(entry);
    }

    @Test
    void testPop_givenNotEmpty_thenNotNull() {
        ChessLog log = new ChessLog();
        Point p1 = new Point(3, 1);
        Point p2 = new Point(3, 3);

        Piece moving = new Pawn(Colour.WHITE, p1); // This will not move in this test
        ChessNotation notation = new ChessNotation(new ChessRecord.Builder(p1, p2, moving, null).build());
        MoveEvent event = new MoveEvent(p1, p2);
        log.push(new ChessLog.Entry(notation, event));

        ChessLog.Entry entry = log.pop();
        Assertions.assertNotNull(entry);
    }

    @Test
    void testUndo_givenEmpty_thenNoChange() {
        ChessLog log = new ChessLog();

        log.pop();
        ChessLog.Entry entry = log.peekUndone();
        Assertions.assertNull(entry);
    }

    @Test
    void testUndo_givenOneEntry_thenEmpty() {
        ChessLog log = new ChessLog();
        Point p1 = new Point(3, 1);
        Point p2 = new Point(3, 3);

        Piece moving = new Pawn(Colour.WHITE, p1); // This will not move in this test
        ChessNotation notation = new ChessNotation(new ChessRecord.Builder(p1, p2, moving, null).build());
        MoveEvent event = new MoveEvent(p1, p2);
        ChessLog.Entry move = new ChessLog.Entry(notation, event);
        log.push(move);

        ChessLog.Entry entry = log.pop();
        Assertions.assertEquals(move, entry);

        ChessLog.Entry undo = log.peekUndone();
        Assertions.assertEquals(move, undo);

        ChessLog.Entry peek = log.peek();
        Assertions.assertNull(peek);
    }

    @Test
    void testUndo_givenTwoEntries_thenNotEmpty() {
        ChessLog log = new ChessLog();
        Point p1 = new Point(3, 1);
        Point p2 = new Point(3, 2);
        Point p3 = new Point(3, 3);

        Piece moving = new Pawn(Colour.WHITE, p1); // This will not move in this test
        ChessNotation notation1 = new ChessNotation(new ChessRecord.Builder(p1, p2, moving, null).build());
        MoveEvent event1 = new MoveEvent(p1, p2);
        ChessLog.Entry move1 = new ChessLog.Entry(notation1, event1);
        log.push(move1);

        ChessNotation notation2 = new ChessNotation(new ChessRecord.Builder(p2, p3, moving, null).build());
        MoveEvent event2 = new MoveEvent(p2, p3);
        ChessLog.Entry move2 = new ChessLog.Entry(notation2, event2);
        log.push(move2);

        ChessLog.Entry entry = log.pop();
        Assertions.assertEquals(move2, entry);

        ChessLog.Entry undo = log.peekUndone();
        Assertions.assertEquals(move2, undo);

        ChessLog.Entry peek = log.peek();
        Assertions.assertEquals(move1, peek);
    }

    @Test
    void testRedo_givenEmpty_thenNoChange() {
        // Note: Redo is now a check by the log. A record pushed to the log that matches its most recent undo is a redo
        ChessLog log = new ChessLog();
        Point p1 = new Point(3, 1);
        Point p2 = new Point(3, 3);

        Piece moving = new Pawn(Colour.WHITE, p1); // This will not move in this test
        ChessNotation notation = new ChessNotation(new ChessRecord.Builder(p1, p2, moving, null).build());
        MoveEvent event = new MoveEvent(p1, p2);
        ChessLog.Entry move = new ChessLog.Entry(notation, event);
        log.push(move);

        ChessLog.Entry undo = log.peekUndone();
        Assertions.assertNull(undo);
    }

    @Test
    void testRedo_givenTwoEntries_thenRedoneIsTop() {
        // Note: Redo is now a check by the log. A record pushed to the log that matches its most recent undo is a redo
        ChessLog log = new ChessLog();
        Point p1 = new Point(3, 1);
        Point p2 = new Point(3, 2);
        Point p3 = new Point(3, 3);

        Piece moving = new Pawn(Colour.WHITE, p1); // This will not move in this test
        ChessNotation notation1 = new ChessNotation(new ChessRecord.Builder(p1, p2, moving, null).build());
        MoveEvent event1 = new MoveEvent(p1, p2);
        ChessLog.Entry move1 = new ChessLog.Entry(notation1, event1);
        log.push(move1);

        ChessNotation notation2 = new ChessNotation(new ChessRecord.Builder(p2, p3, moving, null).build());
        MoveEvent event2 = new MoveEvent(p2, p3);
        ChessLog.Entry move2 = new ChessLog.Entry(notation2, event2);
        log.push(move2);

        ChessLog.Entry entry = log.pop();
        Assertions.assertEquals(move2, entry);

        ChessLog.Entry undo = log.peekUndone();
        Assertions.assertEquals(move2, undo);

        ChessLog.Entry peek = log.peek();
        Assertions.assertEquals(move1, peek);

        // Undo again to check for proper redo
        ChessLog.Entry redo = log.pop();

        Assertions.assertNull(log.peek());
        Assertions.assertEquals(redo, log.peekUndone());

        // Apply redo
        log.push(redo);

        Assertions.assertEquals(redo, log.peek());
        Assertions.assertEquals(entry, log.peekUndone()); // This has not been cleared with a proper redo
    }

}
