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
package com.funzio.pure2D.demo.physics;

import android.graphics.PointF;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;

import com.funzio.pure2D.physics.box2D.Box2DWorld;
import com.funzio.pure2D.shapes.Sprite;

public class HelloPhysicsWorld extends Box2DWorld {
    public HelloPhysicsWorld(float screenWidth, float screenHeight, final Vec2 gravity) {
        super(gravity, false);

        screenWidth = p2M(screenWidth);
        screenHeight = p2M(screenHeight);

        // the bound boxes
        Body bottomBody = createBody(new BodyDef());
        PolygonShape bottomShape = new PolygonShape();
        bottomShape.setAsEdge(new Vec2(0, 0f), new Vec2(screenWidth, 0));
        bottomBody.createFixture(bottomShape, 0);
        bottomBody.setType(BodyType.STATIC);

        Body topBody = createBody(new BodyDef());
        PolygonShape topShape = new PolygonShape();
        topShape.setAsEdge(new Vec2(0, screenHeight), new Vec2(screenWidth, screenHeight));
        topBody.createFixture(topShape, 0);
        topBody.setType(BodyType.STATIC);

        Body leftBody = createBody(new BodyDef());
        PolygonShape leftShape = new PolygonShape();
        leftShape.setAsEdge(new Vec2(0f, 0f), new Vec2(0f, screenHeight));
        leftBody.createFixture(leftShape, 0);
        leftBody.setType(BodyType.STATIC);

        Body rightBody = createBody(new BodyDef());
        PolygonShape rightShape = new PolygonShape();
        rightShape.setAsEdge(new Vec2(screenWidth, 0f), new Vec2(screenWidth, screenHeight));
        rightBody.createFixture(rightShape, 0);
        rightBody.setType(BodyType.STATIC);
    }

    public Body addBox(final float x, final float y, final float w, final float h) {
        // Create Dynamic Body
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x, y);
        Body body = createBody(bodyDef);
        if (body != null) {
            body.setType(BodyType.DYNAMIC);

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(w / 2, h / 2);
            // Assign shape to Body
            Fixture fixture = body.createFixture(shape, 1);
            fixture.setFriction(0);
            fixture.setRestitution(0.5f);
        }

        return body;
    }

    // public Body getBodyAt(final float x, final float y) {
    // Vec2 point = new Vec2(x, y);
    // AABB aabb = new AABB();
    // aabb.lowerBound.set(new Vec2(x - 0.001f, y - 0.001f));
    // aabb.upperBound.set(new Vec2(x + 0.001f, y + 0.001f));
    //
    // int maxCount = 2;
    // Shape[] shapes = query(aabb, maxCount);
    // int len = shapes.length;
    // for (int i = 0; i < len; i++) {
    // if (!shapes[i].m_body.isStatic()) {
    // if (shapes[i].testPoint(shapes[i].m_body.getXForm(), point)) {
    // return shapes[i].m_body;
    // }
    // }
    // }
    // return null;
    // }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.physics.box2D.Box2DWorld#render()
     */
    @Override
    public void render(final float alpha) {
        // super.render(alpha);

        Body body = getBodyList();
        final float oneMinusAlpha = 1 - alpha;
        while (body != null) {
            if (body.getType() != BodyType.STATIC) {
                final Sprite sprite = (Sprite) body.getUserData();
                if (sprite != null) {
                    final Vec2 newPos = body.getPosition();
                    final float newRotation = (float) (body.getAngle() * 180f / Math.PI);
                    if (alpha == 1) {
                        // quick update
                        sprite.setPosition(m2P(newPos.x), m2P(newPos.y));
                        sprite.setRotation(newRotation);
                    } else {
                        PointF currentPos = sprite.getPosition();
                        // interpolate position
                        final float smoothX = m2P(newPos.x) * alpha + currentPos.x * oneMinusAlpha;
                        final float smoothY = m2P(newPos.y) * alpha + currentPos.y * oneMinusAlpha;

                        // interpolate rotation
                        final float smoothRotation = newRotation * alpha + sprite.getRotation() * oneMinusAlpha;

                        sprite.setPosition(smoothX, smoothY);
                        sprite.setRotation(smoothRotation);
                    }
                }
            }

            body = body.getNext();
        }
    }

    // @Override
    // public void render(final float alpha) {
    // // super.render(alpha);
    //
    // Body body = getBodyList();
    // final float time = alpha * mDt;
    // while (body != null) {
    // if (body.getType() != BodyType.STATIC) {
    // final Sprite sprite = (Sprite) body.getUserData();
    // if (sprite != null) {
    // final Vec2 newPos = body.getPosition();
    // if (alpha == 1) {
    // // quick update
    // sprite.setPosition(m2P(newPos.x), m2P(newPos.y));
    // final float newRotation = (float) (body.getAngle() * 180f / Math.PI);
    // sprite.setRotation(newRotation);
    // } else {
    // // exterpolate position
    // final Vec2 vecloc = body.getLinearVelocity();
    // final float smoothX = m2P(newPos.x + vecloc.x * time);
    // final float smoothY = m2P(newPos.y + vecloc.y * time);
    //
    // // exterpolate rotation
    // final float smoothRotation = (float) ((body.getAngle() + body.getAngularVelocity() * time) * 180f / Math.PI);
    // sprite.setPosition(smoothX, smoothY);
    // sprite.setRotation(smoothRotation);
    // }
    // }
    // }
    //
    // body = body.getNext();
    // }
    // }
}
