package ca.etsmtl.sketch.common.provider.shapeserialization;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import org.bson.types.BasicBSONList;

public class MongoDBShapeSerializer implements ShapeSerializer {
    private final DBCollection coll;
    private BasicDBObject doc;
    private BasicBSONList strokes;
    private String drawingID;

    public MongoDBShapeSerializer(DB db, String drawingID) {
        this.drawingID = drawingID;
        coll = db.getCollection("drawings");

        BasicDBObject query = new BasicDBObject("session_id", drawingID);
        DBCursor cursor = coll.find(query);


        try {

            if (!cursor.hasNext()) {
                createDrawing(drawingID);
            } else {
                doc = (BasicDBObject) cursor.next();
                strokes = (BasicBSONList) doc.get("strokes");
            }
        } finally {
            cursor.close();
        }
    }

    private void createDrawing(String sessionID) {
        strokes = new BasicBSONList();
        doc = new BasicDBObject("name", "drawing")
                .append("session_id", sessionID)
                .append("strokes", strokes);
        coll.insert(doc);
    }

    private void save() {
        coll.update(new BasicDBObject().append("session_id", drawingID), doc);
    }

    @Override
    public void serializeInkStroke(float[] strokesPoints, int strokeColor, int uniqueID, int userID) {
        BasicBSONList points = new BasicBSONList();

        for (int i = 0; i < strokesPoints.length; i++) {
            points.add(new BasicDBObject("value", strokesPoints[i]));
        }

        BasicDBObject newStroke = new BasicDBObject("id", uniqueID)
                .append("color", strokeColor)
                .append("userID", userID)
                .append("points", points);
        strokes.add(newStroke);
        save();
    }

    @Override
    public void pullAllInkStroke(InkStoreReaderStrategy strategy) {
        for (int i = 0; i < strokes.size(); i++) {
            BasicDBObject stroke = (BasicDBObject) strokes.get(i);

            int color = stroke.getInt("color");
            int id = stroke.getInt("id");
            int userID = stroke.getInt("userID");
            BasicBSONList points = (BasicBSONList) stroke.get("points");
            float[] strokePoints = new float[points.size()];

            for (int j = 0; j < points.size(); j++) {
                BasicDBObject point = (BasicDBObject) points.get(j);
                strokePoints[j] = point.getInt("value");
            }

            strategy.readStroke(strokePoints, color, id, userID);
        }
    }

    @Override
    public void removeStroke(int shapeID, int userID) {
        int indexToRemove = -1;
        int i = 0;

        while (indexToRemove == -1 && i < strokes.size()) {
            BasicDBObject stroke = (BasicDBObject) strokes.get(i++);

            int id = stroke.getInt("id");
            int currentUserID = stroke.getInt("userID");

            if (id == shapeID && userID == currentUserID) {
                indexToRemove = i;
            }
        }

        if (indexToRemove != -1) {
            strokes.remove(indexToRemove);
            save();
        }
    }
}