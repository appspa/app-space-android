# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# 将当前 "./src/main/cpp" 目录下的所有源文件保存到 "NATIVE_SRC" 中，然后在 add_library 方法调用。
aux_source_directory( . NATIVE_SRC )
# 将 "./src/main/cpp/bzip2" 目录下的子目录bzip2保存到 "BZIP2_BASE" 中，然后在 add_library 方法调用。
aux_source_directory( ./bzip2 BZIP2_BASE )
# 将 BZIP2_BASE 增加到 NATIVE_SRC 中，这样目录的源文件也加入了编译列表中，当然也可以不加到 NATIVE_SRC，直接调用add_library。
list(APPEND NATIVE_SRC ${BZIP2_BASE})

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        patch-lib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ${NATIVE_SRC})

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        patch-lib

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})