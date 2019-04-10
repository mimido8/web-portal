package com.healthware;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public abstract class SQLRow {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.CONSTRUCTOR)
    public @interface DAAAO {
        String[] value();
    }

    public static <T> T construct(Class<T> type, ResultSet resultSet) throws Exception {
        resultSet.next();
        Constructor<?> mappedCtor = null;
        for (Constructor<?> ctor : type.getDeclaredConstructors()) {
            if (ctor.getAnnotation(DAO.class) != null) {
                mappedCtor = ctor;
                break;
            }
        }
        if (mappedCtor == null) return null;

        String[] ctorColumns = mappedCtor.getDeclaredAnnotation(DAO.class).value();
        List<Object> parameters = new ArrayList<>();
        for (int i = 0; i < mappedCtor.getParameterTypes().length; i++) {
            Class<?> parameterType = mappedCtor.getParameterTypes()[i];
            parameters.add(ResultSet.class.getMethod("get" + parameterType.getSimpleName(), String.class).invoke(resultSet, ctorColumns[i]));
        }

        return (T)mappedCtor.newInstance(parameters);
    }
}
