#ifndef LWF_CORE_H
#define	LWF_CORE_H

#include "lwf_eventmovie.h"
#include "lwf_eventbutton.h"

namespace LWF {

class Button;
class ButtonEventHandlers;
struct Data;
class IObject;
class IRendererFactory;
class Movie;
class ProgramObject;
class Property;
class Renderer;

typedef function<shared_ptr<Renderer> (ProgramObject *, int, int, int)>
	ProgramObjectConstructor;
typedef map<string, EventHandlerList> GenericEventHandlerDictionary;
typedef map<string, MovieEventHandlers> MovieEventHandlersDictionary;
typedef map<string, ButtonEventHandlers> ButtonEventHandlersDictionary;
typedef map<string, MovieEventHandler> MovieEventHandlerDictionary;
typedef map<string, ButtonEventHandler> ButtonEventHandlerDictionary;
typedef function<void (Movie *)> MovieCommand;
typedef vector<pair<vector<string>, MovieCommand> > MovieCommands;
typedef map<int, bool> AllowButtonList;
typedef map<int, bool> DenyButtonList;
typedef function<void (LWF *)> ExecHandler;
typedef vector<pair<int, ExecHandler> > ExecHandlerList;

class LWF
{
public:
	enum TweenMode {
		TweenModeMovie,
		TweenModeLWF,
	};

	static float ROUND_OFF_TICK_RATE;

public:
	shared_ptr<Data> data;
	shared_ptr<IRendererFactory> rendererFactory;
	shared_ptr<Property> property;
	shared_ptr<Movie> rootMovie;
	Button *focus;
	Button *pressed;
	Button *buttonHead;
	DetachHandler detachHandler;
	Movie *parent;
	string name;
	string attachName;
	int frameRate;
	int execLimit;
	int renderingIndex;
	int renderingIndexOffsetted;
	int renderingCount;
	int depth;
	int execCount;
	int updateCount;
	double time;
	float scaleByStage;
	float tick;
	float thisTick;
	float height;
	float width;
	float pointX;
	float pointY;
	bool interactive;
	bool isExecDisabled;
	bool pressing;
	bool attachVisible;
	bool isPropertyDirty;
	bool isLWFAttached;
	bool interceptByNotAllowOrDenyButtons;
	bool intercepted;
	bool playing;
	void *privateData;

	//TODO
	//TweenMode tweenMode;
	//tweens

private:
	vector<IObject *> m_instances;
	vector<EventHandlerList> m_eventHandlers;
	GenericEventHandlerDictionary m_genericEventHandlerDictionary;
	vector<MovieEventHandlers> m_movieEventHandlers;
	vector<ButtonEventHandlers> m_buttonEventHandlers;
	MovieEventHandlersDictionary m_movieEventHandlersByFullName;
	ButtonEventHandlersDictionary m_buttonEventHandlersByFullName;
	MovieCommands m_movieCommands;
	vector<ProgramObjectConstructor> m_programObjectConstructors;
	AllowButtonList m_allowButtonList;
	DenyButtonList m_denyButtonList;
	ExecHandlerList m_execHandlers;
	float m_progress;
	float m_roundOffTick;
	bool m_executedForExecDisabled;
	Matrix m_matrix;
	Matrix m_matrixIdentity;
	ColorTransform m_colorTransform;
	ColorTransform m_colorTransformIdentity;
	int m_rootMovieStringId;
	int m_eventOffset;

public:
	LWF(shared_ptr<Data> d, shared_ptr<IRendererFactory> rendererFactory);

	void SetRendererFactory(shared_ptr<IRendererFactory> r);
	void SetFrameRate(int f);
	void SetPreferredFrameRate(int f, int eLimit = 2);

	void FitForHeight(float stageWidth, float stageHeight);
	void FitForWidth(float stageWidth, float stageHeight);
	void ScaleForHeight(float stageWidth, float stageHeight);
	void ScaleForWidth(float stageWidth, float stageHeight);

	void RenderOffset();
	void ClearRenderOffset();
	int RenderObject(int count = 1);

	void SetAttachVisible(bool visible);
	void ClearFocus(Button *button);
	void ClearPressed(Button *button);
	void ClearIntercepted();

	void Init();

	int Exec(float tick = 0,
		const Matrix *matrix = 0, const ColorTransform *colorTransform = 0);
	int ForceExec(
		const Matrix *matrix = 0, const ColorTransform *colorTransform = 0);
	int ForceExecWithoutProgress(
		const Matrix *matrix = 0, const ColorTransform *colorTransform = 0);
	void Update(
		const Matrix *matrix = 0, const ColorTransform *colorTransform = 0);
	int Render(int rIndex = 0, int rCount = 0, int rOffset = INT_MIN);
	int Inspect(Inspector inspector, int hierarchy = 0, int inspectDepth = 0,
		int rIndex = 0, int rCount = 0, int rOffset = INT_MIN);

	void Destroy();

