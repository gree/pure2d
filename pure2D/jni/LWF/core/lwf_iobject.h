#ifndef LWF_IOBJECT_H
#define LWF_IOBJECT_H

#include "lwf_object.h"

namespace LWF {

class IObject : public Object
{
public:
	int instanceId;
	string name;
	IObject *prevInstance;
	IObject *nextInstance;
	IObject *linkInstance;

public:
	IObject() {}
	IObject(LWF *lwf, Movie *p, int t, int objId, int instId);

	void Destroy();
	virtual void LinkButton() {/* NOTHING TO DO */}
	string GetFullName() const;
};

}	// namespace LWF

#endif
