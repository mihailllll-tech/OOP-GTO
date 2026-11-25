package ru.nsu.kurumun.gradebook.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ru.nsu.kurumun.gradebook.model.GradeBook.Entry;

/**
 * Выполняет расчёты по данным зачётной книжки.
 */
public final class GradeBookAnalyzer {
    private static final int SESSIONS_FOR_TRANSFER = 2;
    private static final int FIRST_SCHOLARSHIP_SEMESTER = 3;
    private static final int HONORS_EXCELLENT_PERCENT = 75;
    private static final int PERCENT_SCALE = 100;


    /**
     * Считает средний балл по экзаменам и дифзачётам.
     * @param book зачётка
     * @return средний балл для зачётки
     */
    public static double averageGrade(GradeBook book) {
        List<Entry> entries = book.entries();
        if (entries.isEmpty()) {
            return 0.0;
        }

        long sum = 0;
        for (Entry entry : entries) {
            sum += entry.grade().score();
        }
        return (double) sum / entries.size();
    }

    /**
     * Проверяет оценки за экзамены двух завершённых сессий для перевода.
     * @param book зачётка
     * @return выполнены ли условия перевода для платника
     */
    public static boolean canTransferToBudget(GradeBook book) {
        int currentSemester = book.currentSemester();
        if (!book.isPaidTuition() || currentSemester <= SESSIONS_FOR_TRANSFER) {
            return false;
        }

        int firstSemester = currentSemester - SESSIONS_FOR_TRANSFER;
        Set<Integer> semestersWithGrades = new HashSet<>();
        for (Entry entry : book.entries()) {
            int semester = entry.semester();
            if (semester < firstSemester || semester >= currentSemester) {
                continue;
            }

            semestersWithGrades.add(semester);
            if (entry.type() == AssessmentType.EXAM && !entry.grade().isGoodOrExcellent()) {
                return false;
            }
        }
        return semestersWithGrades.size() == SESSIONS_FOR_TRANSFER;
    }

    /**
     * Прогнозирует диплом с отличием по последним оценкам и результату дипломной.
     * @param book зачётка
     * @param remainingSubjects число предметов, которые ещё предстоит сдать
     * @return возможен ли диплом, если оставшиеся предметы сданы на отлично
     */
    public static boolean canGetHonorsDiploma(GradeBook book, int remainingSubjects) {
        Grade thesisGrade = book.thesisGrade();
        if (thesisGrade != null && thesisGrade != Grade.EXCELLENT) {
            return false;
        }

        Map<String, Entry> latestGrades = latestGradesBySubject(book.entries());

        // Для прогноза считаем, что оставшиеся предметы будут сданы на отлично.
        long excellent = remainingSubjects;
        for (Entry entry : latestGrades.values()) {
            if (!entry.grade().isGoodOrExcellent()) {
                return false;
            }
            if (entry.grade() == Grade.EXCELLENT) {
                excellent++;
            }
        }

        long total = latestGrades.size() + (long) remainingSubjects;
        return total > 0 && excellent * PERCENT_SCALE >= total * HONORS_EXCELLENT_PERCENT;
    }

    /**
     * Оценивает шансы на повышенную стипендию по предыдущей сессии.
     * @param book зачётка
     * @return подходят ли оценки и форма обучения для стипендии
     */
    public static boolean canGetIncreasedScholarship(GradeBook book) {
        int currentSemester = book.currentSemester();
        if (book.isPaidTuition() || currentSemester < FIRST_SCHOLARSHIP_SEMESTER) {
            return false;
        }

        int lastSemester = currentSemester - 1;
        boolean hasGrades = false;
        for (Entry entry : book.entries()) {
            if (entry.semester() != lastSemester) {
                continue;
            }
            if (!entry.grade().isGoodOrExcellent()) {
                return false;
            }
            hasGrades = true;
        }
        return hasGrades;
    }

    /**
     * Выбирает последнюю оценку по каждому предмету.
     */
    private static Map<String, Entry> latestGradesBySubject(List<Entry> entries) {
        Map<String, Entry> latestGrades = new HashMap<>();
        for (Entry entry : entries) {
            latestGrades.merge(entry.subject(), entry, (previous, current) ->
                    current.semester() >= previous.semester() ? current : previous);
        }
        return latestGrades;
    }
}
