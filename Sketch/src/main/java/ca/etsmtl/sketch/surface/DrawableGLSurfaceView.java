package ca.etsmtl.sketch.surface;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.widget.Toast;

import java.io.IOException;
import java.net.Socket;
import java.util.Stack;

import ca.etsmtl.sketch.R;
import ca.etsmtl.sketch.common.bus.builder.RemoteBusBuilder;
import ca.etsmtl.sketch.common.bus.component.UniqueIDGenerator;
import ca.etsmtl.sketch.common.bus.event.OnClientDisconnected;
import ca.etsmtl.sketch.common.event.OnNewClientConnected;
import ca.etsmtl.sketch.common.bus.eventbus.EventBus;
import ca.etsmtl.sketch.common.bus.eventbus.IDReceiverDecorator;
import ca.etsmtl.sketch.common.bus.eventbus.SimpleEventBus;
import ca.etsmtl.sketch.common.bus.eventbus.Subscribe;
import ca.etsmtl.sketch.common.event.OnInkStrokeAdded;
import ca.etsmtl.sketch.common.event.OnInkStrokeReAdded;
import ca.etsmtl.sketch.common.event.OnInkStrokeRemoved;
import ca.etsmtl.sketch.eventbus.UIThreadEventBusDecorator;
import ca.etsmtl.sketch.surface.command.AddInkStroke;
import ca.etsmtl.sketch.surface.command.DrawingCommand;
import ca.etsmtl.sketch.surface.openglshape.Drawing;
import ca.etsmtl.sketch.surface.openglshape.Shape;
import ca.etsmtl.sketch.surface.touch.InkPencilTouchStrategy;
import ca.etsmtl.sketch.surface.touch.OpenGLInkModeTouchComponent;
import ca.etsmtl.sketch.surface.touch.StrategyTouchListenerDelegator;
import ca.etsmtl.sketch.ui.dialog.CollaboratorsDialog;
import ca.etsmtl.sketch.utils.ColorGenerator;
import ca.etsmtl.sketch.utils.UserUtils;

public class DrawableGLSurfaceView extends GLSurfaceView {
    private DrawingRenderer drawingRenderer;
    private EventBus bus;
    private Drawing drawing = new Drawing();

    private int currentUserID;

    private Stack<DrawingCommand> undoCommands = new Stack<DrawingCommand>();
    private Stack<DrawingCommand> redoCommands = new Stack<DrawingCommand>();

    private OpenGLInkModeTouchComponent inkModeTouchComponent;
    private ProgressDialog connectionDialog;
    private int currentStrokeColor = 0;
    private UniqueIDGenerator newShapeIDGenerator = new UniqueIDGenerator();

    private CollaboratorsCollection collaborators = new CollaboratorsCollection();
    private InkPencilTouchStrategy inkPencilTouchStrategy;
    private StrategyTouchListenerDelegator onTouchListener;

    private ColorGenerator colorGenerator = new ColorGenerator();

    public DrawableGLSurfaceView(Context context) {
        super(context);
        init();
    }

    public DrawableGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        drawing.attachListener(new Shape.ShapeListener() {
            @Override
            public void onShapeChanged() {
                DrawableGLSurfaceView.this.post(new Runnable() {
                    @Override
                    public void run() {
                        requestRender();
                        invalidate();
                    }
                });
            }
        });

        connectionDialog = ProgressDialog.show(this.getContext(),
                this.getResources().getString(R.string.connection_dialog_title),
                this.getResources().getString(R.string.connection_dialog_message), true);

        drawingRenderer = new DrawingRenderer(drawing);
        setRenderer(drawingRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        createEventBus();
    }

