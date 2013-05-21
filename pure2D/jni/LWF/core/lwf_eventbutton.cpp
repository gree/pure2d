#include "lwf_eventbutton.h"
#include "lwf_compat.h"

namespace LWF {

#define EType ButtonEventHandlers
typedef map<string, int> table_t;
static table_t table;

static void PrepareTable()
{
	if (table.empty()) {
		const char *names[] = {
			"load",
			"unload",
			"enterFrame",
			"update",
			"render",
			"press",
			"release",
			"rollOver",
			"rollOut",
			0,
		};
		const int vals[] = {
			EType::LOAD,
			EType::UNLOAD,
			EType::ENTERFRAME,
			EType::UPDATE,
			EType::RENDER,
			EType::PRESS,
			EType::RELEASE,
			EType::ROLLOVER,
			EType::ROLLOUT,
		};

		for (int i = 0; names[i]; ++i)
			table[names[i]] = vals[i];
	}
};

void ButtonEventHandlers::Clear()
{
	for (int i = 0; i < EVENTS; ++i)
		m_handlers[i].clear();
	m_keyPressHandler.clear();
	m_empty = true;
}

void ButtonEventHandlers::Clear(string type)
{
	if (type == "keyPress") {
		m_keyPressHandler.clear();
	} else {
		PrepareTable();
		const table_t::iterator it = table.find(type);
		if (it == table.end())
			return;
		m_handlers[it->second].clear();
	}
	UpdateEmpty();
}

void ButtonEventHandlers::Add(const ButtonEventHandlers *h)
{
	if (!h)
		return;

	for (int i = 0; i < EVENTS; ++i)
		m_handlers[i].insert(m_handlers[i].end(),
			h->m_handlers[i].begin(), h->m_handlers[i].end());
	m_keyPressHandler.insert(m_keyPressHandler.end(),
			h->m_keyPressHandler.begin(), h->m_keyPressHandler.end());

	if (m_empty)
		m_empty = h->Empty();
}


int ButtonEventHandlers::Add(const ButtonEventHandlerDictionary &h,
	ButtonKeyPressHandler kh)
{
	static int eventId;
	int i = ++eventId;

	ButtonEventHandlerDictionary::const_iterator it(h.begin()), itend(h.end());
	PrepareTable();
	table_t::const_iterator tit(table.begin()), titend(table.end());
	for (; it != itend; ++it)
		for (; tit != titend; ++tit)
			if (it->first == tit->first)
				m_handlers[tit->second].push_back(make_pair(i, it->second));
	if (kh)
		m_keyPressHandler.push_back(make_pair(i, kh));

	if (m_empty)
		UpdateEmpty();

	return i;
}

class Pred
{
private:
	int id;
public:
	Pred(int i) : id(i) {}
	bool operator()(const pair<int, ButtonEventHandler> &h)
	{
		return h.first == id;
	}
};

class KPred
{
private:
	int id;
public:
	KPred(int i) : id(i) {}
	bool operator()(const pair<int, ButtonKeyPressHandler> &h)
	{
		return h.first == id;
	}
};

void ButtonEventHandlers::Remove(int id)
{
	for (int i = 0; i < EVENTS; ++i)
		remove_if(m_handlers[i].begin(), m_handlers[i].end(), Pred(id));
	remove_if(m_keyPressHandler.begin(), m_keyPressHandler.end(), KPred(id));

	UpdateEmpty();
}

class Exec
{
private:
	Button *target;
public:
	Exec(Button *t) : target(t) {}
	void operator()(const pair<int, ButtonEventHandler> &h)
	{
		h.second(target);
	}
};

void ButtonEventHandlers::Call(Type type, Button *target)
{
	scoped_ptr<ButtonEventHandlerList>
		p(new ButtonEventHandlerList(m_handlers[type]));
	for_each(p->begin(), p->end(), Exec(target));
}

class KExec
{
private:
	Button *target;
	int code;
public:
	KExec(Button *t, int c) : target(t), code(c) {}
	void operator()(const pair<int, ButtonKeyPressHandler> &h)
	{
		h.second(target, code);
	}
};

void ButtonEventHandlers::CallKEYPRESS(Button *target, int code)
{
	scoped_ptr<ButtonKeyPressHandlerList>
		p(new ButtonKeyPressHandlerList(m_keyPressHandler));
	for_each(p->begin(), p->end(), KExec(target, code));
}

void ButtonEventHandlers::UpdateEmpty()
{
	m_empty = true;
	for (int i = 0; i < EVENTS; ++i) {
		if (!m_handlers[i].empty()) {
			m_empty = false;
			break;
		}
	}
	if (m_empty)
		m_empty = m_keyPressHandler.empty();
}

}	// namespace LWF
