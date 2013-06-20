LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := lwf-pure2d
LOCAL_SRC_FILES := \
	LWF/lwf/cplusplus/core/lwf_animation.cpp \
	LWF/lwf/cplusplus/core/lwf_bitmap.cpp \
	LWF/lwf/cplusplus/core/lwf_bitmapex.cpp \
	LWF/lwf/cplusplus/core/lwf_button.cpp \
	LWF/lwf/cplusplus/core/lwf_core.cpp \
	LWF/lwf/cplusplus/core/lwf_coredata.cpp \
	LWF/lwf/cplusplus/core/lwf_data.cpp \
	LWF/lwf/cplusplus/core/lwf_event.cpp \
	LWF/lwf/cplusplus/core/lwf_eventbutton.cpp \
	LWF/lwf/cplusplus/core/lwf_eventmovie.cpp \
	LWF/lwf/cplusplus/core/lwf_format.cpp \
	LWF/lwf/cplusplus/core/lwf_graphic.cpp \
	LWF/lwf/cplusplus/core/lwf_input.cpp \
	LWF/lwf/cplusplus/core/lwf_iobject.cpp \
	LWF/lwf/cplusplus/core/lwf_lwfcontainer.cpp \
	LWF/lwf/cplusplus/core/lwf_movie.cpp \
	LWF/lwf/cplusplus/core/lwf_movieat.cpp \
	LWF/lwf/cplusplus/core/lwf_movieop.cpp \
	LWF/lwf/cplusplus/core/lwf_movieprop.cpp \
	LWF/lwf/cplusplus/core/lwf_object.cpp \
	LWF/lwf/cplusplus/core/lwf_particle.cpp \
	LWF/lwf/cplusplus/core/lwf_programobj.cpp \
	LWF/lwf/cplusplus/core/lwf_property.cpp \
	LWF/lwf/cplusplus/core/lwf_text.cpp \
	LWF/lwf/cplusplus/core/lwf_utility.cpp \
	LWF/lwf/cplusplus/supports/boost/android.cpp \
	LWF/pure2d/lwf_pure2d_bitmap.cpp \
	LWF/pure2d/lwf_pure2d_factory.cpp \
	LWF/pure2d/lwf_pure2d_object.cpp

LOCAL_C_INCLUDES := LWF/lwf/cplusplus/supports LWF/lwf/cplusplus/core
LOCAL_LDLIBS    := -llog -lGLESv1_CM

include $(BUILD_SHARED_LIBRARY)
