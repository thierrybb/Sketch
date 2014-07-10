package ca.etsmtl.sketch.surface.collaborator;

import java.util.HashMap;

public class CollaboratorsCollection extends HashMap<Integer, Collaborator> {
    public boolean containCollaborator(int userID) {
        return containsKey(userID);
    }
}
