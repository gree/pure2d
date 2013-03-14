/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.List;

import com.funzio.pure2D.Scene;

/**
 * @author long
 */
public class ParticleVO {
    public List<String> sprites;

    public int start_delay = 0;
    public int step_delay = Scene.DEFAULT_MSPF;
    public int duration = 0; // <= 0 is unlimited
    public int step_quantity = 1;

    public int layer = 0;

    public String animator;
    public String blend_mode;
}
