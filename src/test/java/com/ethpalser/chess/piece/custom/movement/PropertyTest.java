package com.ethpalser.chess.piece.custom.movement;

import com.ethpalser.chess.space.Point;
import com.ethpalser.chess.piece.Colour;
import com.ethpalser.chess.piece.custom.CustomPiece;
import com.ethpalser.chess.piece.custom.PieceType;
import com.ethpalser.chess.piece.custom.condition.Property;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class PropertyTest {

    @Test
    void fetch_missingFieldFromPiece_isNull() {
        // Given
        String fieldName = "a";
        Property<CustomPiece> property = new Property<>(fieldName);
        CustomPiece customPiece = new CustomPiece(PieceType.PAWN, Colour.WHITE, new Point());
        // When
        Object result = property.fetch(customPiece);
        // Then
        assertNull(result);
    }

    @Test
    void fetch_existingFieldWithGetterIncorrectCaseFromPiece_isNull() {
        // Given
        String fieldName = "HaSmOvEd";
        Property<CustomPiece> property = new Property<>(fieldName);
        CustomPiece customPiece = new CustomPiece(PieceType.PAWN, Colour.WHITE, new Point());
        // When
        Object result = property.fetch(customPiece);
        // Then
        assertNull(result);
    }

    @Test
    void fetch_existingFieldWithGetterFromPiece_isNotNull() {
        // Given
        String fieldName = "hasMoved";
        Property<CustomPiece> property = new Property<>(fieldName);
        CustomPiece customPiece = new CustomPiece(PieceType.PAWN, Colour.WHITE, new Point());
        // When
        Object result = property.fetch(customPiece);
        // Then
        assertNotNull(result);
    }

    @Test
    void fetch_existingBooleanWithGetterFromPiece_isBoolean() {
        // Given
        String fieldName = "hasMoved";
        Property<CustomPiece> property = new Property<>(fieldName);
        CustomPiece customPiece = new CustomPiece(PieceType.PAWN, Colour.WHITE, new Point());
        // When
        Object result = property.fetch(customPiece);
        // Then
        assertEquals(Boolean.class, result.getClass());
    }

    @Test
    void fetch_existingColourWithGetterFromPiece_isBoolean() {
        // Given
        String fieldName = "colour";
        Property<CustomPiece> property = new Property<>(fieldName);
        CustomPiece customPiece = new CustomPiece(PieceType.PAWN, Colour.WHITE, new Point());
        // When
        Object result = property.fetch(customPiece);
        // Then
        assertEquals(Colour.class, result.getClass());
    }

}
