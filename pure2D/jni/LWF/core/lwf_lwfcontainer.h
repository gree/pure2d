#ifndef LWF_LWFCONTAINER_H
#define LWF_LWFCONTAINER_H

#include "lwf_button.h"

namespace LWF {

class LWF;
class Movie;

class LWFContainer : public Button
{
public:
	shared_ptr<LWF> child;

public:
	LWFContainer(Movie *p, shared_ptr<LWF> c);
	bool CheckHit(float px, float py);
	void RollOver();
	void RollOut();
	void Press();
	void Release();
	void KeyPress(int code);
};

}	// namespace LWF

#endif
