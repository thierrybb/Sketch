package ca.etsmtl.sketch.surface.collaborator;

import android.content.Context;
import android.widget.Toast;

import java.util.Map;

import ca.etsmtl.sketch.R;
import ca.etsmtl.sketch.common.bus.event.OnClientDisconnected;
import ca.etsmtl.sketch.common.bus.eventbus.EventBus;
import ca.etsmtl.sketch.common.bus.eventbus.Subscribe;
import ca.etsmtl.sketch.common.event.OnClientKeepAlive;
import ca.etsmtl.sketch.common.event.OnNewClientConnected;
import ca.etsmtl.sketch.utils.ColorGenerator;
import ca.etsmtl.sketch.utils.UserUtils;

public class CollaboratorComponent {

    private int currentUserID;
    private CollaboratorsCollection collaborators;
    private ColorGenerator colorGenerator = new ColorGenerator();
    private Context context;
    private EventBus bus;

    public CollaboratorComponent(int currentUserID, CollaboratorsCollection collaborators, Context context) {
        this.currentUserID = currentUserID;
        this.collaborators = collaborators;
        this.context = context;
    }

    public void plugInto(EventBus bus) throws NoSuchMethodException {
        bus.register(this, OnNewClientConnected.class);
        bus.register(this, OnClientDisconnected.class);
        bus.register(this, OnClientKeepAlive.class);

        this.bus = bus;
        bus.post(new OnNewClientConnected(UserUtils.getUsername(context), currentUserID));
    }

    @Subscribe
    public void onNewUserAdded(OnNewClientConnected event) {
        if (event.getUserId() != currentUserID && !collaborators.containsKey(event.getUserId())) {
            String message = String.format(context.getString(R.string.new_user_connected), event.getName());
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            collaborators.put(event.getUserId(), new Collaborator(event.getUserId(), colorGenerator.next(),
                    event.getName()));
            bus.post(new OnClientKeepAlive(UserUtils.getUsername(context), currentUserID));
        }
    }

    @Subscribe
    public void onClientKeepAlive(OnClientKeepAlive event) {
        if (!collaborators.containCollaborator(event.getUserId())) {
            onNewUserAdded(event);
        }
    }

    @Subscribe
    public void onUserDisconnected(OnClientDisconnected event) {
        if (collaborators.containsKey(event.getUserID())) {
            Collaborator leavedCollaborator = collaborators.get(event.getUserID());
            String message = String.format(context.getString(R.string.user_disconnected), leavedCollaborator.getName());
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            collaborators.remove(event.getUserID());
        }
    }
}
