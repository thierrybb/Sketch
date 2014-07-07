package ca.etsmtl.sketch.surface;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.IOException;
import java.net.Socket;

import ca.etsmtl.sketch.R;
import ca.etsmtl.sketch.common.bus.builder.RemoteBusBuilder;
import ca.etsmtl.sketch.common.bus.eventbus.EventBus;
import ca.etsmtl.sketch.common.bus.eventbus.SimpleEventBus;
import ca.etsmtl.sketch.common.bus.eventbus.Subscribe;
import ca.etsmtl.sketch.common.event.OnInkDrawingAdded;
import ca.etsmtl.sketch.eventbus.UIThreadEventBusDecorator;
import ca.etsmtl.sketch.surface.graphic.CanvasGraphics;
import ca.etsmtl.sketch.surface.shape.Drawing;
import ca.etsmtl.sketch.surface.shape.InkStroke;
import ca.etsmtl.sketch.surface.shape.Shape;
import ca.etsmtl.sketch.surface.touch.InkModeTouchComponent;

public class CanvasDrawableSurface extends SurfaceView {
    private EventBus bus;
    private CanvasGraphics currentGraphics = new CanvasGraphics();
    private Drawing drawing = new Drawing();

    private InkModeTouchComponent inkModeTouchComponent;
    private ProgressDialog connectionDialog;

    public CanvasDrawableSurface(Context context) {
        super(context);
        init();
    }

    public CanvasDrawableSurface(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public CanvasDrawableSurface(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        inkModeTouchComponent = new InkModeTouchComponent(drawing);
        setBackgroundColor(Color.WHITE);
        drawing.attachListener(new Shape.ShapeListener() {
            @Override
            public void onShapeChanged() {
                CanvasDrawableSurface.this.post(new Runnable() {
                    @Override
                    public void run() {
                        invalidate();
                    }
                });
            }
        });

        connectionDialog = ProgressDialog.show(this.getContext(),
                this.getResources().getString(R.string.connection_dialog_title),
                this.getResources().getString(R.string.connection_dialog_message), true);
        createEventBus();
    }

    private void createEventBus() {
        new Thread() {
            @Override
            public void run() {
                try {
                    RemoteBusBuilder remoteBusBuilder = new RemoteBusBuilder();
                    Socket socket = new Socket("192.168.2.125", 11111);
                    bus = remoteBusBuilder.setSocket(socket)
                            .setDecoratedBus(new UIThreadEventBusDecorator(new SimpleEventBus(), CanvasDrawableSurface.this))
                            .build();
                    bus.register(CanvasDrawableSurface.this, OnInkDrawingAdded.class);

                    setOnTouchListener(new EventBusTouchListenerDelegator(bus));

                    inkModeTouchComponent.plug(bus);
                } catch (IOException e) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CanvasDrawableSurface.this.getContext(),
                                    R.string.unable_to_connect_to_server, Toast.LENGTH_LONG).show();
                            CanvasDrawableSurface.this.setEnabled(false);
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

    @Override
    protected void onDraw(Canvas canvas) {
        currentGraphics.setCanvas(canvas);
        drawing.draw(currentGraphics);
    }
}
