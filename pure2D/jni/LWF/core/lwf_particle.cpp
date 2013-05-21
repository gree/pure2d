#include "lwf_core.h"
#include "lwf_data.h"
#include "lwf_particle.h"
#include "lwf_renderer.h"

namespace LWF {

Particle::Particle(LWF *lwf, Movie *p, int objId)
	: Object(lwf, p, Format::Object::PARTICLE, objId)
{
	dataMatrixId = lwf->data->particles[objId].matrixId;
	renderer = lwf->rendererFactory->ConstructParticle(lwf, objId, this);
}

}	// namespace LWF
