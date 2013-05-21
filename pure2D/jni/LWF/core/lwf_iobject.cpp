#include "lwf_core.h"
#include "lwf_data.h"
#include "lwf_iobject.h"
#include "lwf_movie.h"

namespace LWF {

IObject::IObject(LWF *lwf, Movie *p, int t, int objId, int instId)
	: Object(lwf, p, t, objId)
{
	instanceId = (instId >= (int)lwf->data->instanceNames.size()) ? -1 : instId;

	prevInstance = 0;
	nextInstance = 0;
	linkInstance = 0;

	if (instanceId >= 0) {
		int stringId = lwf->GetInstanceNameStringId(instanceId);
		if (stringId != -1)
			name = lwf->data->strings[stringId];

		IObject *head = lwf->GetInstance(instanceId);
		if (head)
			head->prevInstance = this;
		nextInstance = head;
		lwf->SetInstance(instanceId, this);
	}
}

void IObject::Destroy()
{
	if (type != OType::ATTACHEDMOVIE && instanceId >= 0) {
		IObject *head = lwf->GetInstance(instanceId);
		if (head == this)
			lwf->SetInstance(instanceId, nextInstance);
		if (nextInstance)
			nextInstance->prevInstance = prevInstance;
		if (prevInstance)
			prevInstance->nextInstance = nextInstance;
	}

	Object::Destroy();
}

string IObject::GetFullName() const
{
	string fullPath;
	string splitter;
	for (const IObject *o = this; o; o = o->parent) {
		if (o->name.empty())
			return string();
		fullPath = o->name + splitter + fullPath;
		if (splitter.empty())
			splitter = ".";
	}
	return fullPath;
}

}	// namespace LWF
