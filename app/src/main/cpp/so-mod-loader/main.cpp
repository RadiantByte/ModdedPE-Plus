#include <jni.h>
#include <string>
#include <dirent.h>
#include <dlfcn.h>
#include <sys/stat.h>

static JavaVM* gVm = nullptr;

using LoadFunc = void (*)(JavaVM*);

static bool endsWith(const std::string& s, const char* suf) {
    size_t n = s.size();
    size_t m = strlen(suf);
    return n >= m && s.compare(n - m, m, suf) == 0;
}

extern "C" JNIEXPORT void JNICALL
Java_com_mcal_pesdk_somod_SoModNativeLoader_nativeCallLeviEntry(JNIEnv* env, jclass, jstring cacheDir_) {
    const char* cacheDir = env->GetStringUTFChars(cacheDir_, nullptr);
    std::string modsPath = std::string(cacheDir) + "/mods";

    DIR* dir = opendir(modsPath.c_str());
    if (dir) {
        struct dirent* ent;
        while ((ent = readdir(dir)) != nullptr) {
            std::string name = ent->d_name;
            if (name == "." || name == "..") continue;
            if (!endsWith(name, ".so")) continue;
            std::string path = modsPath + "/" + name;
            void* handle = dlopen(path.c_str(), RTLD_NOW);
            if (handle) {
                LoadFunc func = (LoadFunc)dlsym(handle, "LeviMod_Load");
                if (func) func(gVm);
            }
        }
        closedir(dir);
    }
    env->ReleaseStringUTFChars(cacheDir_, cacheDir);
}

jint JNI_OnLoad(JavaVM* vm, void*) {
    gVm = vm;
    return JNI_VERSION_1_6;
}