#include "lwf_lwfcontainer.h"
#include "lwf_core.h"
#include "lwf_movie.h"

namespace LWF {

LWFContainer::LWFContainer(Movie *p, shared_ptr<LWF> c)
{
    lwf = p->lwf;
    parent = p;
    child = c;
}

bool LWFContainer::CheckHit(float px, float py)
{
    Button *button = child->InputPoint(px, py);
    return button ? true : false;
}

void LWFContainer::RollOver()
{
    // NOTHING TO DO
}

void LWFContainer::RollOut()
{
    if (child->focus) {
        child->focus->RollOut();
        child->ClearFocus(child->focus);
    }
}

void LWFContainer::Press()
{
    child->InputPress();
}

void LWFContainer::Release()
{
    child->InputRelease();
}

void LWFContainer::KeyPress(int code)
{
    child->InputKeyPress(code);
}

}	// namespace LWF