	Movie *SearchMovieInstance(int stringId) const;
	Movie *SearchMovieInstance(string instanceName) const;
	Movie *operator[](string instanceName) const;
	Movie *SearchMovieInstanceByInstanceId(int instId) const;

	Button *SearchButtonInstance(int stringId) const;
	Button *SearchButtonInstance(string instanceName) const;
	Button *SearchButtonInstanceByInstanceId(int instId) const;

	IObject *GetInstance(int instId) const;
	void SetInstance(int instId, IObject *instance);

	ProgramObjectConstructor GetProgramObjectConstructor(
		string programObjectName) const;
	ProgramObjectConstructor GetProgramObjectConstructor(
		int programObjectId) const;
	void SetProgramObjectConstructor(string programObjectName,
		ProgramObjectConstructor programObjectConstructor);
	void SetProgramObjectConstructor(int programObjectId,
		ProgramObjectConstructor programObjectConstructor);

	void ExecMovieCommand();
	void SetMovieCommand(vector<string> instanceNames, MovieCommand cmd);

	bool AddAllowButton(string buttonName);
	bool RemoveAllowButton(string buttonName);
	void ClearAllowButton();
	bool AddDenyButton(string buttonName);
	void DenyAllButtons();
	bool RemoveDenyButton(string buttonName);
	void ClearDenyButton();

	void DisableExec();
	void EnableExec();
	void SetPropertyDirty();
	int AddExecHandler(ExecHandler execHandler);
	void RemoveExecHandler(int id);
	void ClearExecHandler();
	int SetExecHandler(ExecHandler execHandler);

	void PlayAnimation(int animationId, Movie *movie, Button *button = 0);

	Button *InputPoint(int px, int py);
	void InputPress();
	void InputRelease();
	void InputKeyPress(int code);

	int GetInstanceNameStringId(int instId) const;
	int GetStringId(string str) const;
	int SearchInstanceId(int stringId) const;
	int SearchFrame(const Movie *movie, string label) const;
	int SearchFrame(const Movie *movie, int stringId) const;
	const map<int, int> *GetMovieLabels(const Movie *movie) const;
	int SearchMovieLinkage(int stringId) const;
	string GetMovieLinkageName(int movieId) const;
	int SearchEventId(string eventName) const;
	int SearchEventId(int stringId) const;
	int SearchProgramObjectId(string programObjectName) const;
	int SearchProgramObjectId(int stringId) const;

	void InitEvent();
	int AddEventHandler(string eventName, EventHandler eventHandler);
	int AddEventHandler(int eventId, EventHandler eventHandler);
	void RemoveEventHandler(string eventName, int id);
	void RemoveEventHandler(int eventId, int id);
	void ClearEventHandler(string eventName);
	void ClearEventHandler(int eventId);
	int SetEventHandler(string eventName, EventHandler eventHandler);
	int SetEventHandler(int eventId, EventHandler eventHandler);
	void DispatchEvent(string eventName, Movie *m, Button *b);
	MovieEventHandlers *GetMovieEventHandlers(const Movie *m);
	int AddMovieEventHandler(
		string instanceName, const MovieEventHandlerDictionary &h);
	int AddMovieEventHandler(
		int instId, const MovieEventHandlerDictionary &h);
	void RemoveMovieEventHandler(string instanceName, int id);
	void RemoveMovieEventHandler(int instId, int id);
	void ClearMovieEventHandler(string instanceName);
	void ClearMovieEventHandler(int instId);
	void ClearMovieEventHandler(string instanceName, string type);
	void ClearMovieEventHandler(int instId, string type);
	void SetMovieEventHandler(
		string instanceName, const MovieEventHandlerDictionary &h);
	void SetMovieEventHandler(int instId, const MovieEventHandlerDictionary &h);
	ButtonEventHandlers *GetButtonEventHandlers(const Button *b);
	int AddButtonEventHandler(string instanceName,
		const ButtonEventHandlerDictionary &h, ButtonKeyPressHandler kh);
	int AddButtonEventHandler(int instId,
		const ButtonEventHandlerDictionary &h, ButtonKeyPressHandler kh);
	void RemoveButtonEventHandler(string instanceName, int id);
	void RemoveButtonEventHandler(int instId, int id);
	void ClearButtonEventHandler(string instanceName);
	void ClearButtonEventHandler(int instId);
	void ClearButtonEventHandler(string instanceName, string type);
	void ClearButtonEventHandler(int instId, string type);
	void SetButtonEventHandler(string instanceName,
		const ButtonEventHandlerDictionary &h, ButtonKeyPressHandler kh);
	void SetButtonEventHandler(int instId,
		const ButtonEventHandlerDictionary &h, ButtonKeyPressHandler kh);
	void ClearAllEventHandlers();

private:
	const Matrix *CalcMatrix(const Matrix *matrix);
	const ColorTransform *CalcColorTransform(
		const ColorTransform *colorTransform);
};

}	// namespace LWF

#endif
