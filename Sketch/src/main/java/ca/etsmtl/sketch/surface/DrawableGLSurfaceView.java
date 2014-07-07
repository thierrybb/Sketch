package ca.etsmtl.sketch.surface;

import android.app.ProgressDialog;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.widget.Toast;

import java.io.IOException;
import java.net.Socket;

import ca.etsmtl.sketch.R;
import ca.etsmtl.sketch.common.bus.builder.RemoteBusBuilder;
import ca.etsmtl.sketch.common.bus.event.OnNewIDAssigned;
import ca.etsmtl.sketch.common.bus.eventbus.EventBus;
import ca.etsmtl.sketch.common.bus.eventbus.SimpleEventBus;
import ca.etsmtl.sketch.common.bus.eventbus.Subscribe;
import ca.etsmtl.sketch.common.event.OnInkDrawingAdded;
import ca.etsmtl.sketch.common.event.OnNewUserAdded;
import ca.etsmtl.sketch.eventbus.UIThreadEventBusDecorator;
import ca.etsmtl.sketch.surface.openglshape.Drawing;
import ca.etsmtl.sketch.surface.openglshape.InkStroke;
import ca.etsmtl.sketch.surface.openglshape.Shape;
import ca.etsmtl.sketch.surface.touch.OpenGLInkModeTouchComponent;
import ca.etsmtl.sketch.utils.UserUtils;

public class DrawableGLSurfaceView extends GLSurfaceView {
    private DrawingRenderer drawingRenderer;
    private EventBus bus;
    private Drawing drawing = new Drawing();

    private int currentUserID;

    private OpenGLInkModeTouchComponent inkModeTouchComponent;
    private ProgressDialog connectionDialog;

    public DrawableGLSurfaceView(Context context) {
        super(context);
        init();
    }

    public DrawableGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inkModeTouchComponent = new OpenGLInkModeTouchComponent(drawing);
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
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        createEventBus();
    }

    private void createEventBus() {
        new Thread() {
            @Override
            public void run() {
                try {
                    RemoteBusBuilder remoteBusBuilder = new RemoteBusBuilder();
                    Socket socket = new Socket("192.168.2.125", 11112);
                    bus = remoteBusBuilder.setSocket(socket)
                            .setDecoratedBus(new UIThreadEventBusDecorator(new SimpleEventBus(), DrawableGLSurfaceView.this))
                            .build();
                    bus.register(DrawableGLSurfaceView.this, OnNewIDAssigned.class);
                    bus.register(DrawableGLSurfaceView.this, OnInkDrawingAdded.class);
                    bus.register(DrawableGLSurfaceView.this, OnNewUserAdded.class);
                    setOnTouchListener(new EventBusTouchListenerDelegator(bus));

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
    public void onInkDrawingAdded(OnInkDrawingAdded event) {
        drawing.addShape(new InkStroke(event.getPoints()));
    }

    @Subscribe
    public void onNewUserAdded(OnNewUserAdded event) {
        if (event.getId() != currentUserID) {
            String message = String.format(getContext().getString(R.string.new_user_connected), event.getName());
            Toast.makeText(this.getContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    @Subscribe
    public void onNewIdAssigned(OnNewIDAssigned event) {
        currentUserID = event.getNewID();
        bus.post(new OnNewUserAdded(UserUtils.getUsername(DrawableGLSurfaceView.this.getContext()), currentUserID));
    }
}
