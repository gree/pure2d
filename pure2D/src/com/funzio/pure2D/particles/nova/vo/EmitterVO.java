/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.List;

/**
 * @author long
 */
public class EmitterVO {
    public String name;
    public String type = "rectangle";
    public float width = 1;
    public float height = 1;
    public int lifespan = 0; // ms

    public List<ParticleVO> particles;

    public EmitterVO() {
        // TODO Auto-generated constructor stub
    }

}
