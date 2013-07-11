/**
 * 
 */
package jp.gree.casino.machine;

import com.funzio.pure2D.atlas.ImageSequenceBufferAtlas;
import com.funzio.pure2D.containers.VWheel;

/**
 * @author long
 */
public class Reel extends VWheel {
    protected SlotMachine mMachine;

    public Reel(final SlotMachine machine) {
        mMachine = machine;

        createChildren();
    }

    protected void createChildren() {
        for (int i = 0; i < mMachine.mTextureManager.mMajorSymbols.length; i++) {
            createSymbol(i, mMachine.mTextureManager.mMajorSymbols[i]);
        }
    }

    protected void createSymbol(final int index, final ImageSequenceBufferAtlas atlas) {
        Symbol symbol = new Symbol(atlas);
        // symbol.stop();
        addChild(symbol);
    }
}
