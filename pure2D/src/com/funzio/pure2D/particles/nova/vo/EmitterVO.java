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
    public int quantity = 1;
    public int duration = 0; // ms

    // animator for this emitter
    public String animator;

    // and particles this will emit
    public List<ParticleVO> particles;

    public EmitterVO() {
        // TODO Auto-generated constructor stub
    }

}
