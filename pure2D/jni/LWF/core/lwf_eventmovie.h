#ifndef LWF_EVENTMOVIE_H
#define LWF_EVENTMOVIE_H

#include "lwf_type.h"

namespace LWF {

class Movie;

typedef function<void (Movie *)> MovieEventHandler;
typedef vector<pair<int, MovieEventHandler> > MovieEventHandlerList;
typedef map<string, MovieEventHandler> MovieEventHandlerDictionary;

class MovieEventHandlers
{
public:
	enum Type {
		LOAD,
		POSTLOAD,
		UNLOAD,
		ENTERFRAME,
		UPDATE,
		RENDER,
		EVENTS,
	};

private:
	bool m_empty;
	MovieEventHandlerList m_handlers[EVENTS];

public:
	MovieEventHandlers() : m_empty(true) {};
	bool Empty() const {return m_empty;}
	void Clear();
	void Clear(string type);
	void Add(const MovieEventHandlers *h);
	int Add(const MovieEventHandlerDictionary &h);
	void Remove(int id);
	void Call(Type type, Movie *target);
	bool Call(string type, Movie *target);

private:
	void UpdateEmpty();
};

}	// namespace LWF

#endif
