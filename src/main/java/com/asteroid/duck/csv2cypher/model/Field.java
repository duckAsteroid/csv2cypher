package com.asteroid.duck.csv2cypher.model;

import lombok.Builder;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Data
@Builder
public class Field {

    public enum SpecialType {
        ID, START_ID, END_ID
    }

    private final int index;
    private final String name;
    private final String type;
    private final String recordName;

    public static Field parseField(int index, String s) {
        FieldBuilder builder = builder().index(index).recordName(s);
        String[] split = s.split(":");
        if (split.length < 2) {
            builder.name(split[0]).type("string");
        }
        else {
            builder.name(split[0]).type(split[1]);
        }
        return builder.build();
    }

    public Optional<SpecialType> detectSpecialType() {
        for(SpecialType s : SpecialType.values()) {
            if(type.startsWith(s.name())) {
                return Optional.of(s);
            }
        }
        return Optional.empty();
    }

    public boolean isNumeric() {
        if (type.equals("double")) {
            return true;
        }

        return false;
    }

    public String getIdSpace() {
        int begin = type.indexOf('(');
        if (begin > 0) {
           int end = type.indexOf(')', begin);
           if (end > 0) {
               return type.substring(begin, end);
           }
        }
        return null;
    }

    public static Predicate<Field> byType(SpecialType... type) {
        List<SpecialType> types = Arrays.asList(type);
        return field -> field.detectSpecialType().isPresent() && types.contains(field.detectSpecialType().get());
    }
}
