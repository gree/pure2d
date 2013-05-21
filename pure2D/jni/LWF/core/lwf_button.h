#ifndef LWF_BUTTON_H
#define	LWF_BUTTON_H

#include "lwf_eventbutton.h"
#include "lwf_iobject.h"

namespace LWF {

class Button : public IObject
{
public:
	const Format::Button *data;
	Button *buttonLink;
	float hitX;
	float hitY;
	float width;
	float height;

private:
	Matrix m_invert;
	ButtonEventHandlers m_handler;

public:
	Button() {};
	Button(LWF *l, Movie *p, int objId, int instId, int mId = -1, int cId = -1);
	virtual ~Button() {};

	void AddHandlers(const ButtonEventHandlers *h);
	void Exec(int mId = 0, int cId = 0);
	void Update(const Matrix *m, const ColorTransform *c);
	void Render(bool v, int rOffset);
	void Destroy();
	void LinkButton();
	virtual bool CheckHit(float px, float py);
	virtual void EnterFrame();
	virtual void RollOver();
	virtual void RollOut();
	virtual void Press();
	virtual void Release();
	virtual void KeyPress(int code);
	void PlayAnimation(int condition, int code = 0);
};

}	// namespace LWF

#endif
