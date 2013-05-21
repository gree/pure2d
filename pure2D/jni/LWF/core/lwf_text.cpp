#include "lwf_core.h"
#include "lwf_data.h"
#include "lwf_renderer.h"
#include "lwf_text.h"

namespace LWF {

Text::Text(LWF *lwf, Movie *p, int objId)
	: Object(lwf, p, Format::Object::TEXT, objId)
{
	dataMatrixId = lwf->data->texts[objId].matrixId;
	renderer = lwf->rendererFactory->ConstructText(lwf, objId, this);
}

}	// namespace LWF
