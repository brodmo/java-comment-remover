package de.jplag;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


// could do source code analysis with java parser as well, use NameExpr to extract variable names
public class Main {
    public static void main(String[] args) {
        for (String arg: args) {
            try {
                Path dir = Paths.get(arg);
                for (Path path: Files.list(dir).toList()) {
                    if (!path.toString().toLowerCase().endsWith(".java")) continue;
                    try {
                        StaticJavaParser.setConfiguration(new ParserConfiguration().setAttributeComments(false)); // .setSymbolResolver(new JavaSymbolSolver(new ReflectionTypeSolver())));
                        CompilationUnit cu = StaticJavaParser.parse(Files.readString(path));
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
    }

    private static void error(Exception e) {
        StackTraceElement[] stackTrace = e.getStackTrace();
        int lineNumber = stackTrace[stackTrace.length - 1].getLineNumber();
        System.err.println(lineNumber + ": " + e.getMessage());
        System.exit(-1);
    }
}
