cmake_minimum_required(VERSION 3.4.1)

# Build our own native library
add_library (native-lib SHARED native-lib.cpp LiveEffectEngine.cpp FullDuplexStream.cpp)

# Build the Oboe library
set (OBOE_DIR /Users/Hp/oboe)
add_subdirectory (${OBOE_DIR} ./oboe)

# Make the Oboe public headers available to our app
include_directories (${OBOE_DIR}/include)
include_directories (${OBOE_DIR}/samples/LiveEffect)

# Specify the libraries which our native library is dependent on, including Oboe
target_link_libraries (native-lib log oboe)