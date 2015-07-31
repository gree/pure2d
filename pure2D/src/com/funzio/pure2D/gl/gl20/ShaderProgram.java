package com.funzio.pure2D.gl.gl20;

import java.util.HashMap;
import java.util.Map;

import android.opengl.GLES20;

import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.utils.Pure2DUtils;

/**
 * @author hyungjoon.kim
 */
public abstract class ShaderProgram {
    public int id;

    private String mVertexSource;
    private String mFragmentSource;
    private final Map<String, Integer> mUniformIndices;
    private final Map<String, Integer> mAttribIndices;

    private int mError = 0;

    public int getVariantIdx(GLState glState) {
        return 0;
    }

    public boolean isLoaded() {
        return id != 0;
    }

    public ShaderProgram(final String vertexSource, final String fragmentSource) {
        this.mVertexSource = vertexSource;
        this.mFragmentSource = fragmentSource;
        this.mUniformIndices = new HashMap<String, Integer>();
        this.mAttribIndices = new HashMap<String, Integer>();
    }

    public ShaderProgram() {
        this.mVertexSource = "";
        this.mFragmentSource = "";
        this.mUniformIndices = new HashMap<String, Integer>();
        this.mAttribIndices = new HashMap<String, Integer>();
    }

    public void setupVariant(int ignore, final String vertexSource, final String fragmentSource) {
        mVertexSource = vertexSource;
        mFragmentSource = fragmentSource;
    }

    public int getUniformLocation(int ignore, final String alias) {
        if (this.id == 0) {
            throw new IllegalStateException("Cannot get uniform location without calling load()");
        }

        final Integer cached = this.mUniformIndices.get(alias);
        if (cached == null) {
            final int index = GLES20.glGetUniformLocation(this.id, alias);
            this.mUniformIndices.put(alias, Integer.valueOf(index));
            return index;
        } else {
            return cached.intValue();
        }
    }

    public int getAttribLocation(int ignore, final String alias) {
        if (this.id == 0) {
            throw new IllegalStateException("Cannot get attrib location without calling load()");
        }

        final Integer cached = this.mAttribIndices.get(alias);
        if (cached == null) {
            final int index = GLES20.glGetAttribLocation(this.id, alias);
            this.mAttribIndices.put(alias, Integer.valueOf(index));
            return index;
        } else {
            return cached.intValue();
        }
    }

    public void load() {
        if (this.id == 0 && this.mError == 0) {
            final int vertex = Pure2DUtils.createShader(GLES20.GL_VERTEX_SHADER, this.mVertexSource);
                final int vertexStatus = Pure2DUtils.getShaderCompileStatus(vertex);
                if (vertexStatus != GLES20.GL_TRUE) {
                    this.mError = GLES20.glGetError();
                    GLES20.glDeleteShader(vertex);
                    return;
                }

            final int fragment = Pure2DUtils.createShader(GLES20.GL_FRAGMENT_SHADER, this.mFragmentSource);
            final int fragmentStatus = Pure2DUtils.getShaderCompileStatus(fragment);
            if (fragmentStatus != GLES20.GL_TRUE) {
                this.mError = GLES20.glGetError();
                GLES20.glDeleteShader(vertex);
                GLES20.glDeleteShader(fragment);
                return;
            }

            final int program = GLES20.glCreateProgram();
            GLES20.glAttachShader(program, vertex);
            GLES20.glAttachShader(program, fragment);
            GLES20.glLinkProgram(program);

            final int linkStatus = Pure2DUtils.getProgramLinkStatus(program);
            if (linkStatus != GLES20.GL_TRUE) {
                this.mError = GLES20.glGetError();
                GLES20.glDeleteShader(vertex);
                GLES20.glDeleteShader(fragment);
                GLES20.glDeleteProgram(program);
            }

            this.id = program;
        }
    }

    public void reload() {
        this.id = 0;
        this.mError = 0;
        load();
    }

    public void bind(int ignore) {
        if (this.id != 0) {
            GLES20.glUseProgram(this.id);
        }
    }

    public void unbind() {
        if (this.id != 0) {
        }
    }
}
