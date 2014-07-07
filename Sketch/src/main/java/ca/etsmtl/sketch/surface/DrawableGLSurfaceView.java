package ca.etsmtl.sketch.surface;

import android.app.ProgressDialog;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.widget.Toast;

import java.io.IOException;
import java.net.Socket;
import java.util.Stack;

import ca.etsmtl.sketch.R;
import ca.etsmtl.sketch.common.bus.builder.RemoteBusBuilder;
import ca.etsmtl.sketch.common.bus.component.UniqueIDGenerator;
import ca.etsmtl.sketch.common.bus.eventbus.EventBus;
import ca.etsmtl.sketch.common.bus.eventbus.IDReceiverDecorator;
import ca.etsmtl.sketch.common.bus.eventbus.SimpleEventBus;
import ca.etsmtl.sketch.common.bus.eventbus.Subscribe;
import ca.etsmtl.sketch.common.event.OnInkStrokeAdded;
import ca.etsmtl.sketch.common.event.OnInkStrokeRemoved;
import ca.etsmtl.sketch.common.event.OnNewUserAdded;
import ca.etsmtl.sketch.eventbus.UIThreadEventBusDecorator;
import ca.etsmtl.sketch.surface.command.AddInkStroke;
import ca.etsmtl.sketch.surface.command.DrawingCommand;
import ca.etsmtl.sketch.surface.openglshape.Drawing;
import ca.etsmtl.sketch.surface.openglshape.Shape;
import ca.etsmtl.sketch.surface.touch.OpenGLInkModeTouchComponent;
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
                    bus.register(DrawableGLSurfaceView.this, OnInkStrokeAdded.class);
                    bus.register(DrawableGLSurfaceView.this, OnNewUserAdded.class);
                    bus.register(DrawableGLSurfaceView.this, OnInkStrokeRemoved.class);


                    currentUserID = decorator.getId();

                    setOnTouchListener(new EventBusTouchListenerDelegator(bus, currentUserID));
                    bus.post(new OnNewUserAdded(UserUtils.getUsername(DrawableGLSurfaceView.this.getContext()), currentUserID));

                    inkModeTouchComponent = new OpenGLInkModeTouchComponent(drawing, currentUserID, newShapeIDGenerator);
                    inkModeTouchComponent.plug(bus);
                } catch (IOException e) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DrawableGLSurfaceView.this.getContext(),
                                    R.string.unable_to_connect_to_server, Toast.LENGTH_LONG).show();
                            DrawableGLSurfaceView.this.setEnabled(false);
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

    @Subscribe
    public void onInkDrawingAdded(OnInkStrokeAdded event) {
        AddInkStroke command = new AddInkStroke(event.getPoints(), event.getStrokeColor(),
                event.getUserID(), event.getUniqueID());
        executeCommand(command);
    }

    private void executeCommand(DrawingCommand command) {
        undoCommands.add(command);
        command.execute(drawing, bus);
        redoCommands.clear();
    }

    public void undo() {
        if (!undoCommands.isEmpty()) {
            DrawingCommand pop = undoCommands.pop();
            pop.undo(drawing, bus);
            redoCommands.add(pop);
        }
    }

    public void redo() {
        if (!redoCommands.isEmpty()) {
            DrawingCommand pop = redoCommands.pop();
            undoCommands.add(pop);
            pop.redo(drawing, bus);
        }
    }

    @Subscribe
    public void onNewUserAdded(OnNewUserAdded event) {
        if (event.getId() != currentUserID) {
            String message = String.format(getContext().getString(R.string.new_user_connected), event.getName());
            Toast.makeText(this.getContext(), message, Toast.LENGTH_LONG).show();
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
}
