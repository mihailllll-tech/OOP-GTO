package ru.nsu.kurumun.blackjack;

/** Фаза раунда либо его окончательный исход. */
public enum RoundState {
    /** Игрок выбирает добор или остановку. */
    PLAYER_TURN(false),
    /** Дилер раскрывает карты и добирает до порога. */
    DEALER_TURN(false),
    /** Раунд завершён победой игрока. */
    PLAYER_WIN(true),
    /** Раунд завершён победой дилера. */
    DEALER_WIN(true),
    /** Раунд завершён ничьей. */
    DRAW(true);

    private final boolean finished;

    RoundState(boolean finished) {
        this.finished = finished;
    }

    /**
     * Проверяет, является ли состояние окончательным исходом.
     */
    public boolean isFinished() {
        return finished;
    }
}
