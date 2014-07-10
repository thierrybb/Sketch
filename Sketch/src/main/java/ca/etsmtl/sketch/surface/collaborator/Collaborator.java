package ca.etsmtl.sketch.surface.collaborator;

public class Collaborator {

    private String name;
    private int colors;
    private int id;

    public Collaborator(int id, int colors, String name) {
        this.id = id;
        this.colors = colors;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return colors;
    }
}
