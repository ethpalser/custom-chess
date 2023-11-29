package com.chess.api.model.movement.condition;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public record Property<T>(String key) {

    public Object fetch(T obj) {
        if (this.key == null) {
            return null;
        }

        Field field;
        try {
            field = obj.getClass().getDeclaredField(this.key);
        } catch (NoSuchFieldException ex) {
            return null;
        }
        // Uppercase first character of the name to follow getter-method syntax
        String fieldName = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);

        Class<?> objCls = obj.getClass();
        String methodName = "get" + fieldName;
        try {
            Method method = objCls.getMethod(methodName);
            return method.invoke(obj);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
            return null;
        }
    }

}
