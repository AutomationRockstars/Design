/*******************************************************************************
 * Copyright (c) 2015 Automation RockStars Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Automation RockStars - initial API and implementation
 *******************************************************************************/
package com.automationrockstars.design.gir.data;

import static com.google.common.collect.Lists.asList;
import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import com.google.common.io.Resources;

public class CsvToTableTest {

	@Test
	public void should_transformTable() {
		List<List<String>> data = newArrayList();
		String[] rest = {"1","2","3"} ;
		data.add(asList("A", "b",rest));
		data.add(asList("c", "d",rest));
		List<List<String>> expected = newArrayList(
					(List<String>)newArrayList("A","c"),
				newArrayList("b","d"),
				newArrayList("1","1"),
				newArrayList("2","2"),
				newArrayList("3","3"));
		assertThat(CsvToTable.transform(data),equalTo(expected));
	}

	@Test
	public void should_readCsv() throws URISyntaxException {
		URL f = Resources.getResource("data/inOut_vertical.csv");
		List<List<String>> lists = CsvToTable.readCsv(Paths.get(f.toURI()));
		List<List<String>> expected = newArrayList(
			(List<String>)newArrayList("data","t1", "t2"),
			newArrayList("in","a", "aaa"),
			newArrayList("out","a", "aaa"));
		assertThat(lists, equalTo(expected));
	}

	@Test
	public void should_writeToTable() throws URISyntaxException {
		List<List<String>> data = newArrayList();
		String[] rest = {"1","2","3"} ;
		data.add(asList("A", "b",rest));
		data.add(asList("c", "d",rest));
		URL file = Resources.getResource("data/writeToTable.csv");
		CsvToTable.writeAsTable(data, Paths.get(file.toURI()));
		List<List<String>> lists = CsvToTable.readCsv(Paths.get(file.toURI()), '|');
		assertThat(data, equalTo(lists));
	}

}
