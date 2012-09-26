/**
 * 
 */
package com.funzio.crimecity.particles.units;

/**
 * @author long
 */
public class RAH extends HeliUnit {
    public RAH() {
        super("RAH");
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.crimecity.particles.units.HeliUnit#soundStart()
     */
    @Override
    protected void soundStart() {
        super.soundStart();

        // sound fx
        // [[RGSoundManager sharedInstance] playSoundWithPath:[[NSBundle mainBundle] pathForResource:@"small_helicopter" ofType:@"mp3"]];
    }
}
