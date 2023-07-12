package de.jplag;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

// could do source code analysis with java parser as well, use NameExpr to extract variable names
public class Main {
    public static void main(String[] args) {
        assert args.length == 1;
        Path dir = Paths.get(args[0]);
        try {
            for (Path path: getJavaFilePaths(dir.toUri())) {
                ParserConfiguration parserConfig = new ParserConfiguration()
                        .setAttributeComments(false).setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17);
                try {
                    StaticJavaParser.setConfiguration(parserConfig); // .setSymbolResolver(new JavaSymbolSolver(new ReflectionTypeSolver())));
                    CompilationUnit cu = StaticJavaParser.parse(Files.readString(path, StandardCharsets.ISO_8859_1));
                    Files.writeString(path, cu.toString());
                } catch (ParseProblemException e) {
                    System.err.print(path + " -- ");
                    error(e);
                }
            }
        } catch (IOException e) {
            error(e);
        }
    }
    
    private static List<Path> getJavaFilePaths(URI uri) throws IOException {
        try (Stream<Path> stream = Files.walk(Paths.get(uri))) {
            return stream.filter(f -> f.toString().toLowerCase().endsWith(".java")).toList();
        }
    }

    private static void error(Exception e) {
        StackTraceElement[] stackTrace = e.getStackTrace();
        int lineNumber = stackTrace[stackTrace.length - 1].getLineNumber();
        System.err.println(lineNumber + ": " + e.getMessage());
        System.exit(-1);
    }
}
