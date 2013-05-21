#ifndef LWF_DATA_H
#define	LWF_DATA_H

#include "lwf_format.h"

namespace LWF {

struct Data {
	Format::Header header;
	vector<Translate> translates;
	vector<Matrix> matrices;
	vector<Color> colors;
	vector<AlphaTransform> alphaTransforms;
	vector<ColorTransform> colorTransforms;
	vector<Format::Object> objects;
	vector<Format::Texture> textures;
	vector<Format::TextureFragment> textureFragments;
	vector<Format::Bitmap> bitmaps;
	vector<Format::BitmapEx> bitmapExs;
	vector<Format::Font> fonts;
	vector<Format::TextProperty> textProperties;
	vector<Format::Text> texts;
	vector<Format::ParticleData> particleDatas;
	vector<Format::Particle> particles;
	vector<Format::ProgramObject> programObjects;
	vector<Format::GraphicObject> graphicObjects;
	vector<Format::Graphic> graphics;
	vector<vector<int> > animations;
	vector<Format::ButtonCondition> buttonConditions;
	vector<Format::Button> buttons;
	vector<Format::Label> labels;
	vector<Format::InstanceName> instanceNames;
	vector<Format::Event> events;
	vector<Format::Place> places;
	vector<Format::ControlMoveM> controlMoveMs;
	vector<Format::ControlMoveC> controlMoveCs;
	vector<Format::ControlMoveMC> controlMoveMCs;
	vector<Format::Control> controls;
	vector<Format::Frame> frames;
	vector<Format::MovieClipEvent> movieClipEvents;
	vector<Format::Movie> movies;
	vector<Format::MovieLinkage> movieLinkages;
	vector<string> strings;

	map<string, int> stringMap;
	map<int, int> instanceNameMap;
	map<int, int> eventMap;
	map<int, int> movieLinkageMap;
	map<int, int> movieLinkageNameMap;
	map<int, int> programObjectMap;
	vector<map<int, int> > labelMap;

	string name;
	bool useScript;
	bool useTextureAtlas;

	Data(const void *bytes, size_t length);
	bool Check();
	bool ReplaceTexture(int index,
		const Format::TextureReplacement &textureReplacement);
	bool ReplaceTextureFragment(int index,
		const Format::TextureFragmentReplacement &textureFragmentReplacement);
};

}	// namespace LWF

#endif
