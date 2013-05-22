#include <GLES/gl.h>
#include "lwf_core.h"
#include "lwf_movie.h"
#include "lwf_pure2d_bitmap.h"
#include "lwf_pure2d_factory.h"

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

	GLuint buffers[2];
	glGenBuffers(2, buffers);
	m_vertexBuffer = buffers[0];
	m_indicesBuffer = buffers[1];

	m_bitmaps = 0;
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

	m_index = 0;

	if (m_updated) {
		if ((size_t)m_bitmaps > m_contexts.size()) {
			m_contexts.resize(m_bitmaps);
			m_vertices.resize(m_bitmaps * 4);
			m_indices.resize(m_bitmaps * 6);
		}
	}
}

void Pure2DRendererFactory::EndRender(LWF *lwf)
{
	if (m_index == 0 || m_lwf->parent)
		return;

	// TODO bind texture
	// TODO blendmode

	// TODO matrix

	size_t size = sizeof(Vertex) * 4 * m_index;
	glBindBuffer(GL_ARRAY_BUFFER, m_vertexBuffer);
	glBufferData(GL_ARRAY_BUFFER, size, &m_vertices[0], GL_DYNAMIC_DRAW);

	size = sizeof(GLushort) * 6 * m_index;
	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_indicesBuffer);
	glBufferData(GL_ELEMENT_ARRAY_BUFFER, size, &m_indices[0], GL_DYNAMIC_DRAW);

	glVertexPointer(3, GL_FLOAT, sizeof(Vertex), (GLvoid *)0);
	glTexCoordPointer(2, GL_FLOAT, sizeof(Vertex), (GLvoid *)12);
	glColorPointer(4, GL_UNSIGNED_BYTE, sizeof(Vertex), (GLvoid *)20);
	glEnableClientState(GL_VERTEX_ARRAY);
	glEnableClientState(GL_TEXTURE_COORD_ARRAY);
	glEnableClientState(GL_COLOR_ARRAY);

	glDrawElements(GL_TRIANGLES, (GLsizei)m_index * 6, GL_UNSIGNED_SHORT, 0);

	glBindBuffer(GL_ARRAY_BUFFER, 0);
	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
}

void Pure2DRendererFactory::Destruct()
{
	GLuint buffers[] = {m_vertexBuffer, m_indicesBuffer};
	glDeleteBuffers(2, buffers);
}

void Pure2DRendererFactory::AddBitmap()
{
	if (m_lwf->parent) {
		Pure2DRendererFactory *parent =
			(Pure2DRendererFactory *)m_lwf->parent->lwf->rendererFactory.get();
		parent->AddBitmap();
		return;
	}

	++m_bitmaps;
}

void Pure2DRendererFactory::DeleteBitmap()
{
	if (m_lwf->parent) {
		Pure2DRendererFactory *parent =
			(Pure2DRendererFactory *)m_lwf->parent->lwf->rendererFactory.get();
		parent->DeleteBitmap();
		return;
	}

	--m_bitmaps;
}

int Pure2DRendererFactory::GetBufferIndex(Pure2DRendererBitmapContext *context)
{
	if (m_lwf->parent) {
		Pure2DRendererFactory *parent =
			(Pure2DRendererFactory *)m_lwf->parent->lwf->rendererFactory.get();
		return parent->GetBufferIndex(context);
	}

	m_contexts.push_back(context);
	return m_index++;
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

	m_vertices[offset] =
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

	m_indices[offset] = index;
}

}	// namespace LWF
