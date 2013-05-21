#include "lwf_bitmap.h"
#include "lwf_bitmapex.h"
#include "lwf_button.h"
#include "lwf_core.h"
#include "lwf_data.h"
#include "lwf_eventbutton.h"
#include "lwf_graphic.h"
#include "lwf_lwfcontainer.h"
#include "lwf_movie.h"
#include "lwf_particle.h"
#include "lwf_property.h"
#include "lwf_programobj.h"
#include "lwf_text.h"
#include "lwf_utility.h"
#include "lwf_compat.h"

namespace LWF {

typedef ButtonEventHandlers BEType;
typedef MovieEventHandlers METype;

Movie::Movie(LWF *l, Movie *p, int objId, int instId, int mId, int cId,
		bool attached, const MovieEventHandlers *handler)
	: IObject(l, p,
		attached ? OType::ATTACHEDMOVIE : OType::MOVIE, objId, instId)
{
	matrixId = mId;
	colorTransformId = cId;

	data = &lwf->data->movies[objId];
	totalFrames = data->frames;

	depth = 0;
	visible = true;
	playing = true;
	active = true;
	hasButton = false;

	m_property = make_shared<Property>(lwf);
	m_instanceHead = 0;
	m_instanceTail = 0;

	m_currentFrameInternal = -1;
	currentFrame = 0;
	m_currentFrameCurrent = -1;
	m_execedFrame = -1;
	m_animationPlayedFrame = -1;
	m_lastControlOffset = -1;
	m_lastControls = -1;
	m_lastControlAnimationOffset = -1;
	m_movieExecCount = -1;
	m_postExecCount = -1;
	m_jumped = false;
	m_overriding = false;
	m_postLoaded = false;
	m_lastHasButton = false;
	m_skipped = false;
	m_attachMovieExeced = false;
	m_attachMoviePostExeced = false;

	m_displayList.resize(data->depths);

	PlayAnimation(ClipEvent::LOAD);

	m_handler.Add(lwf->GetMovieEventHandlers(this));
	m_handler.Add(handler);
	if (!m_handler.Empty())
		m_handler.Call(METype::LOAD, this);

	lwf->ExecMovieCommand();
}

void Movie::AddHandlers(const MovieEventHandlers *h)
{
	m_handler.Add(h);
}

Point Movie::GlobalToLocal(const Point &point) const
{
	float px;
	float py;
	Matrix invert;
	Utility::InvertMatrix(&invert, &matrix);
	Utility::CalcMatrixToPoint(px, py, point.x, point.y, &invert);
	Point p(px, py);
	return p;
}

Point Movie::LocalToGlobal(const Point &point) const
{
	float px;
	float py;
	Utility::CalcMatrixToPoint(px, py, point.x, point.y, &matrix);
	Point p(px, py);
	return p;
}

void Movie::ExecObject(int dlDepth, int objId,
	int matrixId, int colorTransformId, int instId)
{
	// Ignore error
	if (objId == -1)
		return;
	const Data *d = lwf->data.get();
	const Format::Object &dataObject = d->objects[objId];
	int dataObjectId = dataObject.objectId;
	shared_ptr<Object> obj = m_displayList[dlDepth];

	if (obj && (obj->type != dataObject.objectType ||
			obj->objectId != dataObjectId || (obj->IsMovie() &&
			((IObject *)obj.get())->instanceId != instId))) {
		obj->Destroy();
		obj.reset();
	}

	if (!obj) {
		switch (dataObject.objectType) {
		case OType::BUTTON:
			obj = make_shared<Button>(lwf,
				this, dataObjectId, instId, matrixId, colorTransformId);
			break;

		case OType::GRAPHIC:
			obj = make_shared<Graphic>(lwf, this, dataObjectId);
			break;

		case OType::MOVIE:
			obj = make_shared<Movie>(lwf, this,
				dataObjectId, instId, matrixId, colorTransformId);
			break;

		case OType::BITMAP:
			obj = make_shared<Bitmap>(lwf, this, dataObjectId);
			break;

		case OType::BITMAPEX:
			obj = make_shared<BitmapEx>(lwf, this, dataObjectId);
			break;

		case OType::TEXT:
			obj = make_shared<Text>(lwf, this, dataObjectId);
			break;

		case OType::PARTICLE:
			obj = make_shared<Particle>(lwf, this, dataObjectId);
			break;

		case OType::PROGRAMOBJECT:
			obj = make_shared<ProgramObject>(lwf, this, dataObjectId);
			break;
		}
	}

	if (obj->IsMovie() || obj->IsButton()) {
		IObject *instance = (IObject *)obj.get();
		instance->linkInstance = 0;
		if (!m_instanceHead)
			m_instanceHead = instance;
		else
			m_instanceTail->linkInstance = instance;
		m_instanceTail = instance;
		if (obj->IsButton())
			hasButton = true;
	}

	m_displayList[dlDepth] = obj;
	obj->execCount = m_movieExecCount;
	obj->Exec(matrixId, colorTransformId);
}

void Movie::Override(bool overriding)
{
	m_overriding = overriding;
}

void Movie::Exec(int matrixId, int colorTransformId)
{
	m_attachMovieExeced = false;
	m_attachMoviePostExeced = false;
	IObject::Exec(matrixId, colorTransformId);
}

void Movie::PostExec(bool progressing)
{
	hasButton = false;
	if (!active)
		return;

	m_execedFrame = -1;
	bool postExeced = m_postExecCount == lwf->execCount;
	if (progressing && playing && !m_jumped && !postExeced) {
		++m_currentFrameInternal;
		currentFrame = m_currentFrameInternal + 1;
	}
	for (;;) {
		if (m_currentFrameInternal < 0 ||
				m_currentFrameInternal >= totalFrames) {
			m_currentFrameInternal = 0;
			currentFrame = m_currentFrameInternal + 1;
		}
		if (m_currentFrameInternal == m_execedFrame)
			break;

		m_currentFrameCurrent = m_currentFrameInternal;
		m_execedFrame = m_currentFrameCurrent;
		const Data *d = lwf->data.get();
		const Format::Frame &frame = d->frames[
			data->frameOffset + m_currentFrameCurrent];

		int controlAnimationOffset;
		IObject *instance;

		if (m_lastControlOffset == frame.controlOffset &&
				m_lastControls == frame.controls) {

			controlAnimationOffset = m_lastControlAnimationOffset;

			if (m_skipped) {
				instance = m_instanceHead;
				while (instance) {
					if (instance->IsMovie()) {
						Movie *movie = (Movie *)instance;
						movie->m_attachMovieExeced = false;
						movie->m_attachMoviePostExeced = false;
					} else if (instance->IsButton()) {
						((Button *)instance)->EnterFrame();
					}
					instance = instance->linkInstance;
				}
				hasButton = m_lastHasButton;
			} else {
				for (int dlDepth = 0; dlDepth < data->depths; ++dlDepth) {
					Object *obj = m_displayList[dlDepth].get();
					if (obj) {
						if (!postExeced) {
							obj->matrixIdChanged = false;
							obj->colorTransformIdChanged = false;
						}
						if (obj->IsMovie()) {
							Movie *movie = (Movie *)obj;
							movie->m_attachMovieExeced = false;
							movie->m_attachMoviePostExeced = false;
						} else if (obj->IsButton()) {
							((Button *)obj)->EnterFrame();
							hasButton = true;
						}
					}
				}
				m_lastHasButton = hasButton;
				m_skipped = true;
			}

		} else {
			++m_movieExecCount;
			m_instanceHead = 0;
			m_instanceTail = 0;
			m_lastControlOffset = frame.controlOffset;
			m_lastControls = frame.controls;
			controlAnimationOffset = -1;
			for (int i = 0; i < frame.controls; ++i) {
				const Format::Control &control =
					d->controls[frame.controlOffset + i];

				switch (control.controlType) {
				case Format::Control::MOVE:
					{
						const Format::Place &p = d->places[control.controlId];
						ExecObject(p.depth, p.objectId,
							p.matrixId, 0, p.instanceId);
					}
					break;

				case Format::Control::MOVEM:
					{
						const Format::ControlMoveM &ctrl =
							d->controlMoveMs[control.controlId];
						const Format::Place &p = d->places[ctrl.placeId];
						ExecObject(p.depth, p.objectId,
							ctrl.matrixId, 0, p.instanceId);
					}
					break;

				case Format::Control::MOVEC:
					{
						const Format::ControlMoveC &ctrl =
							d->controlMoveCs[control.controlId];
						const Format::Place &p = d->places[ctrl.placeId];
						ExecObject(p.depth, p.objectId, p.matrixId,
							ctrl.colorTransformId, p.instanceId);
					}
					break;

				case Format::Control::MOVEMC:
					{
						const Format::ControlMoveMC &ctrl =
							d->controlMoveMCs[control.controlId];
						const Format::Place &p = d->places[ctrl.placeId];
						ExecObject(p.depth, p.objectId, ctrl.matrixId,
							ctrl.colorTransformId, p.instanceId);
					}
					break;

				case Format::Control::ANIMATION:
					if (controlAnimationOffset == -1)
						controlAnimationOffset = i;
					break;

				case Format::Control::CONTROL_MAX:
					// SUPPRESS WARNING
					break;
				}
			}

			m_lastControlAnimationOffset = controlAnimationOffset;
			m_lastHasButton = hasButton;

			for (int dlDepth = 0; dlDepth < data->depths; ++dlDepth) {
				Object *obj = m_displayList[dlDepth].get();
				if (obj && obj->execCount != m_movieExecCount) {
					obj->Destroy();
					m_displayList[dlDepth].reset();
				}
			}
		}

		m_attachMovieExeced = true;
		if (!m_attachedMovies.empty()) {
			AttachedMovieList::iterator it(m_attachedMovieList.begin()),
				itend(m_attachedMovieList.end());
			for (; it != itend; ++it)
				if (it->second)
					it->second->Exec();
		}

		m_attachMoviePostExeced = true;
		instance = m_instanceHead;
		while (instance) {
			if (instance->IsMovie()) {
				Movie *movie = (Movie *)instance;
				movie->PostExec(progressing);
				if (!hasButton && movie->hasButton)
					hasButton = true;
			}
			instance = instance->linkInstance;
		}

		if (m_attachedMovies.empty()) {
			DetachDict::const_iterator
				dit(m_detachedMovies.begin()), ditend(m_detachedMovies.end());
			for (; dit != ditend; ++dit) {
				AttachedMovies::iterator it = m_attachedMovies.find(dit->first);
				if (it != m_attachedMovies.end())
					DeleteAttachedMovie(this, it->second, true, false);
			}
			m_detachedMovies.clear();

			AttachedMovieList::iterator it(m_attachedMovieList.begin()),
				itend(m_attachedMovieList.end());
			for (; it != itend; ++it) {
				if (it->second) {
					it->second->PostExec(progressing);
					if (!hasButton && it->second->hasButton)
						hasButton = true;
				}
			}
		}

		if (!m_attachedLWFs.empty())
			hasButton = true;

		if (!m_postLoaded) {
			m_postLoaded = true;
			if (!m_handler.Empty())
				m_handler.Call(METype::POSTLOAD, this);
		}

		if (controlAnimationOffset != -1 &&
				m_execedFrame == m_currentFrameInternal) {
			bool animationPlayed = m_animationPlayedFrame ==
				m_currentFrameCurrent && !m_jumped;
			if (!animationPlayed) {
				for (int i = controlAnimationOffset;
						i < frame.controls; ++i) {
					const Format::Control &control =
						d->controls[frame.controlOffset + i];
					lwf->PlayAnimation(control.controlId, this);
				}
			}
		}

		m_animationPlayedFrame = m_currentFrameCurrent;
		if (m_currentFrameCurrent == m_currentFrameInternal)
			m_jumped = false;
	}

	PlayAnimation(ClipEvent::ENTERFRAME);
	if (!m_handler.Empty())
		m_handler.Call(METype::ENTERFRAME, this);
	m_postExecCount = lwf->execCount;
}

void Movie::UpdateObject(Object *obj, const Matrix *m, const ColorTransform *c,
	bool matrixChanged, bool colorTransformChanged)
{
	const Matrix *objm;
	if (obj->IsMovie() && ((Movie *)obj)->m_property->hasMatrix)
		objm = m;
	else if (matrixChanged || !obj->updated || obj->matrixIdChanged)
		objm = Utility::CalcMatrix(lwf, &m_matrix1, m, obj->matrixId);
	else
		objm = 0;

	const ColorTransform *objc;
	if (obj->IsMovie() && ((Movie *)obj)->m_property->hasColorTransform)
		objc = c;
	else if (colorTransformChanged ||
			!obj->updated || obj->colorTransformIdChanged)
		objc = Utility::CalcColorTransform(
			lwf, &m_colorTransform1, c, obj->colorTransformId);
	else
		objc = 0;

	obj->Update(objm, objc);
}

void Movie::Update(const Matrix *m, const ColorTransform *c)
{
	if (!active)
		return;

	bool matrixChanged;
	bool colorTransformChanged;

	if (m_overriding) {
		matrixChanged = true;
		colorTransformChanged = true;
	} else {
		matrixChanged = matrix.SetWithComparing(m);
		colorTransformChanged = colorTransform.SetWithComparing(c);
	}

	if (!m_handler.Empty())
		m_handler.Call(METype::UPDATE, this);

	if (m_property->hasMatrix) {
		matrixChanged = true;
		m = Utility::CalcMatrix(&m_matrix0, &matrix, &m_property->matrix);
	} else {
		m = &matrix;
	}

	if (m_property->hasColorTransform) {
		colorTransformChanged = true;
		c = Utility::CalcColorTransform(
			&m_colorTransform0, &colorTransform, &m_property->colorTransform);
	} else {
		c = &colorTransform;
	}

	for (int dlDepth = 0; dlDepth < data->depths; ++dlDepth) {
		Object *obj = m_displayList[dlDepth].get();
		if (obj)
			UpdateObject(obj, m, c, matrixChanged, colorTransformChanged);
	}

	if (!m_attachedMovies.empty()) {
		AttachedMovieList::iterator it(m_attachedMovieList.begin()),
			itend(m_attachedMovieList.end());
		for (; it != itend; ++it)
			if (it->second)
				it->second->UpdateObject(it->second.get(),
					m, c, matrixChanged, colorTransformChanged);
	}

	if (!m_attachedLWFs.empty()) {
		DetachDict::const_iterator
			dit(m_detachedLWFs.begin()), ditend(m_detachedLWFs.end());
		for (; dit != ditend; ++dit) {
			AttachedLWFs::iterator it = m_attachedLWFs.find(dit->first);
			if (it != m_attachedLWFs.end())
				DeleteAttachedLWF(this, it->second, true, false);
		}
		m_detachedLWFs.clear();

		AttachedLWFList::iterator
			it(m_attachedLWFList.begin()), itend(m_attachedLWFList.end());
		for (; it != itend; ++it) {
			if (it->second)
				lwf->RenderObject(
					it->second->child->Exec(lwf->thisTick, m, c));
		}
	}
}

void Movie::LinkButton()
{
	if (!visible || !active || !hasButton)
		return;

	if (!m_attachedLWFs.empty()) {
		AttachedLWFList::iterator
			it(m_attachedLWFList.begin()), itend(m_attachedLWFList.end());
		for (; it != itend; ++it)
			if (it->second)
				it->second->LinkButton();
	}

	if (!m_attachedMovies.empty()) {
		AttachedMovieList::iterator it(m_attachedMovieList.begin()),
			itend(m_attachedMovieList.end());
		for (; it != itend; ++it)
			if (it->second && it->second->hasButton)
				it->second->LinkButton();
	}

	for (int dlDepth = 0; dlDepth < data->depths; ++dlDepth) {
		Object *obj = m_displayList[dlDepth].get();
		if (obj) {
			if (obj->IsButton()) {
				((Button *)obj)->LinkButton();
			} else if (obj->IsMovie()) {
				Movie *movie = (Movie *)obj;
				if (movie->hasButton)
					movie->LinkButton();
			}
		}
	}
}

void Movie::Render(bool v, int rOffset)
{
	if (!visible || !active)
		v = false;

	if (v && !m_handler.Empty())
		m_handler.Call(METype::RENDER, this);

	if (m_property->hasRenderingOffset) {
		lwf->RenderOffset();
		rOffset = m_property->renderingOffset;
	}
	if (rOffset == INT_MIN)
		lwf->ClearRenderOffset();

	for (int dlDepth = 0; dlDepth < data->depths; ++dlDepth) {
		Object *obj = m_displayList[dlDepth].get();
		if (obj)
			obj->Render(v, rOffset);
	}

	if (!m_attachedMovies.empty()) {
		AttachedMovieList::iterator it(m_attachedMovieList.begin()),
			itend(m_attachedMovieList.end());
		for (; it != itend; ++it)
			if (it->second)
				it->second->Render(v, rOffset);
	}

	if (!m_attachedLWFs.empty()) {
		AttachedLWFList::iterator
			it(m_attachedLWFList.begin()), itend(m_attachedLWFList.end());
		for (; it != itend; ++it) {
			if (it->second) {
				LWF *child = it->second->child.get();
				child->SetAttachVisible(v);
				lwf->RenderObject(child->Render(lwf->renderingIndex,
					lwf->renderingCount, rOffset));
			}
		}
	}
}

void Movie::Inspect(
	Inspector inspector, int hierarchy, int inspectDepth, int rOffset)
{
	if (m_property->hasRenderingOffset) {
		lwf->RenderOffset();
		rOffset = m_property->renderingOffset;
	}
	if (rOffset == INT_MIN)
		lwf->ClearRenderOffset();

	inspector(this, hierarchy, inspectDepth, rOffset);

	++hierarchy;

	int d;
	for (d = 0; d < data->depths; ++d) {
		Object *obj = m_displayList[d].get();
		if (obj)
			obj->Inspect(inspector, hierarchy, d, rOffset);
	}

	if (!m_attachedMovies.empty()) {
		AttachedMovieList::iterator
			it(m_attachedMovieList.begin()), itend(m_attachedMovieList.end());
		for (; it != itend; ++it)
			if (it->second)
				it->second->Inspect(inspector, hierarchy, d++, rOffset);
	}

	if (!m_attachedLWFs.empty()) {
		AttachedLWFList::iterator
			it(m_attachedLWFList.begin()), itend(m_attachedLWFList.end());
		for (; it != itend; ++it) {
			if (it->second) {
				lwf->RenderObject(it->second->child->Inspect(
					inspector, hierarchy, d++, rOffset));
			}
		}
	}
}

void Movie::Destroy()
{
	for (int dlDepth = 0; dlDepth < data->depths; ++dlDepth) {
		Object *obj = m_displayList[dlDepth].get();
		if (obj)
			obj->Destroy();
	}

	if (!m_attachedMovies.empty()) {
		AttachedMovies::iterator
			it(m_attachedMovies.begin()), itend(m_attachedMovies.end());
		for (; it != itend; ++it)
			it->second->Destroy();
		m_attachedMovies.clear();
		m_attachedMovieList.clear();
		m_detachedMovies.clear();
	}

	if (!m_attachedLWFs.empty()) {
		AttachedLWFs::iterator
			it(m_attachedLWFs.begin()), itend(m_attachedLWFs.end());
		for (; it != itend; ++it)
			if (it->second->child->detachHandler)
				it->second->child->detachHandler(it->second->child.get());
		m_attachedLWFs.clear();
		m_attachedLWFList.clear();
		m_detachedLWFs.clear();
	}

	PlayAnimation(ClipEvent::UNLOAD);

	if (!m_handler.Empty())
		m_handler.Call(METype::UNLOAD, this);

	m_displayList.clear();
	m_property.reset();

	IObject::Destroy();
}

void Movie::PlayAnimation(int clipEvent)
{
	const vector<Format::MovieClipEvent> &clipEvents =
		lwf->data->movieClipEvents;
	for (int i = 0; i < data->clipEvents; ++i) {
		const Format::MovieClipEvent &c = clipEvents[data->clipEventId + i];
		if ((c.clipEvent & (int)clipEvent) != 0)
			lwf->PlayAnimation(c.animationId, this);
	}
}

int Movie::SearchFrame(string label) const
{
	return lwf->SearchFrame(this, label);
}

int Movie::SearchFrame(int stringId) const
{
	return lwf->SearchFrame(this, stringId);
}

Movie *Movie::SearchMovieInstance(int stringId, bool recursive) const
{
	for (IObject *instance = m_instanceHead; instance;
			instance = instance->linkInstance) {
		if (instance->IsMovie() && lwf->GetInstanceNameStringId(
				instance->instanceId) == stringId) {
			return (Movie *)instance;
		} else if (recursive && instance->IsMovie()) {
			Movie *i = ((Movie *)instance)->SearchMovieInstance(
				stringId, recursive);
			if (i)
				return i;
		}
	}
	return 0;
}

Movie *Movie::SearchMovieInstance(string instanceName, bool recursive) const
{
	return SearchMovieInstance(lwf->GetStringId(instanceName), recursive);
}

Movie *Movie::operator[](string instanceName) const
{
	return SearchMovieInstance(instanceName);
}

Movie *Movie::SearchMovieInstanceByInstanceId(int instId, bool recursive) const
{
	for (IObject *instance = m_instanceHead; instance;
			instance = instance->linkInstance) {
		if (instance->IsMovie() && instance->instanceId == instId) {
			return (Movie *)instance;
		} else if (recursive && instance->IsMovie()) {
			Movie *i = ((Movie *)instance)->SearchMovieInstanceByInstanceId(
				instId, recursive);
			if (i)
				return i;
		}
	}
	return 0;
}

Button *Movie::SearchButtonInstance(int stringId, bool recursive) const
{
	for (IObject *instance = m_instanceHead; instance;
			instance = instance->linkInstance) {
		if (instance->IsButton() && lwf->GetInstanceNameStringId(
				instance->instanceId) == stringId) {
			return (Button *)instance;
		} else if (recursive && instance->IsMovie()) {
			Button *i = ((Movie *)instance)->SearchButtonInstance(
				stringId, recursive);
			if (i)
				return i;
		}
	}
	return 0;
}

Button *Movie::SearchButtonInstance(string instanceName, bool recursive) const
{
	return SearchButtonInstance(lwf->GetStringId(instanceName), recursive);
}

Button *Movie::SearchButtonInstanceByInstanceId(
	int instId, bool recursive) const
{
	for (IObject *instance = m_instanceHead; instance;
			instance = instance->linkInstance) {
		if (instance->IsButton() && instance->instanceId == instId) {
			return (Button *)instance;
		} else if (recursive && instance->IsMovie()) {
			Button *i = ((Movie *)instance)->SearchButtonInstanceByInstanceId(
				instId, recursive);
			if (i)
				return i;
		}
	}
	return 0;
}

int Movie::AddEventHandler(string eventName, EventHandler eventHandler)
{
	static int id;
	EventHandlerDictionary::iterator it = m_eventHandlers.find(eventName);
	if (it == m_eventHandlers.end()) {
		m_eventHandlers[eventName] = EventHandlerList();
		it = m_eventHandlers.find(eventName);
	}
	it->second.push_back(make_pair(++id, eventHandler));
	return id;
}

class Pred
{
private:
	int id;
public:
	Pred(int i) : id(i) {}
	bool operator()(const pair<int, EventHandler> &h)
	{
		return h.first == id;
	}
};

void Movie::RemoveEventHandler(string eventName, int id)
{
	EventHandlerDictionary::iterator it = m_eventHandlers.find(eventName);
	if (it == m_eventHandlers.end())
		return;

	remove_if(it->second.begin(), it->second.end(), Pred(id));
}

void Movie::ClearEventHandler(string eventName)
{
	m_eventHandlers.erase(eventName);
}

int Movie::SetEventHandler(string eventName, EventHandler eventHandler)
{
	ClearEventHandler(eventName);
	return AddEventHandler(eventName, eventHandler);
}

void Movie::DispatchEvent(string eventName)
{
	if (m_handler.Call(eventName, this))
		return;

	scoped_ptr<EventHandlerList> list(
		new EventHandlerList(m_eventHandlers[eventName]));
	EventHandlerList::iterator it(list->begin()), itend(list->end());
	for (; it != itend; ++it)
		it->second(this, 0);
}

}	// namespace LWF
