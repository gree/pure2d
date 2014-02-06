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
package com.funzio.pure2D.atlas;

import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import android.content.res.XmlResourceParser;
import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author long
 */
public class FunzioAtlas extends XMLAtlas {
    public static final String TAG = FunzioAtlas.class.getSimpleName();
    private static final String NODE_TEXTURE_ATLAS = "TextureAtlas";
    private static final String NODE_IMAGE = "Image";

    public FunzioAtlas(final String xml) {
        super(xml);
    }

    public FunzioAtlas(final XmlResourceParser parser) {
        super(parser);
    }

    @Override
    protected void parseXML(final String xml) {
        try {
            XMLReader xmlreader = getXMLReader();
            XMLHandler handler = new XMLHandler(this);

            // assign our handler
            xmlreader.setContentHandler(handler);
            // perform the synchronous parse
            xmlreader.parse(new InputSource(new StringReader(xml)));
        } catch (Exception e) {
            Log.e(TAG, "Parsing Error!", e);
        }
    }

    @Override
    protected void parseXML(final XmlResourceParser parser) {
        int eventType = -1;
        int index = 0;
        while (eventType != XmlResourceParser.END_DOCUMENT) {
            if (eventType == XmlResourceParser.START_TAG) {
                String strNode = parser.getName();
                if (strNode.equals(NODE_TEXTURE_ATLAS)) {
                    mWidth = Integer.parseInt(parser.getAttributeValue(null, "width"));
                    mHeight = Integer.parseInt(parser.getAttributeValue(null, "height"));
                } else if (strNode.equals(NODE_IMAGE)) {
                    String name = parser.getAttributeValue(null, "name");
                    int left = Integer.parseInt(parser.getAttributeValue(null, "left"));
                    int top = Integer.parseInt(parser.getAttributeValue(null, "top"));
                    int right = Integer.parseInt(parser.getAttributeValue(null, "right"));
                    int bottom = Integer.parseInt(parser.getAttributeValue(null, "bottom"));

                    // create and add frame
                    AtlasFrame frame = new AtlasFrame(this, index++, name, left, top, right, bottom);
                    addFrame(frame);
                }
            }

            try {
                eventType = parser.next();
            } catch (Exception e) {
                Log.e(TAG, "Parsing Error!", e);
            }
        }
    }

    private static XMLReader getXMLReader() throws ParserConfigurationException, SAXException {
        return SAXParserFactory.newInstance().newSAXParser().getXMLReader();
    }

    private static class XMLHandler extends DefaultHandler {
        private Atlas mAtlas;
        private int mIndex = 0;

        public XMLHandler(final Atlas atlas) {
            mAtlas = atlas;
        }

        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
            if (localName.equals(NODE_TEXTURE_ATLAS)) {
                mAtlas.mWidth = Integer.parseInt(attributes.getValue("width"));
                mAtlas.mHeight = Integer.parseInt(attributes.getValue("height"));
            } else if (localName.equals(NODE_IMAGE)) {
                String name = attributes.getValue("name");
                int left = Integer.parseInt(attributes.getValue("left"));
                int right = Integer.parseInt(attributes.getValue("right"));
                int top = Integer.parseInt(attributes.getValue("top"));
                int bottom = Integer.parseInt(attributes.getValue("bottom"));

                // create and add frame
                AtlasFrame frame = new AtlasFrame(mAtlas, mIndex++, name, left, top, right, bottom);
                mAtlas.addFrame(frame);
            }
        }
    }

}
