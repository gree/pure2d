#ifndef LWF_TEXT_H
#define LWF_TEXT_H

#include "lwf_object.h"

namespace LWF {

class LWF;
class Movie;

class Text : public Object
{
public:
	Text(LWF *lwf, Movie *p, int objId);
};

}	// namespace LWF

#endif
