LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := lwf-pure2d
LOCAL_SRC_FILES := \
	LWF/core/lwf_animation.cpp \
	LWF/core/lwf_bitmap.cpp \
	LWF/core/lwf_bitmapex.cpp \
	LWF/core/lwf_button.cpp \
	LWF/core/lwf_core.cpp \
	LWF/core/lwf_coredata.cpp \
	LWF/core/lwf_data.cpp \
	LWF/core/lwf_event.cpp \
	LWF/core/lwf_eventbutton.cpp \
	LWF/core/lwf_eventmovie.cpp \
	LWF/core/lwf_format.cpp \
	LWF/core/lwf_graphic.cpp \
	LWF/core/lwf_input.cpp \
	LWF/core/lwf_iobject.cpp \
	LWF/core/lwf_lwfcontainer.cpp \
	LWF/core/lwf_movie.cpp \
	LWF/core/lwf_movieat.cpp \
	LWF/core/lwf_movieop.cpp \
	LWF/core/lwf_movieprop.cpp \
	LWF/core/lwf_object.cpp \
	LWF/core/lwf_particle.cpp \
	LWF/core/lwf_programobj.cpp \
	LWF/core/lwf_property.cpp \
	LWF/core/lwf_text.cpp \
	LWF/core/lwf_utility.cpp \
	LWF/supports/boost/android.cpp \
	LWF/pure2d/lwf_pure2d_bitmap.cpp \
	LWF/pure2d/lwf_pure2d_factory.cpp \
	LWF/pure2d/lwf_pure2d_object.cpp

LOCAL_C_INCLUDES := LWF/supports LWF/core
LOCAL_LDLIBS    := -llog -lGLESv1_CM

include $(BUILD_SHARED_LIBRARY)
