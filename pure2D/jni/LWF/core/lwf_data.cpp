#include "lwf_data.h"
#include "lwf_animation.h"

#define	P(n)	advanceP(p, n)
#define	READ(container, what, T)							\
	do {													\
		container.reserve(header.what.length);				\
		for (int i = 0; i < header.what.length; ++i)	{	\
			const T *tp = (const T *)P(sizeof(T));			\
			if (tp >= (const T *)end)						\
				break;										\
			container.push_back(*tp);						\
		}													\
	} while (0)

namespace LWF {

static const char *advanceP(const char *&p, size_t n)
{
	const char *pp = p;
	p += n;
	return pp;
}

static int readInt32(const char *&pp)
{
	const unsigned char *p = (const unsigned char *)pp;
	int v = (p[3] << 24) | (p[2] << 16) | (p[1] <<  8) | (p[0] <<  0);
	pp += 4;
	return v;
}

static vector<int> readAnimation(const char *p, size_t offset, size_t length)
{
	vector<int> array;
	const char *end = p + offset + length;
	p += offset;

	for (;;) {
		if (p >= end)
			return array;

		char code = *p++;
		array.push_back(code);

		switch (code) {
		case Animation::PLAY:
		case Animation::STOP:
		case Animation::NEXTFRAME:
		case Animation::PREVFRAME:
			break;

		case Animation::GOTOFRAME:
		case Animation::GOTOLABEL:
		case Animation::EVENT:
		case Animation::CALL:
			array.push_back(readInt32(p));
			break;

		case Animation::SETTARGET:
			{
				int count = readInt32(p);
				array.push_back(count);
				for (int i = 0; i < count; ++i) {
					int target = readInt32(p);
					array.push_back(target);
				}
			}
			break;

		case Animation::END:
			return array;
		}
	}
}

Data::Data(const void *bytes, size_t length)
{
	if (length < Format::HEADER_SIZE)
		return;

	const char *p = (const char *)bytes;
	const char *end = p + length;
	header = *(const Format::Header *)P(sizeof(Format::Header));
	if (!Check())
		return;

	const char *stringByteData = P(header.stringBytes.length);
	const char *animationByteData = P(header.animationBytes.length);
	vector<Format::String> stringData;
	vector<Format::Animation> animationData;

	READ(translates, translate, Translate);
	READ(matrices, matrix, Matrix);
	READ(colors, color, Color);
	READ(alphaTransforms, alphaTransform, AlphaTransform);
	READ(colorTransforms, colorTransform, ColorTransform);
	READ(objects, objectData, Format::Object);
	READ(textures, texture, Format::TextureBase);
	READ(textureFragments, textureFragment, Format::TextureFragmentBase);
	READ(bitmaps, bitmap, Format::Bitmap);
	READ(bitmapExs, bitmapEx, Format::BitmapEx);
	READ(fonts, font, Format::Font);
	READ(textProperties, textProperty, Format::TextProperty);
	READ(texts, text, Format::Text);
	READ(particleDatas, particleData, Format::ParticleData);
	READ(particles, particle, Format::Particle);
	READ(programObjects, programObject, Format::ProgramObject);
	READ(graphicObjects, graphicObject, Format::GraphicObject);
	READ(graphics, graphic, Format::Graphic);
	READ(animationData, animation, Format::Animation);
	READ(buttonConditions, buttonCondition, Format::ButtonCondition);
	READ(buttons, button, Format::Button);
	READ(labels, label, Format::Label);
	READ(instanceNames, instanceName, Format::InstanceName);
	READ(events, eventData, Format::Event);
	READ(places, place, Format::Place);
	READ(controlMoveMs, controlMoveM, Format::ControlMoveM);
	READ(controlMoveCs, controlMoveC, Format::ControlMoveC);
	READ(controlMoveMCs, controlMoveMC, Format::ControlMoveMC);
	READ(controls, control, Format::Control);
	READ(frames, frame, Format::Frame);
	READ(movieClipEvents, movieClipEvent, Format::MovieClipEvent);
	READ(movies, movie, Format::Movie);
	READ(movieLinkages, movieLinkage, Format::MovieLinkage);
	READ(stringData, stringData, Format::String);

	if (p != end) {
		memset(&header, 0, sizeof(header));
		return;
	}

	strings.reserve(stringData.size());
	vector<Format::String>::const_iterator
		dit(stringData.begin()), ditend(stringData.end());
	for (; dit != ditend; ++dit) {
		string str(stringByteData + dit->stringOffset, dit->stringLength);
		strings.push_back(str);
	}

	animations.reserve(animationData.size());
	vector<Format::Animation>::const_iterator
		ait(animationData.begin()), aitend(animationData.end());
	for (; ait != aitend; ++ait)
		animations.push_back(readAnimation(
			animationByteData, ait->animationOffset, ait->animationLength));

	int i = 0;
	vector<string>::const_iterator sit(strings.begin()), sitend(strings.end());
	for (; sit != sitend; ++sit)
		stringMap[*sit] = i++;

	i = 0;
	vector<Format::InstanceName>::const_iterator
		nit(instanceNames.begin()), nitend(instanceNames.end());
	for (; nit != nitend; ++nit)
		instanceNameMap[nit->stringId] = i++;

	i = 0;
	vector<Format::Event>::const_iterator
		eit(events.begin()), eitend(events.end());
	for (; eit != eitend; ++eit)
		eventMap[eit->stringId] = i++;

	i = 0;
	vector<Format::MovieLinkage>::const_iterator
		lit(movieLinkages.begin()), litend(movieLinkages.end());
	for (; lit != litend; ++lit) {
		movieLinkageMap[lit->stringId] = i++;
		movieLinkageNameMap[lit->movieId] = lit->stringId;
	}

	i = 0;
	vector<Format::ProgramObject>::const_iterator
		oit(programObjects.begin()), oitend(programObjects.end());
	for (; oit != oitend; ++oit)
		programObjectMap[oit->stringId] = i++;

	i = 0;
	labelMap.resize(movies.size());
	vector<Format::Movie>::const_iterator
		mit(movies.begin()), mitend(movies.end());
	for (; mit != mitend; ++mit) {
		int o = mit->labelOffset;
		for (int j = 0; j < mit->labels; ++j) {
			Format::Label l = labels[o + j];
			labelMap[i][l.stringId] = l.frameNo;
		}
		++i;
	}

	vector<Format::Texture>::iterator
		tit(textures.begin()), titend(textures.end());
	for (; tit != titend; ++tit)
		tit->SetFilename(this);
	vector<Format::TextureFragment>::iterator
		fit(textureFragments.begin()), fitend(textureFragments.end());
	for (; fit != fitend; ++fit)
		fit->SetFilename(this);

	name = strings[header.nameStringId];
	useScript = (header.option & Format::OPTION_USE_SCRIPT) != 0;
	useTextureAtlas = (header.option & Format::OPTION_USE_TEXTUREATLAS) != 0;
}

bool Data::Check()
{
	if (header.id0 == 'L' &&
			header.id1 == 'W' &&
			header.id2 == 'F' &&
			header.id3 == Format::FORMAT_TYPE &&
			header.formatVersion0 == Format::FORMAT_VERSION_0 &&
			header.formatVersion1 == Format::FORMAT_VERSION_1 &&
			header.formatVersion2 == Format::FORMAT_VERSION_2) {
		return true;
	}
	return false;
}

bool Data::ReplaceTexture(int index,
	const Format::TextureReplacement &textureReplacement)
{
	if (index < 0 || index >= (int)textures.size())
		return false;

	textures[index] = textureReplacement;
	return true;
}

bool Data::ReplaceTextureFragment(int index,
	const Format::TextureFragmentReplacement &textureFragmentReplacement)
{
	if (index < 0 || index >= (int)textureFragments.size())
		return false;

	textureFragments[index] = textureFragmentReplacement;
	return true;
}

}	// namespace LWF
