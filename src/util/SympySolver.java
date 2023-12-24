package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.joining;

public class SympySolver {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String IMPORT_SYMPY = "from sympy import symbols, Eq, solve\n";

    public static Map<String, Long> solveGeneric(List<String> symbolList, List<Equation> equationList) {
        StringBuilder code = new StringBuilder();

        code.append(IMPORT_SYMPY)
                .append("\n");

        String returnValues = String.join(", ", symbolList);
        String symbolsString = String.join(" ", symbolList);

        code.append(returnValues)
                .append(" = ")
                .append("symbols('")
                .append(symbolsString)
                .append("')\n");

        String equations = equationList.stream()
                .map(Equation::toString)
                .collect(joining(",\n"));

        code.append("equations = (\n")
                .append(equations)
                .append("\n)\n");

        code.append("\nprint(solve(equations, (")
                .append(returnValues)
                .append("), dict=True))");

        return runPython(code.toString());
    }

    private static Map<String, Long> runPython(String pythonCode) {
        String fileName = UUID.randomUUID().toString();
        File file = new File(fileName);
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(pythonCode);
            writer.close();

            Process process = new ProcessBuilder("python", fileName).start();
            String results = CharStreams.toString(new InputStreamReader(process.getInputStream(), Charsets.UTF_8));
            process.waitFor();

            return extractOneSolution(results);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }
    }

    // aggressively extract one solution
    private static Map<String, Long> extractOneSolution(String results) {
        try {
            return MAPPER.readValue(results, getTypeReference()).getFirst();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public record Equation(String substitution, String base) {
        @Override
        public String toString() {
            return "Eq(" + substitution + ", " + base + ")";
        }

        public static Equation build(long a, long b, String p, String d, String t) {
            String replacement = a + "+" + b + "*" + t;
            String base = p + "+" + d + "*" + t;
            return new Equation(replacement, base);
        }
    }

    private static TypeReference<List<Map<String, Long>>> getTypeReference() {
        return new TypeReference<>() {
        };
    }
}
