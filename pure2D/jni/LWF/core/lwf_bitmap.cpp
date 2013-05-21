#include "lwf_bitmap.h"
#include "lwf_core.h"
#include "lwf_data.h"
#include "lwf_renderer.h"

namespace LWF {

Bitmap::Bitmap(LWF *lwf, Movie *p, int objId)
	: Object(lwf, p, Format::Object::BITMAP, objId)
{
	dataMatrixId = lwf->data->bitmaps[objId].matrixId;
	renderer = lwf->rendererFactory->ConstructBitmap(lwf, objId, this);
}

}	// namespace LWF
