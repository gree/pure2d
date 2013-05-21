#include "lwf_movie.h"
#include "lwf_property.h"
#include "lwf_utility.h"

namespace LWF {

float Movie::GetX() const
{
	if (m_property->hasMatrix)
		return m_property->matrix.translateX;
	else
		return Utility::GetX(this);
}

void Movie::SetX(float value)
{
	if (!m_property->hasMatrix)
		Utility::SyncMatrix(this);
	m_property->MoveTo(value, m_property->matrix.translateY);
}

float Movie::GetY() const
{
	if (m_property->hasMatrix)
		return m_property->matrix.translateY;
	else
		return Utility::GetY(this);
}

void Movie::SetY(float value)
{
	if (!m_property->hasMatrix)
		Utility::SyncMatrix(this);
	m_property->MoveTo(m_property->matrix.translateX, value);
}

float Movie::GetScaleX() const
{
	if (m_property->hasMatrix)
		return m_property->scaleX;
	else
		return Utility::GetScaleX(this);
}

void Movie::SetScaleX(float value)
{
	if (!m_property->hasMatrix)
		Utility::SyncMatrix(this);
	m_property->ScaleTo(value, m_property->scaleY);
}

float Movie::GetScaleY() const
{
	if (m_property->hasMatrix)
		return m_property->scaleY;
	else
		return Utility::GetScaleY(this);
}

void Movie::SetScaleY(float value)
{
	if (!m_property->hasMatrix)
		Utility::SyncMatrix(this);
	m_property->ScaleTo(m_property->scaleX, value);
}

float Movie::GetRotation() const
{
	if (m_property->hasMatrix)
		return m_property->rotation;
	else
		return Utility::GetRotation(this);
}

void Movie::SetRotation(float value)
{
	if (!m_property->hasMatrix)
		Utility::SyncMatrix(this);
	m_property->RotateTo(value);
}

float Movie::GetAlpha() const
{
	if (m_property->hasColorTransform)
		return m_property->colorTransform.multi.alpha;
	else
		return Utility::GetAlpha(this);
}

void Movie::SetAlpha(float value)
{
	if (!m_property->hasColorTransform)
		Utility::SyncColorTransform(this);
	m_property->SetAlpha(value);
}

float Movie::GetRed() const
{
	if (m_property->hasColorTransform)
		return m_property->colorTransform.multi.red;
	else
		return Utility::GetRed(this);
}

void Movie::SetRed(float value)
{
	if (!m_property->hasColorTransform)
		Utility::SyncColorTransform(this);
	m_property->SetRed(value);
}

float Movie::GetGreen() const
{
	if (m_property->hasColorTransform)
		return m_property->colorTransform.multi.green;
	else
		return Utility::GetGreen(this);
}

void Movie::SetGreen(float value)
{
	if (!m_property->hasColorTransform)
		Utility::SyncColorTransform(this);
	m_property->SetGreen(value);
}

float Movie::GetBlue() const
{
	if (m_property->hasColorTransform)
		return m_property->colorTransform.multi.blue;
	else
		return Utility::GetBlue(this);
}

void Movie::SetBlue(float value)
{
	if (!m_property->hasColorTransform)
		Utility::SyncColorTransform(this);
	m_property->SetBlue(value);
}

}	// namespace LWF
