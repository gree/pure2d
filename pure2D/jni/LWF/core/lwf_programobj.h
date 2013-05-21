#ifndef LWF_PROGRAMOBJ_H
#define LWF_PROGRAMOBJ_H

#include "lwf_object.h"

namespace LWF {

class ProgramObject : public Object
{
public:
	ProgramObject(LWF *lwf, Movie *p, int objId);
	void Update(const Matrix *m, const ColorTransform *c);
};

}	// namespace LWF

#endif
