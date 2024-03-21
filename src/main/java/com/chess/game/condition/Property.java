package main.java.com.chess.game.condition;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Property<T> {

    private final String key;

    public Property(String key) {
        this.key = key;
    }

    /**
     * Uses the given object and reflection to find the field of the object that matches the key of this Property.
     * This requires using the class's getter method for that field, so if there is no get method or does not begin
     * with "get" this will fail.
     *
     * @param obj Object to fetch the field value from
     * @return Object that matches the field class and value of the object, if it exists. Otherwise, null.
     */
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
