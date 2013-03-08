/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;

/**
 * @author long
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @Type(value = MoveAnimatorVO.class, name = "move"), //
        @Type(value = RotateAnimatorVO.class, name = "rotate"), //
        @Type(value = SequenceAnimatorVO.class, name = "sequence"), //
        @Type(value = ParallelAnimatorVO.class, name = "parallel"), //
})
public class AnimatorVO {
    public String name;
    public String type;

    public AnimatorVO() {
        // TODO Auto-generated constructor stub
    }

}
