#ifndef LWF_GRAPHIC_H
#define LWF_GRAPHIC_H

#include "lwf_object.h"

namespace LWF {

class Graphic : public Object
{
public:
	typedef vector<shared_ptr<Object> > DisplayList;

private:
	DisplayList m_displayList;

public:
	Graphic(LWF *l, Movie *p, int objId);
	void Update(const Matrix *m, const ColorTransform *c);
	void Render(bool v, int rOffset);
	void Destroy();
};

}	// namespace LWF

#endif
