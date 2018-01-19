package com.example.pappu.memotape.capture;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaMuxer;
import android.net.Uri;
import android.util.Log;

import com.example.pappu.memotape.Utility.FileUtils;

import java.io.IOException;
import java.nio.ByteBuffer;




/**
 * Created by pappu on 4/1/17.
 */

public class AudioVedioMerger {
    private static final int DEFAULT_BUFFER_SIZE = 1 * 1024 * 1024;
    private MediaMuxer mergMuxer;

    private Context context;
    String TAG = "AudioVedioMerger";

    public AudioVedioMerger(Context context){
        this.context = context;
    }


    public void mergeAudioWithCapturedVideo(Uri audioSourcePath, String destinationPath) throws IOException {
        mergMuxer = new MediaMuxer(destinationPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        String tempVideoPath  = FileUtils.getTemprorayVideoFilePath();

        //use one of overloaded setDataSource() functions to set your data source
        retriever.setDataSource(tempVideoPath);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time );


        MediaExtractor videoExtractor = new MediaExtractor();
        MediaExtractor audioExtractor = new MediaExtractor();
        if (audioSourcePath != null)
        audioExtractor.setDataSource(context,audioSourcePath,null);
        videoExtractor.setDataSource(tempVideoPath);
        MediaFormat audioFormat =  audioExtractor.getTrackFormat(0);
        MediaFormat videoFormat = videoExtractor.getTrackFormat(0);
        int bufferSize = 0;


//        audio extractor
        audioExtractor.selectTrack(0);
        if (audioFormat.containsKey(MediaFormat.KEY_MAX_INPUT_SIZE)) {
            int newSize = audioFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
            bufferSize = newSize > bufferSize ? newSize : bufferSize;
        }
        if (bufferSize < 0) {
            bufferSize = DEFAULT_BUFFER_SIZE;
        }
        int audioTrackIndex = mergMuxer.addTrack(audioFormat);
        int videoTrackIndex = mergMuxer.addTrack(videoFormat);

        mergMuxer.start();
        writeIntoMuxer(audioExtractor,audioTrackIndex,bufferSize,timeInMillisec);

        videoExtractor.selectTrack(0);
        if (videoFormat.containsKey(MediaFormat.KEY_MAX_INPUT_SIZE)) {
            int newSize = videoFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
            bufferSize = newSize > bufferSize ? newSize : bufferSize;
        }
        if (bufferSize < 0) {
            bufferSize = DEFAULT_BUFFER_SIZE;
        }
        writeIntoMuxer(videoExtractor,videoTrackIndex,bufferSize,timeInMillisec);
        mergMuxer.stop();
        mergMuxer.release();
        mergMuxer = null;
    }

    private void writeIntoMuxer(MediaExtractor extractor, int trackIndex, int bufferSize, long endMs) {
        int offset = 0;
        ByteBuffer dstBuf = ByteBuffer.allocate(bufferSize);
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        while (true) {
            bufferInfo.offset = offset;
            bufferInfo.size = extractor.readSampleData(dstBuf, offset);
            if (bufferInfo.size < 0) {
                Log.d(TAG, "Saw input EOS.");
                bufferInfo.size = 0;
                break;
            }
            else {
                bufferInfo.presentationTimeUs = extractor.getSampleTime();
                if (endMs > 0 && bufferInfo.presentationTimeUs > (endMs*1000)) {
                    Log.d(TAG, "The current sample is over the trim end time.");
                    break;
                }
                else {
                    bufferInfo.presentationTimeUs = extractor.getSampleTime();
                    bufferInfo.flags = extractor.getSampleFlags();
                    mergMuxer.writeSampleData(trackIndex, dstBuf, bufferInfo);
                    extractor.advance();
                }
            }
        }
    }

}
