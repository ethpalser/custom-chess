package com.ethpalser.chess.game;

import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.space.Point;
import java.util.List;
import java.util.stream.Collectors;

public class MockNode {

    private final Colour colour;
    private final int value;
    private final List<MockNode> next;

    public MockNode(Colour colour, int value, List<MockNode> children) {
        this.colour = colour;
        this.value = value;
        this.next = children;
    }

    public int getValue() {
        return this.value;
    }

    public List<MockNode> getNext() {
        return this.next;
    }

    public List<Action> getChildren() {
        return this.next.stream().map(n -> new Action(Colour.opposite(this.colour), new Point(this.value, 0),
                new Point(n.value, 0))).collect(Collectors.toList());
    }

}