    private void createEventBus() {
        new Thread() {
            @Override
            public void run() {
                try {
                    RemoteBusBuilder remoteBusBuilder = new RemoteBusBuilder();
                    Socket socket = new Socket("192.168.2.125", 11112);

                    IDReceiverDecorator decorator = new IDReceiverDecorator(new UIThreadEventBusDecorator(new SimpleEventBus(), DrawableGLSurfaceView.this));
                    bus = remoteBusBuilder.setSocket(socket)
                            .setDecoratedBus(decorator)
                            .build();
                    registerToEventbus();

                    currentUserID = decorator.getId();

                    initAfterEventbusConnected();
                } catch (IOException e) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DrawableGLSurfaceView.this.getContext(),
                                    R.string.unable_to_connect_to_server, Toast.LENGTH_LONG).show();
                            DrawableGLSurfaceView.this.setEnabled(false);
                            DrawableGLSurfaceView.this.setBackgroundColor(Color.GRAY);
                        }
                    });
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } finally {
                    connectionDialog.cancel();
                }
            }
        }.start();
    }

    private void initAfterEventbusConnected() throws NoSuchMethodException {
        inkPencilTouchStrategy = new InkPencilTouchStrategy(currentUserID, bus);
        onTouchListener = new StrategyTouchListenerDelegator(inkPencilTouchStrategy);
        setOnTouchListener(onTouchListener);

        bus.post(new OnNewClientConnected(UserUtils.getUsername(this.getContext()), currentUserID));

        inkModeTouchComponent = new OpenGLInkModeTouchComponent(drawing, currentUserID, newShapeIDGenerator, collaborators);
        inkModeTouchComponent.plug(bus);
    }

    private void registerToEventbus() throws NoSuchMethodException {
        bus.register(this, OnInkStrokeAdded.class);
        bus.register(this, OnNewClientConnected.class);
        bus.register(this, OnInkStrokeRemoved.class);
        bus.register(this, OnInkStrokeReAdded.class);
        bus.register(this, OnClientDisconnected.class);
    }

    @Subscribe
    public void onInkDrawingAdded(OnInkStrokeAdded event) {
        AddInkStroke command = new AddInkStroke(event.getPoints(), event.getStrokeColor(),
                event.getUserID(), event.getUniqueID());
        executeCommand(command);
    }

    @Subscribe
    public void onInkStrokeReAdded(OnInkStrokeReAdded event) {
        AddInkStroke command = new AddInkStroke(event.getPoints(), event.getStrokeColor(),
                event.getUserID(), event.getUniqueID());
        executeWithoutSaveIt(command);
    }

    private void executeCommand(DrawingCommand command) {
        undoCommands.add(command);
        redoCommands.clear();
        executeWithoutSaveIt(command);
    }

    private void executeWithoutSaveIt(DrawingCommand command) {
        command.execute(drawing, bus);
    }

    @Subscribe
    public void onNewUserAdded(OnNewClientConnected event) {
        if (event.getUserId() != currentUserID && !collaborators.containsKey(event.getUserId())) {
            String message = String.format(getContext().getString(R.string.new_user_connected), event.getName());
            Toast.makeText(this.getContext(), message, Toast.LENGTH_LONG).show();
            collaborators.put(event.getUserId(), new Collaborator(event.getUserId(), colorGenerator.next(),
                    event.getName()));

            bus.post(new OnNewClientConnected(UserUtils.getUsername(DrawableGLSurfaceView.this.getContext()), currentUserID));
        }
    }

    @Subscribe
    public void onUserDisconnected(OnClientDisconnected event) {
        if (collaborators.containsKey(event.getUserID())) {
            Collaborator leavedCollaborator = collaborators.get(event.getUserID());
            String message = String.format(getContext().getString(R.string.user_disconnected), leavedCollaborator.getName());
            Toast.makeText(this.getContext(), message, Toast.LENGTH_LONG).show();
            collaborators.remove(event.getUserID());
        }
    }

    @Subscribe
    public void onInkStrokeRemoved(OnInkStrokeRemoved event) {
        drawing.removeShape(event.getUniqueID(), event.getUserID());
    }

    public void setStrokeColor(int strokeColor) {
        currentStrokeColor = strokeColor;
        inkModeTouchComponent.setStrokeColor(strokeColor);
    }

    /* TODO Refactor that into external of the surface */

    public void redo() {
        if (!redoCommands.isEmpty()) {
            DrawingCommand pop = redoCommands.pop();
            undoCommands.add(pop);
            pop.redo(drawing, bus);
        }
    }

    public void undo() {
        if (!undoCommands.isEmpty()) {
            DrawingCommand pop = undoCommands.pop();
            pop.undo(drawing, bus);
            redoCommands.add(pop);
        }
    }

    public void showCollaborators() {
        if (collaborators.isEmpty()) {
            Toast.makeText(this.getContext(), getContext().getString(R.string.no_collaborator_message), Toast.LENGTH_LONG).show();
        } else {
            CollaboratorsDialog dialog = new CollaboratorsDialog(this.getContext(), collaborators);
            dialog.show();
        }
    }
}
