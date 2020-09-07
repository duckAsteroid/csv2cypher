package com.asteroid.duck.csv2cypher.model;

import java.util.Set;

public interface IdSpaceManager {
    Set<String> getIdSpace(String idSpaceName);
}
