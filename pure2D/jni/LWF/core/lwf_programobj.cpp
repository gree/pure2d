#include "lwf_core.h"
#include "lwf_data.h"
#include "lwf_programobj.h"
#include "lwf_renderer.h"

namespace LWF {

ProgramObject::ProgramObject(LWF *l, Movie *p, int objId)
	: Object(l, p, Format::Object::PROGRAMOBJECT, objId)
{
	const ProgramObjectConstructor ctor =
		lwf->GetProgramObjectConstructor(objId);
	if (!ctor)
		return;

	const Format::ProgramObject &data = lwf->data->programObjects[objId];
	dataMatrixId = data.matrixId;
	renderer = ctor(this, objId, data.width, data.height);
}

void ProgramObject::Update(const Matrix *m, const ColorTransform *c)
{
	Object::Update(m, c);
	if (renderer)
		renderer->Update(&matrix, &colorTransform);
}

}	// namespace LWF
