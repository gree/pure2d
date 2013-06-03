#ifndef LWF_PURE2D_FACTORY_H
#define LWF_PURE2D_FACTORY_H

#include "lwf_renderer.h"
#include "lwf_type.h"

namespace LWF {

class Pure2DRendererBitmapContext;

class Pure2DRendererFactory : public IRendererFactory
{
public:
	struct Vertex {
		float x;
		float y;
		float z;
		float u;
		float v;
		unsigned char r;
		unsigned char g;
		unsigned char b;
		unsigned char a;

		Vertex() {}
		Vertex(float ax, float ay, float az, float au, float av,
				unsigned char ar, unsigned char ag, unsigned char ab,
				unsigned char aa)
			: x(ax), y(ay), z(az), u(au), v(av), r(ar), g(ag), b(ab), a(aa) {}
	};

	struct Buffer {
		vector<Vertex> vertices;
		vector<unsigned short> indices;
		int glTextureId;
		bool preMultipliedAlpha;
		float height;
		int index;

		Buffer() : glTextureId(-1), index(0) {}
		Buffer(int gId, bool p, float h) : glTextureId(gId),
			preMultipliedAlpha(p), height(h), index(0) {}
	};

protected:
	LWF *m_lwf;
	const vector<shared_ptr<Pure2DRendererBitmapContext> > &m_bitmapContexts;
	const vector<shared_ptr<Pure2DRendererBitmapContext> > &m_bitmapExContexts;
	vector<Buffer> m_buffers;
	unsigned int m_vertexBuffer;
	unsigned int m_indicesBuffer;
	int m_updateCount;
	int m_updated;

public:
	Pure2DRendererFactory(const vector<shared_ptr<Pure2DRendererBitmapContext> > &bitmapContexts, const vector<shared_ptr<Pure2DRendererBitmapContext> > &bitmapExContexts) : m_bitmapContexts(bitmapContexts), m_bitmapExContexts(bitmapExContexts) {}

	shared_ptr<Renderer> ConstructBitmap(
		LWF *lwf, int objId, Bitmap *bitmap);
	shared_ptr<Renderer> ConstructBitmapEx(
		LWF *lwf, int objId, BitmapEx *bitmapEx);
	shared_ptr<TextRenderer> ConstructText(
		LWF *lwf, int objId, Text *text);
	shared_ptr<Renderer> ConstructParticle(
		LWF *lwf, int objId, Particle *particle);

	void Init(LWF *lwf);
	void BeginRender(LWF *lwf);
	void EndRender(LWF *lwf);
	void Destruct();

	Pure2DRendererBitmapContext *GetBitmapContext(int id)
		{return id < 0 || id >= m_bitmapContexts.size() ?
			0 : m_bitmapContexts[id].get();}
	Pure2DRendererBitmapContext *GetBitmapExContext(int id)
		{return id < 0 || id >= m_bitmapExContexts.size() ?
			0 : m_bitmapExContexts[id].get();}
	bool IsUpdated() const {return m_updated;}
	int GetBufferIndex(Pure2DRendererBitmapContext *context);
	void SetVertex(int offset, float x, float y, float z,
		float u, float v, float r, float g, float b, float a);
	void SetIndex(int offset, unsigned short index);
};

}	// namespace LWF

#endif
