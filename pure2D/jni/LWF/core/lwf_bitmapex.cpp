#include "lwf_bitmapex.h"
#include "lwf_core.h"
#include "lwf_data.h"
#include "lwf_renderer.h"

namespace LWF {

BitmapEx::BitmapEx(LWF *lwf, Movie *p, int objId)
	: Object(lwf, p, Format::Object::BITMAPEX, objId)
{
	dataMatrixId = lwf->data->bitmapExs[objId].matrixId;
	renderer = lwf->rendererFactory->ConstructBitmapEx(lwf, objId, this);
}

}	// namespace LWF
