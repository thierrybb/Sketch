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
import ca.etsmtl.sketch.common.bus.eventbus.EventBus;
import ca.etsmtl.sketch.common.bus.eventbus.IDReceiverDecorator;
import ca.etsmtl.sketch.common.bus.eventbus.SimpleEventBus;
import ca.etsmtl.sketch.common.bus.eventbus.Subscribe;
import ca.etsmtl.sketch.common.event.OnAllStrokeRestored;
import ca.etsmtl.sketch.common.event.OnInkStrokeAdded;
import ca.etsmtl.sketch.common.event.OnInkStrokeErased;
import ca.etsmtl.sketch.common.event.OnInkStrokeReAdded;
import ca.etsmtl.sketch.common.event.OnInkStrokeRemoved;
import ca.etsmtl.sketch.common.event.OnStrokeRestored;
import ca.etsmtl.sketch.common.event.OnSyncRequireEvent;
import ca.etsmtl.sketch.common.utils.UniqueIDGenerator;
import ca.etsmtl.sketch.eventbus.UIThreadEventBusDecorator;
import ca.etsmtl.sketch.surface.collaborator.CollaboratorComponent;
import ca.etsmtl.sketch.surface.collaborator.CollaboratorsCollection;
import ca.etsmtl.sketch.surface.command.AddInkStroke;
import ca.etsmtl.sketch.surface.command.DrawingCommand;
import ca.etsmtl.sketch.surface.command.EraseInkStroke;
import ca.etsmtl.sketch.surface.openglshape.Drawing;
import ca.etsmtl.sketch.surface.openglshape.InkStroke;
import ca.etsmtl.sketch.surface.openglshape.Shape;
import ca.etsmtl.sketch.surface.touch.EraseModeTouchStrategy;
import ca.etsmtl.sketch.surface.touch.FingerMotionMatrixDelegator;
import ca.etsmtl.sketch.surface.touch.InkPencilTouchStrategy;
import ca.etsmtl.sketch.surface.touch.OpenGLInkModeTouchComponent;
import ca.etsmtl.sketch.surface.touch.StrategyTouchListenerDelegator;
import ca.etsmtl.sketch.surface.touch.TouchStrategy;
import ca.etsmtl.sketch.surface.transformation.MatrixWrapper;
import ca.etsmtl.sketch.ui.dialog.CollaboratorsDialog;
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

    private StrategyTouchListenerDelegator drawableTouchDelegator;
    private InkPencilTouchStrategy inkPencilTouchStrategy;
    private TouchStrategy eraseModeStrategy;

    private FingerMotionMatrixDelegator fingerMotionMatrixDelegator;
    private Socket eventBusSocket;

    public DrawableGLSurfaceView(Context context) {
        super(context);
        init();
    }

    public DrawableGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (eventBusSocket.isConnected()) {
            try {
                eventBusSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        super.onDetachedFromWindow();
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

        MatrixWrapper matrix = new MatrixWrapper();
        drawingRenderer = new DrawingRenderer(drawing, matrix);

        fingerMotionMatrixDelegator = new FingerMotionMatrixDelegator(getContext(), matrix);

        drawableTouchDelegator = new StrategyTouchListenerDelegator(matrix);

        setRenderer(drawingRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    public void loadDrawing(final String drawingID, final String serverIP, final int serverPort) {
        new Thread() {
            @Override
            public void run() {
                try {
                    RemoteBusBuilder remoteBusBuilder = new RemoteBusBuilder();
                    eventBusSocket = new Socket(serverIP, serverPort);

                    IDReceiverDecorator decorator = new IDReceiverDecorator(new UIThreadEventBusDecorator(new SimpleEventBus(), DrawableGLSurfaceView.this));
                    bus = remoteBusBuilder.setSocket(eventBusSocket)
                            .setAccount(UserUtils.getUsername(DrawableGLSurfaceView.this.getContext()))
                            .setPassword(UserUtils.getPassword(DrawableGLSurfaceView.this.getContext()))
                            .setDecoratedBus(decorator)
                            .build(drawingID);
                    registerToEventbus();

                    currentUserID = decorator.getId();

                    initAfterEventBusConnected();
                } catch (Exception e) {
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
                } finally {
                    connectionDialog.cancel();
                }
            }
        }.start();
    }

    private void initAfterEventBusConnected() throws NoSuchMethodException {
        inkPencilTouchStrategy = new InkPencilTouchStrategy(currentUserID, bus);
        eraseModeStrategy = new EraseModeTouchStrategy(currentUserID, drawing, bus);
        setToPenDrawingMode();
        setOnTouchListener(drawableTouchDelegator);

        inkModeTouchComponent = new OpenGLInkModeTouchComponent(drawing, currentUserID, newShapeIDGenerator, collaborators);
        inkModeTouchComponent.plugInto(bus);

        CollaboratorComponent collaboratorComponent = new CollaboratorComponent(currentUserID, collaborators, getContext());
        collaboratorComponent.plugInto(bus);

        bus.post(new OnSyncRequireEvent(currentUserID));
    }

    private void registerToEventbus() throws NoSuchMethodException {
        bus.register(this, OnInkStrokeAdded.class);
        bus.register(this, OnInkStrokeRemoved.class);
        bus.register(this, OnInkStrokeReAdded.class);
        bus.register(this, OnInkStrokeErased.class);
        bus.register(this, OnStrokeRestored.class);
        bus.register(this, OnAllStrokeRestored.class);
    }

    @Subscribe
    public void onInkDrawingAdded(OnInkStrokeAdded event) {
        AddInkStroke command = new AddInkStroke(event.getPoints(), event.getStrokeColor(),
                event.getUserID(), event.getUniqueID());
        executeCommand(command);
    }

    @Subscribe
    public void onInkStrokeReAdded(OnInkStrokeReAdded event) {
        drawing.addShape(new InkStroke(event.getPoints(), event.getStrokeColor()), event.getUniqueID(),
                event.getUserID());
    }

    @Subscribe
    public void onInkStrokeRemoved(OnInkStrokeRemoved event) {
        drawing.removeShape(event.getUniqueID(), event.getUserID());
    }

    @Subscribe
    public void onInkStrokeErased(OnInkStrokeErased event) {
        EraseInkStroke command = new EraseInkStroke(event.getEraserUserID(),
                event.getUserID(), event.getUniqueID());
        executeCommand(command);
    }

    @Subscribe
    public void onStrokeRestored(OnStrokeRestored event) {
        if (event.getDestinationUserID() == currentUserID) {
            drawing.addShape(new InkStroke(event.getPoints(), event.getStrokeColor()), event.getUniqueID(),
                    event.getUserID());
        }
    }

    @Subscribe
    public void onAllStrokeRestored(OnAllStrokeRestored event) {
        if (event.getDestinationUserID() == currentUserID) {
            bus.unregister(this, OnStrokeRestored.class);
            bus.unregister(this, OnAllStrokeRestored.class);
        }
    }

    public void setStrokeColor(int strokeColor) {
        currentStrokeColor = strokeColor;
        inkModeTouchComponent.setStrokeColor(strokeColor);
    }

    public void setToPenDrawingMode() {
        drawableTouchDelegator.setCurrentStrategy(inkPencilTouchStrategy);
        setOnTouchListener(drawableTouchDelegator);
    }

    public void setToEraseMode() {
        drawableTouchDelegator.setCurrentStrategy(eraseModeStrategy);
        setOnTouchListener(drawableTouchDelegator);
    }

    public void setToPanMode() {
        setOnTouchListener(fingerMotionMatrixDelegator);
    }

    /* TODO Refactor that into external of the surface */

    private void executeCommand(DrawingCommand command) {
        if (command.getUserIdSource() == currentUserID) {
            undoCommands.add(command);
            redoCommands.clear();
        }

        command.execute(drawing, bus);
    }

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
