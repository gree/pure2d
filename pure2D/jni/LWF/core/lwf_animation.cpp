#include "lwf_animation.h"
#include "lwf_button.h"
#include "lwf_core.h"
#include "lwf_data.h"
#include "lwf_movie.h"

namespace LWF {

void LWF::PlayAnimation(int animationId, Movie *movie, Button *button)
{
	int i = 0;
	const vector<int> &animations = data->animations[animationId];
	Movie *target = movie;

	for (;;) {
		switch (animations[i++]) {
		case Animation::END:
			return;

		case Animation::PLAY:
			target->Play();
			break;

		case Animation::STOP:
			target->Stop();
			break;

		case Animation::NEXTFRAME:
			target->NextFrame();
			break;

		case Animation::PREVFRAME:
			target->PrevFrame();
			break;

		case Animation::GOTOFRAME:
			target->GotoFrameInternal(animations[i++]);
			break;

		case Animation::GOTOLABEL:
			target->GotoFrame(SearchFrame(target, animations[i++]));
			break;

		case Animation::SETTARGET:
			{
				target = movie;

				int count = animations[i++];
				if (count == 0)
					break;

				for (int j = 0; j < count; ++j) {
					int instId = animations[i++];

					switch (instId) {
					case Animation::INSTANCE_TARGET_ROOT:
						target = rootMovie.get();
						break;

					case Animation::INSTANCE_TARGET_PARENT:
						target = target->parent;
						if (!target)
							target = rootMovie.get();
						break;

					default:
						{
							target = target->SearchMovieInstanceByInstanceId(
								instId, false);
							if (!target)
								target = movie;
							break;
						}
					}
				}
			}
			break;

		case Animation::EVENT:
			{
				int eventId = animations[i++];
				EventHandlerList &v(m_eventHandlers[eventId]);
				EventHandlerList::iterator it(v.begin()), itend(v.end());
				for (; it != itend; ++it)
					it->second(movie, button);
			}
			break;

		case Animation::CALL:
			i++;
			break;
		}
	}
}

}	// namespace LWF
