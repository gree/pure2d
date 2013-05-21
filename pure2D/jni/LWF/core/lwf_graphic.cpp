#include "lwf_bitmap.h"
#include "lwf_bitmapex.h"
#include "lwf_core.h"
#include "lwf_data.h"
#include "lwf_format.h"
#include "lwf_graphic.h"
#include "lwf_text.h"

namespace LWF {

typedef Format::GraphicObject GType;

Graphic::Graphic(LWF *l, Movie *p, int objId)
	: Object(l, p, Format::Object::GRAPHIC, objId)
{
	const Format::Graphic &data = lwf->data->graphics[objId];
	int n = data.graphicObjects;
	m_displayList.resize(n);

	const vector<Format::GraphicObject> &graphicObjects =
		lwf->data->graphicObjects;
	for (int i = 0; i < n; ++i) {
		const Format::GraphicObject &gobj =
			graphicObjects[data.graphicObjectId + i];
		shared_ptr<Object> obj;
		int graphicObjectId = gobj.graphicObjectId;

		// Ignore error
		if (graphicObjectId == -1)
			continue;

		switch (gobj.graphicObjectType) {
		case GType::BITMAP:
			obj = make_shared<Bitmap>(lwf, parent, graphicObjectId);
			break;

		case GType::BITMAPEX:
			obj = make_shared<BitmapEx>(lwf, parent, graphicObjectId);
			break;

		case GType::TEXT:
			obj = make_shared<Text>(lwf, parent, graphicObjectId);
			break;
		}

		obj->Exec();
		m_displayList[i] = obj;
	}
}

void Graphic::Update(const Matrix *m, const ColorTransform *c)
{
	DisplayList::iterator it(m_displayList.begin()), itend(m_displayList.end());
	for (; it != itend; ++it)
		(*it)->Update(m, c);
}

void Graphic::Render(bool v, int rOffset)
{
	if (!v)
		return;
	DisplayList::iterator it(m_displayList.begin()), itend(m_displayList.end());
	for (; it != itend; ++it)
		(*it)->Render(v, rOffset);
}

void Graphic::Destroy()
{
	DisplayList::iterator it(m_displayList.begin()), itend(m_displayList.end());
	for (; it != itend; ++it)
		(*it)->Destroy();
	m_displayList.clear();
}

}	// namespace LWF
