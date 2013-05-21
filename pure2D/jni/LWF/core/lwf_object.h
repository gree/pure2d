#ifndef LWF_OBJECT_H
#define LWF_OBJECT_H

#include "lwf_format.h"

namespace LWF {

class LWF;
class Movie;
class Object;
class Renderer;

typedef Format::Object OType;

class Object
{
public:
	LWF *lwf;
	Movie *parent;
	int type;
	int execCount;
	int objectId;
	int matrixId;
	int colorTransformId;
	Matrix matrix;
	int dataMatrixId;
	ColorTransform colorTransform;
	shared_ptr<Renderer> renderer;
	bool matrixIdChanged;
	bool colorTransformIdChanged;
	bool updated;

public:
	Object() {}
	Object(LWF *l, Movie *p, int t, int objId);
	virtual ~Object() {}

	virtual void Exec(int mId = 0, int cId = 0);
	virtual void Update(const Matrix *m, const ColorTransform *c);
	virtual void Render(bool v, int rOffset);
	virtual void Inspect(
		Inspector inspector, int hierarchy, int depth, int rOffset);
	virtual void Destroy();

	bool IsButton() {return type == Format::Object::BUTTON ? true : false;}
	bool IsMovie() {return (type == Format::Object::MOVIE ||
		type == Format::Object::ATTACHEDMOVIE) ? true : false;}
	bool IsParticle() {return type == Format::Object::PARTICLE ? true : false;}
	bool IsProgramObject()
		{return type == Format::Object::PROGRAMOBJECT ? true : false;}
	bool IsText() {return type == Format::Object::TEXT ? true : false;}
};

}	// namespace LWF

#endif
