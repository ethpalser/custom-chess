package com.chess.api.model.movement;

import com.chess.api.model.Colour;
import com.chess.api.model.movement.condition.Property;
import com.chess.api.model.piece.Piece;
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
        assertInstanceOf(Boolean.class, result);
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
        assertInstanceOf(Colour.class, result);
    }

}
