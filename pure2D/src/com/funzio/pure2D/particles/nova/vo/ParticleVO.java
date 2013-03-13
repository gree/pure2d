/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import com.funzio.pure2D.Scene;

/**
 * @author long
 */
public class ParticleVO {
    public String sprite;

    public int start_delay = 0;
    public int step_delay = Scene.DEFAULT_MSPF;
    public int duration = 0; // <= 0 is unlimited
    public int num_per_step = 1;

    public int layer = 0;

    public String animator;
    public String blend_mode;
}
