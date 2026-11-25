package ru.nsu.kurumun.gradebook;

import java.util.Locale;
import ru.nsu.kurumun.gradebook.model.AssessmentType;
import ru.nsu.kurumun.gradebook.model.Grade;
import ru.nsu.kurumun.gradebook.model.GradeBook;
import ru.nsu.kurumun.gradebook.model.GradeBookAnalyzer;

public class Main {
    public static void main(String[] args) {
        GradeBook paidStudent = new GradeBook(true, 3);
        paidStudent.addGrade("Математика", 1, AssessmentType.EXAM, Grade.EXCELLENT);
        paidStudent.addGrade("Английский", 2, AssessmentType.GRADED_CREDIT, Grade.SATISFACTORY);
        paidStudent.addGrade("Программирование", 2, AssessmentType.EXAM, Grade.EXCELLENT);

        System.out.printf(Locale.ROOT, "Средний балл: %.2f%n",
                GradeBookAnalyzer.averageGrade(paidStudent));
        System.out.println("Можно перевестись на бюджет: "
                + GradeBookAnalyzer.canTransferToBudget(paidStudent));

        GradeBook budgetStudent = new GradeBook(false, 3);
        budgetStudent.addGrade("Математика", 1, AssessmentType.EXAM, Grade.EXCELLENT);
        budgetStudent.addGrade("Программирование", 2, AssessmentType.EXAM, Grade.EXCELLENT);
        budgetStudent.addGrade("Алгоритмы", 2, AssessmentType.GRADED_CREDIT, Grade.EXCELLENT);
        budgetStudent.addGrade("Английский", 2, AssessmentType.GRADED_CREDIT, Grade.GOOD);

        System.out.println("Возможен диплом с отличием: "
                + GradeBookAnalyzer.canGetHonorsDiploma(budgetStudent, 0));
        System.out.println("Возможна повышенная стипендия: "
                + GradeBookAnalyzer.canGetIncreasedScholarship(budgetStudent));
    }
}
