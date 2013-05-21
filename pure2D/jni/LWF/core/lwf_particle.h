#ifndef LWF_PARTICLE_H
#define LWF_PARTICLE_H

#include "lwf_object.h"

namespace LWF {

class LWF;
class Movie;

class Particle : public Object
{
public:
	Particle(LWF *lwf, Movie *p, int objId);
};

}	// namespace LWF

#endif
