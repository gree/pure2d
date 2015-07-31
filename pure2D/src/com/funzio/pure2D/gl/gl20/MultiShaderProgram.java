package com.funzio.pure2D.gl.gl20;

import java.util.HashMap;
import java.util.Map;

import android.opengl.GLES20;
import android.util.Log;

import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.utils.Pure2DUtils;

/**
 * @author hyungjoon.kim
 */
public abstract class MultiShaderProgram extends ShaderProgram {
    public int id[] = new int[0];
    public boolean loaded = false;

    private String mVertexSource[] = new String[0];
    private String mFragmentSource[] = new String[0];
    private Map<String, Integer> mUniformIndices[] = new Map[0];
    private Map<String, Integer> mAttribIndices[] = new Map[0];

    private int mError = 0;

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    public MultiShaderProgram() {
    }

    @Override
    public int getVariantIdx(GLState glState) {     // return the shader variant for the current state (matches idx passed to setupVariant())
        int idx = 0;
        return idx;
    }

    @Override
    public void setupVariant(int idx, final String vertexSource, final String fragmentSource) {
        int olen = this.id.length;
        if (idx >= olen) {
            int[] tmpIDs = new int[idx+1];
            String[] tmpVStrs = new String[idx+1];
            String[] tmpFStrs = new String[idx+1];
            Map<String, Integer>[] tmpUMap = new Map[idx+1];
            Map<String, Integer>[] tmpAMap = new Map[idx+1];
            for (int j=0; j<idx; j++) {
                if (j >= olen) {
                    tmpIDs[j] = 0;
                    tmpVStrs[j] = new String();
                    tmpFStrs[j] = new String();
                    tmpUMap[j] = new HashMap<String, Integer>();
                    tmpAMap[j] = new HashMap<String, Integer>();
                } else {
                    tmpIDs[j] = this.id[j];
                    tmpVStrs[j] = this.mVertexSource[j];
                    tmpFStrs[j] = this.mFragmentSource[j];
                    tmpUMap[j] = this.mUniformIndices[j];
                    tmpAMap[j] = this.mAttribIndices[j];
                }
            }
            this.id = tmpIDs;
            this.mVertexSource = tmpVStrs;
            this.mFragmentSource = tmpFStrs;
            this.mUniformIndices = tmpUMap;
            this.mAttribIndices = tmpAMap;
        }

        this.id[idx] = 0;
        this.mVertexSource[idx] = vertexSource;
        this.mFragmentSource[idx] = fragmentSource;
        this.mUniformIndices[idx] = new HashMap<String, Integer>();
        this.mAttribIndices[idx] = new HashMap<String, Integer>();
    }

    @Override
    public int getUniformLocation(int idx, final String alias) {
        if (!this.loaded) {
            throw new IllegalStateException("Cannot get uniform location without calling load()");
        }

        final Integer cached = this.mUniformIndices[idx].get(alias);
        if (cached == null) {
            final int index = GLES20.glGetUniformLocation(this.id[idx], alias);
            this.mUniformIndices[idx].put(alias, Integer.valueOf(index));
            return index;
        } else {
            return cached.intValue();
        }
    }

    @Override
    public int getAttribLocation(int idx, final String alias) {
        if (!this.loaded) {
            throw new IllegalStateException("Cannot get attrib location without calling load()");
        }

        final Integer cached = this.mAttribIndices[idx].get(alias);
        if (cached == null) {
            final int index = GLES20.glGetAttribLocation(this.id[idx], alias);
            this.mAttribIndices[idx].put(alias, Integer.valueOf(index));
            return index;
        } else {
            return cached.intValue();
        }
    }

    @Override
    public void load() {
        if ((!this.loaded) && this.mError == 0) {
            for (int i=0; i<this.id.length; i++) {
                final int vertex = Pure2DUtils.createShader(GLES20.GL_VERTEX_SHADER, this.mVertexSource[i]);
                final int vertexStatus = Pure2DUtils.getShaderCompileStatus(vertex);
                if (vertexStatus != GLES20.GL_TRUE) {
                    this.mError = GLES20.glGetError();
                    Log.e("Shader", "Failed to compile vert["+i+"]: "+GLES20.glGetShaderInfoLog(vertex));
                    GLES20.glDeleteShader(vertex);
                    return;
                }

                final int fragment = Pure2DUtils.createShader(GLES20.GL_FRAGMENT_SHADER, this.mFragmentSource[i]);
                final int fragmentStatus = Pure2DUtils.getShaderCompileStatus(fragment);
                if (fragmentStatus != GLES20.GL_TRUE) {
                    this.mError = GLES20.glGetError();
                    Log.e("Shader", "Failed to compile frag["+i+"]: "+GLES20.glGetShaderInfoLog(fragment));
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
                    Log.e("Shader", "Failed to link["+i+"]: " + GLES20.glGetProgramInfoLog(program));
                    GLES20.glDeleteShader(vertex);
                    GLES20.glDeleteShader(fragment);
                    GLES20.glDeleteProgram(program);
                    return;
                }

                this.id[i] = program;
            }
            this.loaded = true;
        }
    }

    @Override
    public void reload() {
        this.mError = 0;
        this.loaded = false;
        load();
    }

    @Override
    public void bind(int idx) {
        if (this.loaded) {
            GLES20.glUseProgram(this.id[idx]);
        }
    }

    @Override
    public void unbind() {
        if (this.loaded) {
        }
    }
}
