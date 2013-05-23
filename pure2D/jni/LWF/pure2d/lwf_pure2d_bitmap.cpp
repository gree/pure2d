#include "lwf_bitmap.h"
#include "lwf_bitmapex.h"
#include "lwf_core.h"
#include "lwf_data.h"
#include "lwf_pure2d_bitmap.h"
#include "lwf_pure2d_factory.h"

namespace LWF {

Pure2DRendererBitmapContext::Pure2DRendererBitmapContext(
	const Data *data, const Format::BitmapEx &bx)
{
	const Format::TextureFragment &f =
		data->textureFragments[bx.textureFragmentId];
	m_textureId = f.textureId;
	const Format::Texture &t = data->textures[f.textureId];
	m_preMultipliedAlpha = t.format == Format::TEXTUREFORMAT_PREMULTIPLIEDALPHA;

	float tw = (float)t.width;
	float th = (float)t.height;
	
	float x = (float)f.x;
	float y = (float)f.y;
	float u = (float)f.u;
	float v = (float)f.v;
	float w = (float)f.w;
	float h = (float)f.h;
	
	float bu = bx.u * w;
	float bv = bx.v * h;
	float bw = bx.w;
	float bh = bx.h;
	
	x += bu;
	y += bv;
	u += bu;
	v += bv;
	w *= bw;
	h *= bh;
	
	m_height = h / t.scale;
	
	float x0 = x / t.scale;
	float y0 = y / t.scale;
	float x1 = (x + w) / t.scale;
	float y1 = (y + h) / t.scale;

	m_vertices[0] = Vector2(x1, y0);
	m_vertices[1] = Vector2(x1, y1);
	m_vertices[2] = Vector2(x0, y0);
	m_vertices[3] = Vector2(x0, y1);

	float dw = 2.0f * tw;
	float dh = 2.0f * th;
	if (f.rotated == 0) {
		float u0 = (float)(2 * u + 1) / dw;
		float v0 = (float)(2 * v + 1) / dh;
		float u1 = u0 + (float)(w * 2 - 2) / dw;
		float v1 = v0 + (float)(h * 2 - 2) / dh;
		m_coordinates[0] = Vector2(u1, v1);
		m_coordinates[1] = Vector2(u1, v0);
		m_coordinates[2] = Vector2(u0, v1);
		m_coordinates[3] = Vector2(u0, v0);
	} else {
		float u0 = (float)(2 * u + 1) / dw;
		float v0 = (float)(2 * v + 1) / dh;
		float u1 = u0 + (float)(h * 2 - 2) / dw;
		float v1 = v0 + (float)(w * 2 - 2) / dh;
		m_coordinates[0] = Vector2(u0, v1);
		m_coordinates[1] = Vector2(u1, v1);
		m_coordinates[2] = Vector2(u0, v0);
		m_coordinates[3] = Vector2(u1, v0);
	}

	m_indices[0] = 0; m_indices[1] = 1; m_indices[2] = 2;
	m_indices[3] = 2; m_indices[4] = 1; m_indices[5] = 3;
}

Pure2DRendererBitmapContext::~Pure2DRendererBitmapContext()
{
}

Pure2DRendererBitmapRenderer::Pure2DRendererBitmapRenderer(
		Pure2DRendererFactory *factory, LWF *l, Bitmap *bitmap)
	: Renderer(l), m_factory(factory), m_context(0)
{
	int objId = bitmap->objectId;
	m_context = factory->GetBitmapContext(objId);
}

Pure2DRendererBitmapRenderer::Pure2DRendererBitmapRenderer(
		Pure2DRendererFactory *factory, LWF *l, BitmapEx *bitmapEx)
	: Renderer(l), m_factory(factory), m_context(0)
{
	int objId = bitmapEx->objectId;
	m_context = factory->GetBitmapExContext(objId);
}

void Pure2DRendererBitmapRenderer::Destruct()
{
}

void Pure2DRendererBitmapRenderer::Update(
	const Matrix *matrix, const ColorTransform *colorTransform)
{
}

void Pure2DRendererBitmapRenderer::Render(
	const Matrix *matrix, const ColorTransform *colorTransform,
	int renderingIndex, int renderingCount, bool visible)
{
	if (!m_context)
		return;

	int bufferIndex = m_factory->GetBufferIndex(m_context);

	if (!m_factory->IsUpdated())
		return;

	const unsigned short *indices = m_context->GetIndices();
	int indexOffset = bufferIndex * 4;
	int offset = bufferIndex * 6;
	for (int i = 0; i < 6; ++i)
		m_factory->SetIndex(offset + i, indices[i] + indexOffset);

	if (visible && colorTransform->multi.alpha > 0.0f) {
		float red = colorTransform->multi.red;
		float green = colorTransform->multi.green;
		float blue = colorTransform->multi.blue;
		float alpha = colorTransform->multi.alpha;

		if (m_context->IsPreMultipliedAlpha()) {
			red *= alpha;
			green *= alpha;
			blue *= alpha;
		}

		const Matrix *&m = matrix;
		float scaleX = m->scaleX;
		float skew0 = -m->skew0;
		float translateX = m->skew0 * m_context->GetHeight() + m->translateX;

		float skew1 = -m->skew1;
		float scaleY = m->scaleY;
		float translateY = -m->scaleY * m_context->GetHeight() - m->translateY;

		float translateZ = renderingCount - renderingIndex;

		const Pure2DRendererBitmapContext::Vector2 *v =
			m_context->GetVertices();
		const Pure2DRendererBitmapContext::Vector2 *c =
			m_context->GetCoordinates();
		offset = bufferIndex * 4;
		for (int i = 0; i < 4; ++i) {
			float x = v[i].x;
			float y = v[i].y;

			float vx = x * scaleX + y * skew0 + translateX;
			float vy = x * skew1 + y * scaleY + translateY;
			float vz = translateZ;

			m_factory->SetVertex(offset + i, vx, vy, vz,
				c[i].x, c[i].y, red, green, blue, alpha);
		}
	} else {
		offset = bufferIndex * 4;
		for (int i = 0; i < 4; ++i)
			m_factory->SetVertex(offset + i, 0, 0, 0, 0, 0, 0, 0, 0, 0);
	}
}

}   // namespace LWF
