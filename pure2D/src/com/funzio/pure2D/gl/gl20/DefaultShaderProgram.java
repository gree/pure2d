package com.funzio.pure2D.gl.gl20;

import com.funzio.pure2D.gl.gl10.GLState;

/**
 * @author hyungjoon.kim
 */
public class DefaultShaderProgram extends MultiShaderProgram implements DefaultAlias {
        private static final String SOURCE_VERTEX_T_C_cC = "" + // Texture modulated by Gouraud per-vertex color and const color
                "uniform mat4 " + UNIFORM_MAT4_TRANSFORM + ";\n" + //
                "attribute vec2 " + ATTRIB_VEC2_POSITION + ";\n" + //
                "attribute vec2 " + ATTRIB_VEC2_TEXCOORDS + ";\n" + //
                "attribute vec4 " + ATTRIB_VEC4_VERTEXCOLOR + ";\n" + //
                "varying vec2 " + VARYING_VEC2_TEXCOORDS + ";\n" + //
                "varying vec4 " + VARYING_VEC4_VERTEXCOLOR + ";\n" + //
                "void main() {\n" + //
                "  " + VARYING_VEC2_TEXCOORDS + " = " + ATTRIB_VEC2_TEXCOORDS + ";\n" + //
                "  " + VARYING_VEC4_VERTEXCOLOR + " = " + ATTRIB_VEC4_VERTEXCOLOR + ";\n" + //
                "  gl_Position = " + UNIFORM_MAT4_TRANSFORM + " * vec4(" + ATTRIB_VEC2_POSITION + ", 0.0, 1.0);\n" + //
                "}";
        private static final String SOURCE_FRAGMENT_T_C_cC = "" + //
                "precision highp float;\n" + //
                "uniform sampler2D " + UNIFORM_SAMPLER2D_TEXTURE + ";\n" + //
                "uniform vec4 " + UNIFORM_VEC4_COLOR + ";\n" + //
                "varying vec2 " + VARYING_VEC2_TEXCOORDS + ";\n" + //
                "varying vec4 " + VARYING_VEC4_VERTEXCOLOR + ";\n" + //
                "void main() {\n" + //
                "  vec4 sample = texture2D(" + UNIFORM_SAMPLER2D_TEXTURE + ", " + VARYING_VEC2_TEXCOORDS + ") * " + UNIFORM_VEC4_COLOR + " * " + VARYING_VEC4_VERTEXCOLOR + ";\n" + //
//"  sample.r = 1.0;\n" + //
//"  sample.g = 1.0;\n" + //
//"  sample.b = 1.0;\n" + //
                "  gl_FragColor = sample;\n" + //
                "}";

        private static final String SOURCE_VERTEX_T_cC = "" + // Texture modulated by const color
                "uniform mat4 " + UNIFORM_MAT4_TRANSFORM + ";\n" + //
                "attribute vec2 " + ATTRIB_VEC2_POSITION + ";\n" + //
                "attribute vec2 " + ATTRIB_VEC2_TEXCOORDS + ";\n" + //
                "varying vec2 " + VARYING_VEC2_TEXCOORDS + ";\n" + //
                "void main() {\n" + //
                "  " + VARYING_VEC2_TEXCOORDS + " = " + ATTRIB_VEC2_TEXCOORDS + ";\n" + //
                "  gl_Position = " + UNIFORM_MAT4_TRANSFORM + " * vec4(" + ATTRIB_VEC2_POSITION + ", 0.0, 1.0);\n" + //
                "}";
        private static final String SOURCE_FRAGMENT_T_cC = "" + //
                "precision highp float;\n" + //
                "uniform sampler2D " + UNIFORM_SAMPLER2D_TEXTURE + ";\n" + //
                "uniform vec4 " + UNIFORM_VEC4_COLOR + ";\n" + //
                "varying vec2 " + VARYING_VEC2_TEXCOORDS + ";\n" + //
                "void main() {\n" + //
                "  vec4 sample = texture2D(" + UNIFORM_SAMPLER2D_TEXTURE + ", " + VARYING_VEC2_TEXCOORDS + ") * " + UNIFORM_VEC4_COLOR + ";\n" + //
//"  sample.a = 1.0;\n" + //
//"  sample += vec4(" + VARYING_VEC2_TEXCOORDS + ".x * 0.5, " + VARYING_VEC2_TEXCOORDS + ".y * 0.5, 1.0 * 0.5, 0.0);\n" +
                "  gl_FragColor = sample;\n" + //
                "}";

