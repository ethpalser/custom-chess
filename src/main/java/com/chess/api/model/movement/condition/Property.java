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

        Class<?> objCls = obj.getClass();
        String methodName = "get" + field.getName();
        try {
            Method method = objCls.getMethod(methodName);
            return method.invoke(obj);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
            return null;
        }
    }

}
