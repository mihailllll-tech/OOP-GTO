package ru.nsu.kurumun.Task_1_1_1;

public class Sort {
    /**
     * меняем 2 элемента местами в массиве
     * кто это читать будет?
     * как глупо было верить в связь....
     * arr массив, в котором производится обмен
     * i индекс 1 элемента
     * j индекс 2 элемента
     */
    static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
    /**
     * восстанавливаем свойство максимальной кучи для дерева с корнем i
     * никто не смеет мне приказывать....
     * рекурсивно просеиваем элемент вниз пока не будет восстановлено
     * свойство кучи: каждый родитель больше или равен своим потомкам
     * arr массив представляющий кучу
     * n размер кучи
     * i индекс корня поддерева которое нужно преобразовать
     */
    static void hippo(int[] arr, int n, int i) {
        int larg = i;
        int lt = 2 * i + 1;
        int rt = 2 * i + 2;

        if (lt<n && arr[lt]>arr[larg]) { larg = lt; }
        if (rt<n && arr[rt]>arr[larg]) { larg = rt; }

        if (larg != i) {
            swap(arr, i, larg);
            hippo(arr, n, larg);
        }
    }

    /**
     * что ты можешь сказать например?
     * у меня в кармане револвер
     * что ты можешь сказать например?
     * меня ищет уже офицер
     */
    public static void sort(int[] arr) {
        int n = arr.length;
        for (int i = n / 2 - 1; i >= 0; i--) { hippo(arr, n, i); }
        for (int i = n - 1; i > 0; i--) {
            swap(arr, 0, i);
            hippo(arr, i, 0);
        }
    }

}
