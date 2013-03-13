/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

/**
 * @author long
 */
public class ParticleVO {
    public String sprite;
    public int start_delay = 0;
    public int step_delay = 100;
    public int duration = 0; // <= 0 is unlimited
    public int num_per_step = 1;
    public String animator;
    public String blend_mode;
}
