#include "lwf_core.h"
#include "lwf_movie.h"
#include "lwf_property.h"
#include "lwf_utility.h"

namespace LWF {

Movie *Movie::Play()
{
	playing = true;
	return this;
}

Movie *Movie::Stop()
{
	playing = false;
	return this;
}

Movie *Movie::NextFrame()
{
	m_jumped = true;
	Stop();
	++m_currentFrameInternal;
	currentFrame = m_currentFrameInternal + 1;
	return this;
}

Movie *Movie::PrevFrame()
{
	m_jumped = true;
	Stop();
	--m_currentFrameInternal;
	currentFrame = m_currentFrameInternal + 1;
	return this;
}

Movie *Movie::GotoFrame(int frameNo)
{
	GotoFrameInternal(frameNo - 1);
	return this;
}

Movie *Movie::GotoFrameInternal(int frameNo)
{
	m_jumped = true;
	Stop();
	m_currentFrameInternal = frameNo;
	currentFrame = m_currentFrameInternal + 1;
	return this;
}

Movie *Movie::SetVisible(bool v)
{
	visible = v;
	lwf->SetPropertyDirty();
	return this;
}

Movie *Movie::GotoLabel(string label)
{
	GotoLabel(lwf->GetStringId(label));
	return this;
}

Movie *Movie::GotoLabel(int stringId)
{
	GotoFrame(lwf->SearchFrame(this, stringId));
	return this;
}

Movie *Movie::GotoAndStop(string label)
{
	GotoFrame(lwf->SearchFrame(this, lwf->GetStringId(label)));
	Stop();
	return this;
}

Movie *Movie::GotoAndStop(int frameNo)
{
	GotoFrame(frameNo);
	Stop();
	return this;
}

Movie *Movie::GotoAndPlay(string label)
{
	GotoFrame(lwf->SearchFrame(this, lwf->GetStringId(label)));
	Play();
	return this;
}

Movie *Movie::GotoAndPlay(int frameNo)
{
	GotoFrame(frameNo);
	Play();
	return this;
}

Movie *Movie::Move(float vx, float vy)
{
	if (!m_property->hasMatrix)
		Utility::SyncMatrix(this);
	m_property->Move(vx, vy);
	return this;
}

Movie *Movie::MoveTo(float vx, float vy)
{
	if (!m_property->hasMatrix)
		Utility::SyncMatrix(this);
	m_property->MoveTo(vx, vy);
	return this;
}

Movie *Movie::Rotate(float degree)
{
	if (!m_property->hasMatrix)
		Utility::SyncMatrix(this);
	m_property->Rotate(degree);
	return this;
}

Movie *Movie::RotateTo(float degree)
{
	if (!m_property->hasMatrix)
		Utility::SyncMatrix(this);
	m_property->RotateTo(degree);
	return this;
}

Movie *Movie::Scale(float vx, float vy)
{
	if (!m_property->hasMatrix)
		Utility::SyncMatrix(this);
	m_property->Scale(vx, vy);
	return this;
}

Movie *Movie::ScaleTo(float vx, float vy)
{
	if (!m_property->hasMatrix)
		Utility::SyncMatrix(this);
	m_property->ScaleTo(vx, vy);
	return this;
}

Movie *Movie::SetMatrix(const Matrix *m, float sx, float sy, float r)
{
	m_property->SetMatrix(m, sx, sy, r);
	return this;
}

Movie *Movie::SetAlphaValue(float v)
{
	if (!m_property->hasColorTransform)
		Utility::SyncColorTransform(this);
	m_property->SetAlpha(v);
	return this;
}

Movie *Movie::SetColorTransform(const ColorTransform *c)
{
	m_property->SetColorTransform(c);
	return this;
}

Movie *Movie::SetRenderingOffset(int rOffset)
{
	m_property->SetRenderingOffset(rOffset);
	return this;
}

}	// namespace LWF
