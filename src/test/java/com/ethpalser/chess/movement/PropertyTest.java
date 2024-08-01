package com.ethpalser.chess.movement;

import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.custom.condition.Property;
import com.ethpalser.chess.piece.custom.Piece;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class PropertyTest {

    @Test
    void fetch_missingFieldFromPiece_isNull() {
        // Given
        String fieldName = "a";
        Property<Piece> property = new Property<>(fieldName);
        Piece piece = new Piece();
        // When
        Object result = property.fetch(piece);
        // Then
        assertNull(result);
    }

    @Test
    void fetch_existingFieldWithGetterIncorrectCaseFromPiece_isNull() {
        // Given
        String fieldName = "HaSmOvEd";
        Property<Piece> property = new Property<>(fieldName);
        Piece piece = new Piece();
        // When
        Object result = property.fetch(piece);
        // Then
        assertNull(result);
    }

    @Test
    void fetch_existingFieldWithGetterFromPiece_isNotNull() {
        // Given
        String fieldName = "hasMoved";
        Property<Piece> property = new Property<>(fieldName);
        Piece piece = new Piece();
        // When
        Object result = property.fetch(piece);
        // Then
        assertNotNull(result);
    }

    @Test
    void fetch_existingBooleanWithGetterFromPiece_isBoolean() {
        // Given
        String fieldName = "hasMoved";
        Property<Piece> property = new Property<>(fieldName);
        Piece piece = new Piece();
        // When
        Object result = property.fetch(piece);
        // Then
        assertEquals(Boolean.class, result.getClass());
    }

    @Test
    void fetch_existingColourWithGetterFromPiece_isBoolean() {
        // Given
        String fieldName = "colour";
        Property<Piece> property = new Property<>(fieldName);
        Piece piece = new Piece();
        // When
        Object result = property.fetch(piece);
        // Then
        assertEquals(Colour.class, result.getClass());
    }

}
