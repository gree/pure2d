#ifndef LWF_ANIMATION_H
#define	LWF_ANIMATION_H

namespace LWF {
namespace Animation {

enum Constnt {
	END = 0,
	PLAY,
	STOP,
	NEXTFRAME,
	PREVFRAME,
	GOTOFRAME,		// FRAMENO(4bytes)
	GOTOLABEL,		// LABELID(4bytes)
	SETTARGET,		// COUNT(1byte) INSTANCEID(4bytes) ...
					// SETTARGET 0           :myself
					// SETTARGET 1 ROOT      :root
					// SETTARGET 1 PARENT    :parent
					// SETTARGET 1 ID        :child
					// SETTARGET 2 PARENT ID :sibling
					// SETTARGET 2 ROOT ID   :root/child
	EVENT,			// EVENTID(4bytes)
	CALL,			// STRINGID(4bytes)

	INSTANCE_TARGET_ROOT = -1,
	INSTANCE_TARGET_PARENT = -2,
};

}	// namespace Animation
}	// namespace LWF

#endif
