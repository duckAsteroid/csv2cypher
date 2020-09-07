package com.asteroid.duck.csv2cypher.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImportArg {
    public enum Type {
        RELATIONSHIPS, NODES
    }

    private final Type type;
    private final String name;
    private final String source;

    public static Type parseType(String type) {
        for(Type t : Type.values()) {
            if(t.name().equalsIgnoreCase(type)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unrecognised type "+type);
    }

    public static ImportArg parseArg(String arg) {
        if (arg.startsWith("--")) {
            arg = arg.substring(2);
            String[] split = arg.split("=");
            if (split.length < 3) {
                throw new IllegalArgumentException("Expected 3 parts in argument");
            }
            return ImportArg.builder().type(parseType(split[0])).name(split[1]).source(split[2]).build();
        }
        throw new IllegalArgumentException("Expected argument starts with --");
    }
}
