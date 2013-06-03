#include "lwf_core.h"
#include "lwf_data.h"
#include "lwf_movie.h"
#include "lwf_renderer.h"
#include "lwf_text.h"

namespace LWF {

Text::Text(LWF *lwf, Movie *p, int objId)
	: Object(lwf, p, Format::Object::TEXT, objId)
{
	const Format::Text &text = lwf->data->texts[objId];
	dataMatrixId = text.matrixId;
	shared_ptr<TextRenderer> textRenderer =
		lwf->rendererFactory->ConstructText(lwf, objId, this);

	string t;
	if (text.stringId != -1)
		t = lwf->data->strings[text.stringId];

	if (text.nameStringId == -1) {
		if (text.stringId != -1)
			textRenderer->SetText(t);
	} else {
		lwf->SetTextRenderer(p->GetFullName(),
			lwf->data->strings[text.nameStringId], t, textRenderer.get());
	}

	renderer = textRenderer;
}

}	// namespace LWF
