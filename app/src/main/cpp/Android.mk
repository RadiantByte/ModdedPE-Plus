LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE    := nmod-core
LOCAL_SRC_FILES := nmod-core/main.cpp
LOCAL_LDLIBS    := -L$(LOCAL_PATH)/$(TARGET_ARCH_ABI) -llog
TARGET_NO_UNDEFINED_LDFLAGS :=
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := so-mod-loader
LOCAL_SRC_FILES := so-mod-loader/main.cpp
LOCAL_LDLIBS    := -llog -ldl
TARGET_NO_UNDEFINED_LDFLAGS :=
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := launcher-core
LOCAL_SRC_FILES := launcher-core/main.cpp
LOCAL_LDLIBS    := -L$(LOCAL_PATH)/$(TARGET_ARCH_ABI) -llog -ldl
TARGET_NO_UNDEFINED_LDFLAGS :=
include $(BUILD_SHARED_LIBRARY)