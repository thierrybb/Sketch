package ca.etsmtl.sketch.surface;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import ca.etsmtl.sketch.surface.openglshape.Drawing;

public class DrawingRenderer implements GLSurfaceView.Renderer {
    private Drawing drawing;

    public DrawingRenderer(Drawing drawing) {
        this.drawing = drawing;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glFrontFace(GL10.GL_CW);
        gl.glDisable(GL10.GL_DEPTH_TEST);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrthof(0, width, height, 0, 1, -1);

        gl.glDisable(GL10.GL_LIGHTING);
        gl.glDisable(GL10.GL_CULL_FACE);
        gl.glDisable(GL10.GL_DEPTH_BUFFER_BIT);
        gl.glDisable(GL10.GL_DEPTH_TEST);
        gl.glClearColor(1f, 1f, 1f, 1.f);
        gl.glShadeModel(GL10.GL_SMOOTH);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glTranslatef(0, 0, 0);
        gl.glScalef(1, 1, 1);

        drawing.draw(gl);
    }
}
