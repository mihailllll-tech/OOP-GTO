package ru.nsu.kurumun.gradebook.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Хранит записи об успеваемости студента.
 */
public class GradeBook {
    private final List<Entry> entries = new ArrayList<>();
    private final boolean paidTuition;
    private int currentSemester;
    // До защиты ВКР оценка равна null.
    private Grade thesisGrade;

    /**
     * Создаёт пустую зачётку.
     * @param paidTuition признак обучения на платной основе
     * @param currentSemester номер текущего семестра
     */
    public GradeBook(boolean paidTuition, int currentSemester) {
        this.paidTuition = paidTuition;
        this.currentSemester = currentSemester;
    }

    /**
     * Добавляет оценку за экзамен или дифзачёт.
     * @param subject название предмета
     * @param semester семестр, в котором получена оценка
     * @param type форма контроля
     * @param grade полученная оценка
     */
    public void addGrade(String subject, int semester, AssessmentType type, Grade grade) {
        entries.add(new Entry(subject.strip(), semester, type, grade));
    }

    /**
     * Записывает результат защиты ВКР.
     * @param grade оценка за ВКР
     */
    public void setThesisGrade(Grade grade) {
        thesisGrade = grade;
    }

    /**
     * Увеличивает номер текущего семестра на один.
     */
    public void startNextSemester() {
        currentSemester++;
    }

    /**
     * Возвращает неизменяемую копию записей для анализатора.
     */
    List<Entry> entries() {
        return List.copyOf(entries);
    }

    boolean isPaidTuition() {
        return paidTuition;
    }

    int currentSemester() {
        return currentSemester;
    }

    Grade thesisGrade() {
        return thesisGrade;
    }

    /**
     * Одна запись об оценке по предмету.
     */
    record Entry(String subject, int semester, AssessmentType type, Grade grade) {
    }
}
