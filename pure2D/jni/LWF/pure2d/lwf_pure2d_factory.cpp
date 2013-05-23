#include <android/log.h>
#include "lwf_core.h"
#include "lwf_movie.h"
#include "lwf_pure2d_bitmap.h"
#include "lwf_pure2d_factory.h"

#define LOG_TAG "pure2d::LWF"
#define LOG(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

namespace LWF {

shared_ptr<Renderer> Pure2DRendererFactory::ConstructBitmap(
	LWF *lwf, int objId, Bitmap *bitmap)
{
	return make_shared<Pure2DRendererBitmapRenderer>(this, lwf, bitmap);
}

shared_ptr<Renderer> Pure2DRendererFactory::ConstructBitmapEx(
	LWF *lwf, int objId, BitmapEx *bitmapEx)
{
	return make_shared<Pure2DRendererBitmapRenderer>(this, lwf, bitmapEx);
}

shared_ptr<Renderer> Pure2DRendererFactory::ConstructText(
	LWF *lwf, int objId, Text *text)
{
	return shared_ptr<Renderer>();
}

shared_ptr<Renderer> Pure2DRendererFactory::ConstructParticle(
	LWF *lwf, int objId, Particle *particle)
{
	return shared_ptr<Renderer>();
}

void Pure2DRendererFactory::Init(LWF *lwf)
{
	m_lwf = lwf;

	m_updateCount = -1;
}

void Pure2DRendererFactory::BeginRender(LWF *lwf)
{
	if (m_updateCount != lwf->updateCount) {
		m_updateCount = lwf->updateCount;
		m_updated = true;
	} else {
		m_updated = false;
	}

	if (m_lwf->parent)
		return;

	m_buffers.clear();
}

void Pure2DRendererFactory::EndRender(LWF *lwf)
{
}

void Pure2DRendererFactory::Destruct()
{
}

int Pure2DRendererFactory::GetBufferIndex(Pure2DRendererBitmapContext *context)
{
	if (m_lwf->parent) {
		Pure2DRendererFactory *parent =
			(Pure2DRendererFactory *)m_lwf->parent->lwf->rendererFactory.get();
		return parent->GetBufferIndex(context);
	}

	if (m_buffers.empty() ||
			m_buffers.back().glTextureId != context->GetGLTextureId()) {
		Buffer buffer(context->GetGLTextureId());
		m_buffers.push_back(buffer);
	}

	return m_buffers.back().index++;
}

void Pure2DRendererFactory::SetVertex(int offset, float x, float y,
	float z, float u, float v, float r, float g, float b, float a)
{
	if (m_lwf->parent) {
		Pure2DRendererFactory *parent =
			(Pure2DRendererFactory *)m_lwf->parent->lwf->rendererFactory.get();
		parent->SetVertex(offset, x, y, z, u, v, r, g, b, a);
		return;
	}

	m_buffers.back().vertices.resize(offset + 1);
	m_buffers.back().vertices[offset] =
		Vertex(x, y, z, u, v, 255 * r, 255 * g, 255 * b, 255 * a);
}

void Pure2DRendererFactory::SetIndex(int offset, unsigned short index)
{
	if (m_lwf->parent) {
		Pure2DRendererFactory *parent =
			(Pure2DRendererFactory *)m_lwf->parent->lwf->rendererFactory.get();
		parent->SetIndex(offset, index);
		return;
	}

	m_buffers.back().indices.resize(offset + 1);
	m_buffers.back().indices[offset] = index;
}

}	// namespace LWF
