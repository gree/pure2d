#ifndef LWF_BITMAP_H
#define	LWF_BITMAP_H

#include "lwf_object.h"

namespace LWF {

class LWF;
class Movie;

class Bitmap : public Object
{
public:
	Bitmap(LWF *lwf, Movie *p, int objId);
};

}	// namespace LWF

#endif
