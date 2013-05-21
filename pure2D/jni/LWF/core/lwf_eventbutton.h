#ifndef LWF_EVENTBUTTON_H
#define	LWF_EVENTBUTTON_H

#include "lwf_type.h"

namespace LWF {

class Button;

typedef function<void (Button *)> ButtonEventHandler;
typedef function<void (Button *, int)> ButtonKeyPressHandler;
typedef vector<pair<int, ButtonEventHandler> > ButtonEventHandlerList;
typedef vector<pair<int, ButtonKeyPressHandler> > ButtonKeyPressHandlerList;
typedef map<string, ButtonEventHandler> ButtonEventHandlerDictionary;

class ButtonEventHandlers
{
public:
	enum Type {
		LOAD,
		UNLOAD,
		ENTERFRAME,
		UPDATE,
		RENDER,
		PRESS,
		RELEASE,
		ROLLOVER,
		ROLLOUT,
		KEYPRESS,
		EVENTS = KEYPRESS,
	};

private:
	bool m_empty;
	ButtonEventHandlerList m_handlers[EVENTS];
	ButtonKeyPressHandlerList m_keyPressHandler;

public:
	ButtonEventHandlers() : m_empty(true) {}
	bool Empty() const {return m_empty;}
	void Clear();
	void Clear(string type);
	void Add(const ButtonEventHandlers *h);
	int Add(const ButtonEventHandlerDictionary &h, ButtonKeyPressHandler kh);
	void Remove(int id);
	void Call(Type type, Button *target);
	void CallKEYPRESS(Button *target, int code);

private:
	void UpdateEmpty();
};

}	// namespace LWF

#endif
