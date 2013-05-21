#ifndef LWF_PROPERTY_H
#define LWF_PROPERTY_H

#include "lwf_type.h"

namespace LWF {

class LWF;

class Property
{
public:
	LWF *lwf;
	Matrix matrix;
	ColorTransform colorTransform;
	int renderingOffset;
	bool hasMatrix;
	bool hasColorTransform;
	bool hasRenderingOffset;
	float scaleX;
	float scaleY;
	float rotation;

public:
	Property(LWF *l);
	void Clear();
	void Move(float x, float y);
	void MoveTo(float x, float y);
	void Rotate(float degree);
	void RotateTo(float degree);
	void Scale(float x, float y);
	void ScaleTo(float x, float y);
	void SetMatrix(const Matrix *m, float sX = 1, float sY = 1, float r = 0);
	void SetAlpha(float alpha);
	void SetRed(float red);
	void SetGreen(float green);
	void SetBlue(float blue);
	void SetColorTransform(const ColorTransform *c);
	void SetRenderingOffset(int rOffset);
	void ClearRenderingOffset();

private:
	void SetScaleAndRotation();
};

}	// namespace LWF

#endif
