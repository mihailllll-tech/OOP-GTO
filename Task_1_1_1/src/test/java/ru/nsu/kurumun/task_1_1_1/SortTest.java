package ru.nsu.kurumun.task_1_1_1;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SortTest {

    @Test
    void sortA() {
        int[] array = {1, 2, 3, 4, 5};
        Sort.sort(array);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, array);
    }

    @Test
    void sortB() {
        int[] array = {5, 4, 3, 2, 1};
        Sort.sort(array);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, array);
    }

    @Test
    void sortC() {
        int[] array = {3, 1, 4, 1, 5, 9, 2, 6, 5};
        Sort.sort(array);
        assertArrayEquals(new int[]{1, 1, 2, 3, 4, 5, 5, 6, 9}, array);
    }

    @Test
    void sortD() {
        int[] array = {};
        Sort.sort(array);
        assertArrayEquals(new int[]{}, array);
    }

    @Test
    void sortE() {
        int[] array = {42};
        Sort.sort(array);
        assertArrayEquals(new int[]{42}, array);
    }

    @Test
    void sortF() {
        int[] array = {-5, 3, -1, 0, 8, -8};
        Sort.sort(array);
        assertArrayEquals(new int[]{-8, -5, -1, 0, 3, 8}, array);
    }

    @Test
    void sortG() {
        int[] array = {7, 7, 7, 7, 7};
        Sort.sort(array);
        assertArrayEquals(new int[]{7, 7, 7, 7, 7}, array);
    }

    @Test
    void sortH() {
        int[] array = {2, 1};
        Sort.sort(array);
        assertArrayEquals(new int[]{1, 2}, array);
    }

    @Test
    void sortI() {
        int[] array = new int[1000];
        for (int i = 0; i < array.length; i++) {
            array[i] = array.length - i;
        }
        Sort.sort(array);
        for (int i = 0; i < array.length; i++) {
            assertEquals(i + 1, array[i]);
        }
    }
}