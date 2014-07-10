package ca.etsmtl.sketch.surface;

import java.util.HashMap;
import java.util.Map;

public class CollaboratorsCollection extends HashMap<Integer, Collaborator> {
    public boolean containCollaborator(int userID) {
        return containsKey(userID);
    }
}
