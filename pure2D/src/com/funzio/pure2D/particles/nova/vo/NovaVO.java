/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.List;

/**
 * @author long
 */
public class NovaVO {
    public int version;
    public List<EmitterVO> emitters;
    public List<SpriteVO> sprites;
    public List<AnimatorVO> animators;

    public NovaVO() {
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Ver: " + version + "\n" //
                + "Emitters: " + (emitters == null ? 0 : emitters.size()) + "\n" //
                + "Sprites: " + (sprites == null ? 0 : sprites.size()) + "\n" //
                + "Animators: " + (animators == null ? 0 : animators.size());
    }

}
