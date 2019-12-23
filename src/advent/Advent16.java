package advent;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

@Slf4j
public class Advent16 {
    private static final ArrayList<Integer> PATTERN = newArrayList(0, 1, 0, -1);

    public static void main(String[] args) {
        solve();
    }

    private static void solve() {
        ArrayList<Integer> fileInput = Arrays.stream(Util.splitLine(Util.fileStream("advent16"))
                .stream()
                .findFirst().map(s -> s.split("")).stream().findFirst().get())
                .mapToInt(Integer::parseInt).collect(ArrayList::new, List::add, List::addAll);

        Object[] repeat = repeat(fileInput.toArray(), fileInput.size() * 10000);

        double[] v = new double[entry.getValue().singleValues.size()];
        int i = 0;
        for(Long v : entry.getValue().singleValues) {
            v[i++] = v.doubleValue();
        }

        Complex[] cinput = new Complex[input.length];
        for (int i = 0; i < input.length; i++)
            cinput[i] = new Complex(input[i], 0.0);

        fft(cinput);

        System.out.println("Results:");
        for (Complex c : cinput) {
            System.out.println(c);
        }

        ArrayList<Integer> input = newArrayList(fileInput);
        for (int phase = 1; phase <= 100; phase++) {
            input = fft(input);
        }
        System.out.println(input);
    }

    private static ArrayList<Integer> fft(ArrayList<Integer> input) {
        ArrayList<Integer> output = newArrayList();

        for (int i = 0; i < input.size(); i++) {
            ArrayList<Integer> patternForStep = newArrayList();
            addToList(patternForStep, PATTERN.get(0), i + 1);
            addToList(patternForStep, PATTERN.get(1), i + 1);
            addToList(patternForStep, PATTERN.get(2), i + 1);
            addToList(patternForStep, PATTERN.get(3), i + 1);

            int sum = 0;
            for (int j = 0; j < input.size(); j++) {
                sum += input.get(j) * patternForStep.get((j + 1) % patternForStep.size());
            }
            output.add(Math.abs(sum % 10));
        }

        return output;
    }

    private static void addToList(ArrayList<Integer> list, int element, int times) {
        for (int i = 1; i <= times; i++) {
            list.add(element);
        }
    }

    public static <T> T[] repeat(T[] arr, int newLength) {
        T[] dup = Arrays.copyOf(arr, newLength);
        for (int last = arr.length; last != 0 && last < newLength; last <<= 1) {
            System.arraycopy(dup, 0, dup, last, Math.min(last << 1, newLength) - last);
        }
        return dup;
    }
}

class FastFourierTransform {

    public static int bitReverse(int n, int bits) {
        int reversedN = n;
        int count = bits - 1;

        n >>= 1;
        while (n > 0) {
            reversedN = (reversedN << 1) | (n & 1);
            count--;
            n >>= 1;
        }

        return ((reversedN << count) & ((1 << bits) - 1));
    }

    static void fft(Complex[] buffer) {

        int bits = (int) (Math.log(buffer.length) / Math.log(2));
        for (int j = 1; j < buffer.length / 2; j++) {

            int swapPos = bitReverse(j, bits);
            Complex temp = buffer[j];
            buffer[j] = buffer[swapPos];
            buffer[swapPos] = temp;
        }

        for (int N = 2; N <= buffer.length; N <<= 1) {
            for (int i = 0; i < buffer.length; i += N) {
                for (int k = 0; k < N / 2; k++) {

                    int evenIndex = i + k;
                    int oddIndex = i + k + (N / 2);
                    Complex even = buffer[evenIndex];
                    Complex odd = buffer[oddIndex];

                    double term = (-2 * Math.PI * k) / (double) N;
                    Complex exp = (new Complex(cos(term), sin(term)).mult(odd));

                    buffer[evenIndex] = even.add(exp);
                    buffer[oddIndex] = even.sub(exp);
                }
            }
        }
    }
}

class Complex {
    public final double re;
    public final double im;

    public Complex() {
        this(0, 0);
    }

    public Complex(double r, double i) {
        re = r;
        im = i;
    }

    public Complex add(Complex b) {
        return new Complex(this.re + b.re, this.im + b.im);
    }

    public Complex sub(Complex b) {
        return new Complex(this.re - b.re, this.im - b.im);
    }

    public Complex mult(Complex b) {
        return new Complex(this.re * b.re - this.im * b.im,
                this.re * b.im + this.im * b.re);
    }

    @Override
    public String toString() {
        return String.format("(%f,%f)", re, im);
    }
}
