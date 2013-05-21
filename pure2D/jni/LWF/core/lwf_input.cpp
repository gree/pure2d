#include "lwf_core.h"
#include "lwf_button.h"

namespace LWF {

Button *LWF::InputPoint(int px, int py)
{
	intercepted = false;

	if (!interactive)
		return 0;

	float x = px;
	float y = py;

	pointX = x;
	pointY = y;

	bool found = false;
	for (Button *button = buttonHead; button; button = button->buttonLink) {
		if (button->CheckHit(x, y)) {
			if (!m_allowButtonList.empty()) {
				if (m_allowButtonList.find(button->instanceId) ==
						m_allowButtonList.end()) {
					if (interceptByNotAllowOrDenyButtons) {
						intercepted = true;
						break;
					} else {
						continue;
					}
				}
			} else if (!m_denyButtonList.empty()) {
				if (m_denyButtonList.find(button->instanceId) !=
						m_denyButtonList.end()) {
					if (interceptByNotAllowOrDenyButtons) {
						intercepted = true;
						break;
					} else {
						continue;
					}
				}
			}

			found = true;
			if (focus != button) {
				if (focus)
					focus->RollOut();
				focus = button;
				focus->RollOver();
			}
			break;
		}
	}
	if (!found && focus) {
		focus->RollOut();
		focus = 0;
	}

	return focus;
}

void LWF::InputPress()
{
	if (!interactive)
		return;

	pressing = true;

	if (focus) {
		pressed = focus;
		focus->Press();
	}
}

void LWF::InputRelease()
{
	if (!interactive)
		return;

	pressing = false;

	if (focus && pressed == focus) {
		focus->Release();
		pressed = 0;
	}
}

void LWF::InputKeyPress(int code)
{
	if (!interactive)
		return;

	for (Button *button = buttonHead; button; button = button->buttonLink) {
		button->KeyPress(code);
	}
}

}	// namespace LWF
