/**
 * 
 */
package jp.gree.casino.machine;

import com.funzio.pure2D.atlas.ImageSequenceBufferAtlas;
import com.funzio.pure2D.shapes.Clip;

/**
 * @author long
 */
public class Symbol extends Clip {

    /**
     * @param frameSet
     */
    public Symbol(final ImageSequenceBufferAtlas atlas) {
        super(atlas.getMasterFrameSet());

        setTexture(atlas.getTexture());
    }

}
