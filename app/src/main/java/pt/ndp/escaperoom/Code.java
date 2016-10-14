package pt.ndp.escaperoom;

/**
 * As part of the escape room, players can enter codes, which are defined in a room's XML file.
 * An instance of this class describes a single code which the game will recognize.
 */
public class Code implements Answerable {
    public String name;
    public int increment;
    public String message;
    private boolean answered;

    public Code(String code, int increment, String message) {
        this.name = code;
        this.increment = increment;
        this.message = message;
        this.answered = false;
    }

    public boolean isAnswered() {
        return this.answered;
    }

    public void setAnswered(boolean value) {
        this.answered = value;
    }
}