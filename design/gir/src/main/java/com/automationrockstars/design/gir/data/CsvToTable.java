/*
 * <!--
 *     Copyright (c) 2015-2019 Automation RockStars Ltd.
 *     All rights reserved. This program and the accompanying materials
 *     are made available under the terms of the Apache License v2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Contributors:
 *         Automation RockStars
 *  -->
 */
package com.automationrockstars.design.gir.data;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class CsvToTable {

    public static final String HORIZONTAL = "_horizontal";
    public static final String VERTICAL = "_vertical";
    public static final CsvPreference TABLE_PREFERENCE = new CsvPreference.Builder('"', '|', "\n").build();
    private static final Logger LOG = LoggerFactory.getLogger(CsvToTable.class);

    public static List<List<String>> readCsv(Path csvPath, CsvPreference preference) {

        List<List<String>> allData = Lists.newArrayList();
        Reader reader = null;
        try {
            reader = Files.newBufferedReader(csvPath, Charset.defaultCharset());
            CsvListReader csv = new CsvListReader(reader, preference);
            List<String> line = null;

            while ((line = csv.read()) != null) {
                allData.add(line);
            }
            csv.close();

        } catch (IOException e) {
            LOG.error("Problem reading CSV file {}", csvPath, e);
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException ignore) {

            }
        }
        return allData;
    }

    public static List<List<String>> readCsv(Path csvPath, char delimiter) {
        return readCsv(csvPath, new CsvPreference.Builder('\"', delimiter, "\n").build());
    }

    public static List<List<String>> readCsv(Path csvPath) {
        return readCsv(csvPath, CsvPreference.EXCEL_PREFERENCE);
    }

    public static List<List<String>> transform(List<List<String>> data) {
        List<List<String>> allData = Lists.newArrayList();
        for (int i = 0; i < data.get(0).size(); i++) {
            allData.add(new ArrayList<String>());
        }
        for (List<String> row : data) {
            for (int i = 0; i < row.size(); i++) {
                allData.get(i).add(row.get(i));
            }
        }
        return allData;
    }

    public static void writeAsTable(List<List<String>> data, Path tableFile) {

        Writer writer;
        try {
            Files.deleteIfExists(tableFile);
            writer = Files.newBufferedWriter(tableFile, Charset.defaultCharset(), StandardOpenOption.CREATE_NEW);
            CsvListWriter table = new CsvListWriter(writer, TABLE_PREFERENCE);
            for (List<String> row : data) {
                table.write(row);
            }
            table.close();
        } catch (IOException e) {
            LOG.error("Cannot write table file {}", tableFile, e);
        }

    }
}
