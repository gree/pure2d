#include "lwf_data.h"

namespace LWF {
namespace Format {

static string ConvertFilename(const Data *data, int stringId)
{
	const string &s = data->strings[stringId];
	string::size_type pos = s.find_last_of('.');
	if (pos != string::npos)
		return s.substr(0, pos);
	else
		return s;
}

void Texture::SetFilename(const Data *data)
{
	filename = ConvertFilename(data, stringId);
}

const string &Texture::GetFilename(const Data *data) const
{
	return data->strings[stringId];
}

void TextureFragment::SetFilename(const Data *data)
{
	filename = ConvertFilename(data, stringId);
}

const string &TextureFragment::GetFilename(const Data *data) const
{
	return data->strings[stringId];
}

}	// namespace Format
}	// namespace LWF
