package com.telefonica.camel.common;

import com.telefonica.camel.exception.IODirectoryException;
import com.telefonica.camel.exception.IOFileException;
import lombok.experimental.UtilityClass;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.stream.Stream;

@UtilityClass
public class Util {

    public static String getUUID() {
	return UUID.randomUUID().toString().replace("-", "");
    }

    public static String getKnownHostsFile() {
	String os = System.getProperty("os.name");
	String home = System.getProperty("user.home");
	String pathWindows = "\\.ssh\\known_hosts";
	String pathNoWindows = "/.ssh/known_hosts";
	return os.contains("win") ? home + pathWindows : home + pathNoWindows;
    }

    public static String log(String message) {
	return "${exchangeProperty.UUID} " + message;
    }

    public static String replaceLineBreakBySpace(String value) {
	return value.replace("\n", " ");
    }

    public static long counterLinesFile(Path file) {
	long numberLines = 0;
	try (Stream<String> fileStream = Files.lines(file)) {
	    numberLines = fileStream.count();
	} catch (IOException e) {
	    throw new IOFileException("failed accesing file " + file.toAbsolutePath(), e);
	}
	return numberLines;
    }

    public static void printLineFile(Path file, String line) {
	Path badDirectory = file.getParent();
	try {
	    Files.createDirectories(badDirectory);
	} catch (IOException e) {
	    throw new IODirectoryException("failed create directory " + badDirectory.toAbsolutePath(), e);
	}
	try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8, StandardOpenOption.CREATE,
		StandardOpenOption.APPEND)) {
	    writer.append(line);
	} catch (IOException e) {
	    throw new IOFileException("failed write on file " + file.toAbsolutePath(), e);
	}
    }

    public boolean existFile(Path file) {
	File f = new File(file.toAbsolutePath().toString());
	return f.exists() && !f.isDirectory();
    }

}
