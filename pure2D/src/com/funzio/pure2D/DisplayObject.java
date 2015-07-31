/*******************************************************************************
 * Copyright (C) 2012-2014 GREE, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
/**
 * 
 */
package com.funzio.pure2D;

import org.xmlpull.v1.XmlPullParser;

import com.funzio.pure2D.containers.Container;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl20.ShaderProgram;
import com.funzio.pure2D.ui.UIManager;

/**
 * @author long
 */
public interface DisplayObject extends Displayable {

    public boolean draw(final GLState glState);

    public boolean isPerspectiveEnabled();

    public void setPerspectiveEnabled(final boolean perspectiveEnabled);

    public void setProgram(final ShaderProgram program);

    public ShaderProgram getShaderProgram();

    /**
     * @hide For internal use
     */
    public void setXMLAttributes(XmlPullParser xmlParser, UIManager manager);

    /**
     * @hide For internal use
     */
    public void onAdded(Container container); // TODO change to Parentable and consolidate with Displayable

    /**
     * @hide For internal use
     */
    public void onCreateChildren(final UIManager manager);
}
