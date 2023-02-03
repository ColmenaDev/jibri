/*
 * Copyright @ 2018 Atlassian Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.jitsi.jibri.capture.ffmpeg

import org.jitsi.jibri.sink.Sink

fun getFfmpegCommandLinux(ffmpegExecutorParams: FfmpegExecutorParams, sink: Sink): List<String> {
    return listOf(
        "ffmpeg", "-y", "-v", "info",
        "-f", ffmpegExecutorParams.audioSource,
        "-i", ffmpegExecutorParams.audioDevice,
//      "-c:a", "aac", "-ar", "44100", "-b:a", "128k",
        "-c:a", "libmp3lame", "-ar", "44100", "-b:a", "128k","-af", "aresample=async=1",
       sink.path, "icecast://source:hackme@colmena.media:8000/"+sink.path.substringAfterLast("/"),
//      "-f", "tee", "-map", "0:a", "\""+sink.path+"|[onfail=ignore]icecast://source:hackme@streaming.colmena.media:8000/"+sink.path.substringAfterLast("/")+"\"",
    )}


/**
 * Mac support is not officially supported, and only exists to make development of Jibri on Mac easier.
 * Certain settings (for example [FfmpegExecutorParams.audioDevice] and [FfmpegExecutorParams.audioSource]
 * may not be used here as they are designed around Linux)
 */
fun getFfmpegCommandMac(ffmpegExecutorParams: FfmpegExecutorParams, sink: Sink): List<String> {
    return listOf(
        "ffmpeg", "-y", "-v", "info",
        "-thread_queue_size", ffmpegExecutorParams.queueSize.toString(),
        "-f", "avfoundation",
        "-framerate", ffmpegExecutorParams.framerate.toString(),
        "-video_size", ffmpegExecutorParams.resolution,
        // Note the values passed here will need to be changed based on the output of
        // ffmpeg -f avfoundation -list_devices true -i ""
        "-i", "0:0",
        "-vsync", "2",
        "-acodec", "aac", "-strict", "-2", "-ar", "44100", "-b:a", "128k",
        "-c:v", "libx264", "-preset", ffmpegExecutorParams.videoEncodePreset,
        *sink.options, "-pix_fmt", "yuv420p", "-crf", ffmpegExecutorParams.h264ConstantRateFactor.toString(),
        "-g", ffmpegExecutorParams.gopSize.toString(), "-tune", "zerolatency",
        "-f", sink.format, sink.path
    )
}
