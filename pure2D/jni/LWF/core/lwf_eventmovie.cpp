#include "lwf_eventmovie.h"
#include "lwf_compat.h"

namespace LWF {

#define EType MovieEventHandlers
typedef map<string, int> table_t;
static table_t table;

static void PrepareTable()
{
	if (table.empty()) {
		const char *names[] = {
			"load",
			"postLoad",
			"unload",
			"enterFrame",
			"update",
			"render",
			0,
		};
		const int vals[] = {
			EType::LOAD,
			EType::POSTLOAD,
			EType::UNLOAD,
			EType::ENTERFRAME,
			EType::UPDATE,
			EType::RENDER,
		};

		for (int i = 0; names[i]; ++i)
			table[names[i]] = vals[i];
	}
};

void MovieEventHandlers::Clear()
{
	for (int i = 0; i < EVENTS; ++i)
		m_handlers[i].clear();
	m_empty = true;
}

void MovieEventHandlers::Clear(string type)
{
	PrepareTable();
	const table_t::iterator it = table.find(type);
	if (it == table.end())
		return;

	m_handlers[it->second].clear();
	UpdateEmpty();
}

void MovieEventHandlers::Add(const MovieEventHandlers *h)
{
	if (!h)
		return;

	for (int i = 0; i < EVENTS; ++i)
		m_handlers[i].insert(m_handlers[i].end(),
			h->m_handlers[i].begin(), h->m_handlers[i].end());

	if (m_empty)
		m_empty = h->Empty();
}

int MovieEventHandlers::Add(const MovieEventHandlerDictionary &h)
{
	static int eventId;
	int i = ++eventId;

	MovieEventHandlerDictionary::const_iterator it(h.begin()), itend(h.end());
	PrepareTable();
	table_t::const_iterator tit(table.begin()), titend(table.end());
	for (; it != itend; ++it)
		for (; tit != titend; ++tit)
			if (it->first == tit->first)
				m_handlers[tit->second].push_back(make_pair(i, it->second));

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
	bool operator()(const pair<int, MovieEventHandler> &h)
	{
		return h.first == id;
	}
};

void MovieEventHandlers::Remove(int id)
{
	if (id < 0)
		return;

	for (int i = 0; i < EVENTS; ++i)
		remove_if(m_handlers[i].begin(), m_handlers[i].end(), Pred(id));

	UpdateEmpty();
}

class Exec
{
private:
	Movie *target;
public:
	Exec(Movie *t) : target(t) {}
	void operator()(const pair<int, MovieEventHandler> &h)
	{
		h.second(target);
	}
};

void MovieEventHandlers::Call(Type type, Movie *target)
{
	scoped_ptr<MovieEventHandlerList>
		p(new MovieEventHandlerList(m_handlers[type]));
	for_each(p->begin(), p->end(), Exec(target));
}

bool MovieEventHandlers::Call(string type, Movie *target)
{
	PrepareTable();
	const table_t::iterator it = table.find(type);
	if (it == table.end())
		return false;
	Call((Type)it->second, target);
	return true;
}

void MovieEventHandlers::UpdateEmpty()
{
	m_empty = true;
	for (int i = 0; i < EVENTS; ++i) {
		if (!m_handlers[i].empty()) {
			m_empty = false;
			break;
		}
	}
}

}	// namespace LWF
