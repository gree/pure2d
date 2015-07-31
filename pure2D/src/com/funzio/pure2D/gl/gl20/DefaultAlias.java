package com.funzio.pure2D.gl.gl20;

/**
 * @author hyungjoon.kim
 */
public interface DefaultAlias {
    public static final String UNIFORM_MAT4_TRANSFORM = "u_Transform";
    public static final String UNIFORM_MAT4_MV_TRANSFORM = "u_MVTransform";
    public static final String UNIFORM_VEC4_COLOR = "u_Color";
    public static final String UNIFORM_SAMPLER2D_TEXTURE = "u_Texture";
    public static final String UNIFORM_HEXUTIL_RADIUS = "u_Radius";

    public static final String ATTRIB_VEC2_POSITION = "a_Position";
    public static final String ATTRIB_VEC2_TEXCOORDS = "a_TexCoords";
    public static final String ATTRIB_VEC4_VERTEXCOLOR = "a_VertexColor";

    public static final String VARYING_VEC2_POSITION = "v_Position";
    public static final String VARYING_VEC2_TEXCOORDS = "v_TexCoords";
    public static final String VARYING_VEC4_VERTEXCOLOR = "v_VertexColor";
}
