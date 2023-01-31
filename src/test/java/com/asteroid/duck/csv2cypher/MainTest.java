package com.asteroid.duck.csv2cypher;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

class MainTest {
	@Test
	void integrationTest() throws IOException {

		Main.main(new String[] {
						"--nodes=Person=person.csv",
						"--nodes=Class=class.csv",
						"--relationships=ATTENDS=person-attends-class.csv"
		});
	}
}
