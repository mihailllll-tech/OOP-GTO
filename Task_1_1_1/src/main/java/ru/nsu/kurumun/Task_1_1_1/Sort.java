package ru.nsu.kurumun.Task_1_1_1;

public class Sort {
    static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
    static void hippo(int[] arr, int n, int i) {
        int larg = i;
        int lt = 2*i + 1;
        int rt = 2*i + 2;

        if (lt<n && arr[lt]>arr[larg]) larg = lt;
        if (rt<n && arr[rt]>arr[larg]) larg = rt;

        if (larg != i) {
            swap(arr, i, larg);
            hippo(arr, n, larg);
        }
    }
    public static void sort(int[] arr) {
        int n = arr.length;
        for (int i = n / 2 - 1; i >= 0; i--) hippo(arr, n, i);
        for (int i = n - 1; i > 0; i--) {
            swap(arr, 0, i);
            hippo(arr, i, 0);
        }
    }

}
