#ifndef LWF_RENDERER_H
#define LWF_RENDERER_H

#include "lwf_format.h"

namespace LWF {

class Bitmap;
class BitmapEx;
class Particle;
class Text;

class Renderer
{
public:
	LWF *lwf;

public:
	Renderer(LWF *l) : lwf(l) {}
	virtual ~Renderer() {}

	virtual void Destruct() = 0;
	virtual void Update(
		const Matrix *matrix, const ColorTransform *colorTransform) = 0;
	virtual void Render(
		const Matrix *matrix, const ColorTransform *colorTransform,
		int renderingIndex, int renderingCount, bool visible) = 0;
};

class TextRenderer : public Renderer
{
public:
	TextRenderer(LWF *l) : Renderer(l) {}
	virtual ~TextRenderer() {}

	virtual void SetText(string text) = 0;
};

class IRendererFactory
{
public:
	IRendererFactory() {}
	virtual ~IRendererFactory() {}
	virtual shared_ptr<Renderer> ConstructBitmap(
		LWF *lwf, int objId, Bitmap *bitmap) = 0;
	virtual shared_ptr<Renderer> ConstructBitmapEx(
		LWF *lwf, int objId, BitmapEx *bitmapEx) = 0;
	virtual shared_ptr<TextRenderer> ConstructText(
		LWF *lwf, int objId, Text *text) = 0;
	virtual shared_ptr<Renderer> ConstructParticle(
		LWF *lwf, int objId, Particle *particle) = 0;
	virtual void Init(LWF *lwf) = 0;
	virtual void BeginRender(LWF *lwf) = 0;
	virtual void EndRender(LWF *lwf) = 0;
	virtual void Destruct() = 0;

	virtual void FitForHeight(LWF *lwf, float w, float h) = 0;
	virtual void FitForWidth(LWF *lwf, float w, float h) = 0;
	virtual void ScaleForHeight(LWF *lwf, float w, float h) = 0;
	virtual void ScaleForWidth(LWF *lwf, float w, float h) = 0;
};

class NullRendererFactory : public IRendererFactory
{
public:
	shared_ptr<Renderer> ConstructBitmap(LWF *lwf,
		int objId, Bitmap *bitmap) {return shared_ptr<Renderer>();}
	shared_ptr<Renderer> ConstructBitmapEx(LWF *lwf,
		int objId, BitmapEx *bitmapEx) {return shared_ptr<Renderer>();}
	shared_ptr<TextRenderer> ConstructText(LWF *lwf,
		int objId, Text *text) {return shared_ptr<TextRenderer>();}
	shared_ptr<Renderer> ConstructParticle(LWF *lwf,
		int objId, Particle *particle) {return shared_ptr<Renderer>();}
	void Init(LWF *lwf) {}
	void BeginRender(LWF *lwf) {}
	void EndRender(LWF *lwf) {}
	void Destruct() {}
};

}	// namespace LWF

#endif