        private static final String SOURCE_VERTEX_T_C = "" + // Texture modulated by Gouraud per-vertex color
                "uniform mat4 " + UNIFORM_MAT4_TRANSFORM + ";\n" + //
                "attribute vec2 " + ATTRIB_VEC2_POSITION + ";\n" + //
                "attribute vec2 " + ATTRIB_VEC2_TEXCOORDS + ";\n" + //
                "attribute vec4 " + ATTRIB_VEC4_VERTEXCOLOR + ";\n" + //
                "varying vec2 " + VARYING_VEC2_TEXCOORDS + ";\n" + //
                "varying vec4 " + VARYING_VEC4_VERTEXCOLOR + ";\n" + //
                "void main() {\n" + //
                "  " + VARYING_VEC2_TEXCOORDS + " = " + ATTRIB_VEC2_TEXCOORDS + ";\n" + //
                "  " + VARYING_VEC4_VERTEXCOLOR + " = " + ATTRIB_VEC4_VERTEXCOLOR + ";\n" + //
                "  gl_Position = " + UNIFORM_MAT4_TRANSFORM + " * vec4(" + ATTRIB_VEC2_POSITION + ", 0.0, 1.0);\n" + //
                "}";
        private static final String SOURCE_FRAGMENT_T_C = "" + //
                "precision highp float;\n" + //
                "uniform sampler2D " + UNIFORM_SAMPLER2D_TEXTURE + ";\n" + //
                "varying vec2 " + VARYING_VEC2_TEXCOORDS + ";\n" + //
                "varying vec4 " + VARYING_VEC4_VERTEXCOLOR + ";\n" + //
                "void main() {\n" + //
                "  vec4 sample = texture2D(" + UNIFORM_SAMPLER2D_TEXTURE + ", " + VARYING_VEC2_TEXCOORDS + ") * " + VARYING_VEC4_VERTEXCOLOR + ";\n" + //
//"  sample.r = 1.0;\n" + //
//"  sample.g = 1.0;\n" + //
//"  sample.b = 1.0;\n" + //
                "  gl_FragColor = sample;\n" + //
                "}";

        private static final String SOURCE_VERTEX_C_cC = "" + // Gouraud modulated by const color
                "uniform mat4 " + UNIFORM_MAT4_TRANSFORM + ";\n" + //
                "attribute vec2 " + ATTRIB_VEC2_POSITION + ";\n" + //
                "attribute vec4 " + ATTRIB_VEC4_VERTEXCOLOR + ";\n" + //
                "varying vec4 " + VARYING_VEC4_VERTEXCOLOR + ";\n" + //
                "void main() {\n" + //
                "  " + VARYING_VEC4_VERTEXCOLOR + " = " + ATTRIB_VEC4_VERTEXCOLOR + ";\n" + //
                "  gl_Position = " + UNIFORM_MAT4_TRANSFORM + " * vec4(" + ATTRIB_VEC2_POSITION + ", 0.0, 1.0);\n" + //
                "}";
        private static final String SOURCE_FRAGMENT_C_cC = "" + //
                "precision highp float;\n" + //
                "uniform vec4 " + UNIFORM_VEC4_COLOR + ";\n" + //
                "varying vec4 " + VARYING_VEC4_VERTEXCOLOR + ";\n" + //
                "void main() {\n" + //
                "  vec4 sample = " + UNIFORM_VEC4_COLOR + " * " + VARYING_VEC4_VERTEXCOLOR + ";\n" + //
                "  gl_FragColor = sample;\n" + //
                "}";

