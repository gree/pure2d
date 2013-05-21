#ifndef LWF_BITMAPEX_H
#define LWF_BITMAPEX_H

#include "lwf_object.h"

namespace LWF {

class LWF;
class Movie;

class BitmapEx : public Object
{
public:
	BitmapEx(LWF *lwf, Movie *p, int objId);
};

}	// namespace LWF

#endif
