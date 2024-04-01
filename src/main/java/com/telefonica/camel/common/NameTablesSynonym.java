package com.telefonica.camel.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum NameTablesSynonym {




    TABLE_1("_1"), TABLE_2("_2");

    private final String tableNumber;

    private static final Logger log = LoggerFactory.getLogger(NameTablesSynonym.class);
    public static String getNewTable(String synonym, String currentTable) {

        log.info("getNewTable called with synonym: " + synonym + ", currentTable: " + currentTable);
        NameTablesSynonym tableNumberOne = NameTablesSynonym.TABLE_1;
        NameTablesSynonym tableNumberTwo = NameTablesSynonym.TABLE_2;

        String newTable = "";

        if (currentTable.equalsIgnoreCase(synonym + tableNumberOne.getTableNumber())) {
            newTable = synonym + tableNumberTwo.getTableNumber();
        } else {
            newTable = synonym + tableNumberOne.getTableNumber();
        }
        log.info("getNewTable returning: " + newTable);
        return newTable;
    }

}
