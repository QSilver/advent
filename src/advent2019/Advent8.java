package advent2019;

import util.Util;

import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

//1848 too low

public class Advent8 {
    public static void main(String[] args) {
        System.out.println(solve());
    }

    private static String solve() {
        String input = Util.fileStream("advent2019/advent8")
                           .findFirst()
                           .get();

        Image image = new Image();
        for (int i = 0; i < input.length(); i++) {
            image.addPixel(input.charAt(i) + "", i);
        }

        Layer layer = new Layer();
        for (int i  = 0; i < 150; i++) {
            int line = i / 25;
            int pos = i % 25;

            for (int j = 0; j < image.getLayers().size(); j++) {
                String pixel = image.getLayers().get(j).getLines().get(line).getPixels().get(pos);
                if (!pixel.equals("2")) {
                    layer.addPixel(pixel, line * 25 + pos);
                    break;
                }
            }
        }

       return layer.toString();
    }
}

class Image {
    List<Layer> getLayers() {
        return layers;
    }

    private final List<Layer> layers = newArrayList();

    void addPixel(String pixel, int index) {
        int layerIndex = index / 25 / 6;
        if (layerIndex >= layers.size()) {
            layers.add(new Layer());
        }
        layers.get(layerIndex).addPixel(pixel, index % 150);
    }

    Layer layerMin0() {
        int min = Integer.MAX_VALUE;
        Layer savedLayer = null;
        for (Layer layer : layers) {
            if (layer.numDigitInLayer("0") < min) {
                savedLayer = layer;
                min = layer.numDigitInLayer("0");
            }
        }
        return savedLayer;
    }
}

class Layer {
    List<Line> getLines() {
        return lines;
    }

    private final List<Line> lines = newArrayList();

    void addPixel(String pixel, int index) {
        int lineIndex = index / 25;
        if (lineIndex >= lines.size()) {
            lines.add(new Line());
        }
        lines.get(lineIndex).addPixel(pixel);
    }

    int numDigitInLayer(String digit) {
        return lines.stream().map(line -> line.numDigitInLine(digit)).mapToInt(value -> value).sum();
    }

    @Override
    public String toString() {
        return lines.stream().map(Line::toString).collect(Collectors.joining("\n"));
    }
}

class Line {
    List<String> getPixels() {
        return pixels;
    }

    private final List<String> pixels = newArrayList();

    void addPixel(String pixel) {
        pixels.add(pixel);
    }

    int numDigitInLine(String digit) {
        return (int) pixels.stream().filter(digit::equals).count();
    }

    @Override
    public String toString() {
        return String.join("", pixels);
    }
}
