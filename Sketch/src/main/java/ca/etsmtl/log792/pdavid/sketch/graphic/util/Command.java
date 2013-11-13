package ca.etsmtl.log792.pdavid.sketch.graphic.util;

import java.util.ArrayList;

import ca.etsmtl.log792.pdavid.sketch.graphic.shape.Stroke;

public class Command {

    ArrayList<Stroke> strokes;
    boolean add;

    public Command(boolean add) {
        this.add = add;
        strokes = new ArrayList<Stroke>();
    }

    public Command(ArrayList<Stroke> ss, boolean add) {
        this.strokes = new ArrayList<Stroke>();
        for (Stroke s : ss) {
            strokes.add(s);
        }
        this.add = add;
    }
}
