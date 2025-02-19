package dev.name.util.collections.array;

import dev.name.util.math.Random;

@SuppressWarnings("unused")
public class Arrays implements Random {
    public static int indexOf(final boolean[] arr, final boolean value) {
        for (int i = 0; i < arr.length; i++)
            if (arr[i] == value)
                return i;

        return -1;
    }

    public static int indexOf(final byte[] arr, final byte value) {
        for (int i = 0; i < arr.length; i++)
            if (arr[i] == value)
                return i;

        return -1;
    }

    public static int indexOf(final short[] arr, final short value) {
        for (int i = 0; i < arr.length; i++)
            if (arr[i] == value)
                return i;

        return -1;
    }

    public static int indexOf(final char[] arr, final char value) {
        for (int i = 0; i < arr.length; i++)
            if (arr[i] == value)
                return i;

        return -1;
    }

    public static int indexOf(final int[] arr, final int value) {
        for (int i = 0; i < arr.length; i++)
            if (arr[i] == value)
                return i;

        return -1;
    }

    public static int indexOf(final float[] arr, final float value) {
        for (int i = 0; i < arr.length; i++)
            if (arr[i] == value)
                return i;

        return -1;
    }

    public static int indexOf(final long[] arr, final long value) {
        for (int i = 0; i < arr.length; i++)
            if (arr[i] == value)
                return i;

        return -1;
    }

    public static int indexOf(final double[] arr, final double value) {
        for (int i = 0; i < arr.length; i++)
            if (arr[i] == value)
                return i;

        return -1;
    }

    public static <T> int indexOf(final T[] arr, final T value) {
        for (int i = 0; i < arr.length; i++)
            if (value.equals(arr[i]))
                return i;

        return -1;
    }

    public static void shuffle(final boolean[] arr) {
        final int n = arr.length;

        for (int i = 0; i < n; i++) {
            final boolean temp = arr[i];
            final int j = RANDOM.nextInt(n);
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    public static void shuffle(final byte[] arr) {
        final int n = arr.length;

        for (int i = 0; i < n; i++) {
            final byte temp = arr[i];
            final int j = RANDOM.nextInt(n);
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    public static void shuffle(final short[] arr) {
        final int n = arr.length;

        for (int i = 0; i < n; i++) {
            final short temp = arr[i];
            final int j = RANDOM.nextInt(n);
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    public static void shuffle(final char[] arr) {
        final int n = arr.length;

        for (int i = 0; i < n; i++) {
            final char temp = arr[i];
            final int j = RANDOM.nextInt(n);
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    public static void shuffle(final int[] arr) {
        final int n = arr.length;

        for (int i = 0; i < n; i++) {
            final int temp = arr[i];
            final int j = RANDOM.nextInt(n);
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    public static void shuffle(final float[] arr) {
        final int n = arr.length;

        for (int i = 0; i < n; i++) {
            final float temp = arr[i];
            final int j = RANDOM.nextInt(n);
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    public static void shuffle(final long[] arr) {
        final int n = arr.length;

        for (int i = 0; i < n; i++) {
            final long temp = arr[i];
            final int j = RANDOM.nextInt(n);
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    public static void shuffle(final double[] arr) {
        final int n = arr.length;

        for (int i = 0; i < n; i++) {
            final double temp = arr[i];
            final int j = RANDOM.nextInt(n);
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    public static <T> void shuffle(final T[] arr) {
        final int n = arr.length;

        for (int i = 0; i < n; i++) swap(arr, i, RANDOM.nextInt(n));
    }

    public static <T> void shuffle(final T[] arr, final T[] arr1, final boolean collisions) {
        final int n = arr.length;

        for (int i = 0; i < n; i++) {
            swap(arr, i, RANDOM.nextInt(n));
            swap(arr1, i, RANDOM.nextInt(n));
        }

        if (collisions) return;

        while (true) {
            for (int i = 0; i < n; i++) {
                if (arr[i] != arr1[i]) continue;
                for (int j = 0; j < n; j++) {
                    if (arr[j] == arr1[j]) continue;
                    swap(arr1, i, j);
                    break;
                }
            }

            boolean hasCollisions = false;

            for (int i = 0; i < n; i++)
                if (arr[i] == arr1[i]) {
                    hasCollisions = true;
                    shuffle(arr, arr1, false);
                    break;
                }

            if (!hasCollisions) break;
        }
    }

    private static <T> void swap(final T[] arr, final int i, final int j) {
        final T temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}