#include "lwf_core.h"
#include "lwf_object.h"
#include "lwf_renderer.h"
#include "lwf_utility.h"

namespace LWF {

Object::Object(LWF *l, Movie *p, int t, int objId)
{
	lwf = l;
	parent = p;
	type = t;
	execCount = 0;
	objectId = objId;
	matrixId = -1;
	colorTransformId = -1;
	dataMatrixId = -1;
	matrixIdChanged = true;
	colorTransformIdChanged = true;
	updated = false;

	matrix.Set(0, 0, 0, 0, 0, 0);
#if LWF_USE_ADDITIONALCOLOR
	colorTransform.Set(0, 0, 0, 0, 0, 0, 0, 0);
#else
	colorTransform.Set(0, 0, 0, 0);
#endif
}

void Object::Exec(int mId, int cId)
{
	if (matrixId != mId) {
		matrixIdChanged = true;
		matrixId = mId;
	}
	if (colorTransformId != cId) {
		colorTransformIdChanged = true;
		colorTransformId = cId;
	}
}

void Object::Update(const Matrix *m, const ColorTransform *c)
{
	updated = true;
	if (m) {
		Utility::CalcMatrix(lwf, &matrix, m, dataMatrixId);
		matrixIdChanged = false;
	}
	if (c) {
		Utility::CopyColorTransform(&colorTransform, c);
		colorTransformIdChanged = false;
	}
	lwf->RenderObject();
}

void Object::Render(bool v, int rOffset)
{
	if (renderer) {
		int rIndex = lwf->renderingIndex;
		int rIndexOffsetted = lwf->renderingIndexOffsetted;
		int rCount = lwf->renderingCount;
		if (rOffset != INT_MIN)
			rIndex = rIndexOffsetted - rOffset + rCount;
		renderer->Render(&matrix, &colorTransform, rIndex, rCount, v);
	}
	lwf->RenderObject();
}

void Object::Inspect(
	Inspector inspector, int hierarchy, int depth, int rOffset)
{
	int rIndex = lwf->renderingIndex;
	int rIndexOffsetted = lwf->renderingIndexOffsetted;
	int rCount = lwf->renderingCount;
	if (rOffset != INT_MIN)
		rIndex = rIndexOffsetted + rOffset + rCount;
	inspector(this, hierarchy, depth, rIndex);
	lwf->RenderObject();
}

void Object::Destroy()
{
	if (renderer) {
		renderer->Destruct();
		renderer.reset();
	}
}

}	// namespace LWF