        private static final String SOURCE_VERTEX_T = "" + // Texture
                "uniform mat4 " + UNIFORM_MAT4_TRANSFORM + ";\n" + //
                "attribute vec2 " + ATTRIB_VEC2_POSITION + ";\n" + //
                "attribute vec2 " + ATTRIB_VEC2_TEXCOORDS + ";\n" + //
                "varying vec2 " + VARYING_VEC2_TEXCOORDS + ";\n" + //
                "void main() {\n" + //
                "  " + VARYING_VEC2_TEXCOORDS + " = " + ATTRIB_VEC2_TEXCOORDS + ";\n" + //
                "  gl_Position = " + UNIFORM_MAT4_TRANSFORM + " * vec4(" + ATTRIB_VEC2_POSITION + ", 0.0, 1.0);\n" + //
                "}";
        private static final String SOURCE_FRAGMENT_T = "" + //
                "precision highp float;\n" + //
                "uniform sampler2D " + UNIFORM_SAMPLER2D_TEXTURE + ";\n" + //
                "varying vec2 " + VARYING_VEC2_TEXCOORDS + ";\n" + //
                "void main() {\n" + //
                "  vec4 sample = texture2D(" + UNIFORM_SAMPLER2D_TEXTURE + ", " + VARYING_VEC2_TEXCOORDS + ");\n" + //
//"  sample.r = 1.0;\n" + //
//"  sample.g = 1.0;\n" + //
//"  sample.b = 1.0;\n" + //
                "  gl_FragColor = sample;\n" + //
                "}";

        private static final String SOURCE_VERTEX_C = "" + // Gouraud per-vertex color
                "uniform mat4 " + UNIFORM_MAT4_TRANSFORM + ";\n" + //
                "attribute vec2 " + ATTRIB_VEC2_POSITION + ";\n" + //
                "attribute vec4 " + ATTRIB_VEC4_VERTEXCOLOR + ";\n" + //
                "varying vec4 " + VARYING_VEC4_VERTEXCOLOR + ";\n" + //
                "void main() {\n" + //
                "  " + VARYING_VEC4_VERTEXCOLOR + " = " + ATTRIB_VEC4_VERTEXCOLOR + ";\n" + //
                "  gl_Position = " + UNIFORM_MAT4_TRANSFORM + " * vec4(" + ATTRIB_VEC2_POSITION + ", 0.0, 1.0);\n" + //
                "}";
        private static final String SOURCE_FRAGMENT_C = "" + //
                "precision highp float;\n" + //
                "varying vec4 " + VARYING_VEC4_VERTEXCOLOR + ";\n" + //
                "void main() {\n" + //
                "  vec4 sample = " + VARYING_VEC4_VERTEXCOLOR + ";\n" + //
                "  gl_FragColor = sample;\n" + //
                "}";

        private static final String SOURCE_VERTEX_cC = "" + // Flat
                "uniform mat4 " + UNIFORM_MAT4_TRANSFORM + ";\n" + //
                "attribute vec2 " + ATTRIB_VEC2_POSITION + ";\n" + //
                "void main() {\n" + //
                "  gl_Position = " + UNIFORM_MAT4_TRANSFORM + " * vec4(" + ATTRIB_VEC2_POSITION + ", 0.0, 1.0);\n" + //
                "}";
        private static final String SOURCE_FRAGMENT_cC = "" + //
                "precision highp float;\n" + //
                "uniform vec4 " + UNIFORM_VEC4_COLOR + ";\n" + //
                "void main() {\n" + //
                "  vec4 sample = " + UNIFORM_VEC4_COLOR + ";\n" + //
                "  gl_FragColor = sample;\n" + //
                "}";

        public DefaultShaderProgram() {
                super();
                setupVariant(7, SOURCE_VERTEX_T_C_cC, SOURCE_FRAGMENT_T_C_cC);
                setupVariant(6, SOURCE_VERTEX_T_C,    SOURCE_FRAGMENT_T_C);
                setupVariant(5, SOURCE_VERTEX_T_cC,   SOURCE_FRAGMENT_T_cC);
                setupVariant(4, SOURCE_VERTEX_T,      SOURCE_FRAGMENT_T);
                setupVariant(3, SOURCE_VERTEX_C_cC,   SOURCE_FRAGMENT_C_cC);
                setupVariant(2, SOURCE_VERTEX_C,      SOURCE_FRAGMENT_C);
                setupVariant(1, SOURCE_VERTEX_cC,     SOURCE_FRAGMENT_cC);
                setupVariant(0, SOURCE_VERTEX_cC,     SOURCE_FRAGMENT_cC);
        }

        @Override
        public int getVariantIdx(GLState glState) {
                int idx = 0;
                if ((glState.getTexture() != null) && (glState.getTextureCoordBuffer() != null) && glState.mTextureCoordArrayEnabled) idx += 4;
                if ((glState.getColorBuffer() != null) && glState.mColorArrayEnabled) idx += 2;
                if (glState.getColor() != null) idx += 1;
                return idx;
        }
}
