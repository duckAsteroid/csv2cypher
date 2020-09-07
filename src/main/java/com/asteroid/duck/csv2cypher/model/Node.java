package com.asteroid.duck.csv2cypher.model;

import org.apache.commons.csv.CSVRecord;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Node extends CypherGenerator {

    private final Field idField;
    private final Set<String> idSpace;

    public Node(String label, IdSpaceManager idSpaceManager, List<Field> fields) {
        super(fields.stream().filter(Predicate.not(Field.byType(Field.SpecialType.ID))).collect(Collectors.toList()), label);
        this.idField = fields.stream().filter(Field.byType(Field.SpecialType.ID)).findFirst().orElseThrow();;
        this.idSpace = idSpaceManager.getIdSpace(idField.getIdSpace());
    }


    public String createRowCypher(CSVRecord record, boolean last) {
        // CREATE (Laurence:Person {name:'Laurence Fishburne', born:1961})
        String id = record.get(idField.getRecordName());
        if (!idSpace.add(id)) {
            throw new IllegalArgumentException("Duplicate ID for "+label+"="+id);
        }
        return "CREATE ("+identifier(idField.getIdSpace(),id)+":"+label+" "+cypherProperties(record)+")";
    }


}
